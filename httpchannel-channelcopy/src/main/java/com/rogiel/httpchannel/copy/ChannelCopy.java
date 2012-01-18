/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.rogiel.httpchannel.copy;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import com.rogiel.httpchannel.copy.exception.NoServiceFoundException;
import com.rogiel.httpchannel.service.DownloadChannel;
import com.rogiel.httpchannel.service.DownloadService;
import com.rogiel.httpchannel.service.UploadChannel;
import com.rogiel.httpchannel.service.UploadService;
import com.rogiel.httpchannel.service.Uploader.UploaderConfiguration;
import com.rogiel.httpchannel.service.exception.DownloadLimitExceededException;
import com.rogiel.httpchannel.service.exception.DownloadLinkNotFoundException;
import com.rogiel.httpchannel.service.exception.DownloadNotAuthorizedException;
import com.rogiel.httpchannel.service.exception.UploadLinkNotFoundException;
import com.rogiel.httpchannel.service.helper.Services;

/**
 * This class provides an utility that copies the entire content of a
 * {@link ReadableByteChannel} (this can be an {@link DownloadChannel} or any
 * {@link Files#newByteChannel(Path, java.nio.file.OpenOption...)} opened with
 * {@link StandardOpenOption#READ READ}.
 * <p>
 * The input channel must be created or provided at construction time, but
 * several output channels can be added through
 * {@link #addOutput(UploadChannel)}, {@link #addOutput(UploadService)} or
 * {@link #addOutput(UploadService, UploaderConfiguration)}.
 * <p>
 * Once all output channels were set, {@link #call()} must be called in order to
 * start copying data. This class implements {@link Callable} and thus can be
 * executed inside an {@link Executor}.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class ChannelCopy implements Callable<List<URI>> {
	/**
	 * The input channel
	 */
	private final ReadableByteChannel downloadChannel;
	/**
	 * The filename
	 */
	private final String filename;
	/**
	 * The filesise
	 */
	private final long filesize;

	/**
	 * The list of all channels to write data to
	 */
	private final List<UploadChannel> uploadChannels = new ArrayList<>();

	/**
	 * Initializes with an {@link ReadableByteChannel}, filename and filesize
	 * 
	 * @param channel
	 *            the channel
	 * @param filename
	 *            the file name
	 * @param filesize
	 *            the file size
	 */
	public ChannelCopy(ReadableByteChannel channel, String filename,
			long filesize) {
		this.downloadChannel = channel;
		this.filename = filename;
		this.filesize = filesize;
	}

	/**
	 * Initializes with a {@link Path}. Will open a new channel.
	 * 
	 * @param path
	 * @throws IOException
	 */
	public ChannelCopy(Path path) throws IOException {
		this(Files.newByteChannel(path, StandardOpenOption.READ), path
				.getFileName().toString(), Files.size(path));
	}

	/**
	 * Initializes with a {@link DownloadChannel}
	 * 
	 * @param downloadChannel
	 *            the download channel
	 * @throws IOException
	 *             if any {@link IOException} occur
	 */
	public ChannelCopy(DownloadChannel downloadChannel) throws IOException {
		this.downloadChannel = downloadChannel;
		this.filename = downloadChannel.filename();
		this.filesize = downloadChannel.size();
	}

	/**
	 * Initializes with an {@link URI}. First tries to open an
	 * {@link DownloadChannel}, if no service is found,
	 * {@link NoServiceFoundException} is thrown.
	 * 
	 * @param uri
	 *            the source {@link URI}
	 * @throws DownloadLinkNotFoundException
	 *             if the download link could not be found
	 * @throws DownloadLimitExceededException
	 *             if the download limit has been exceeded
	 * @throws DownloadNotAuthorizedException
	 *             if the download was not authorized by the service
	 * @throws NoServiceFoundException
	 *             if no service could be found for the {@link URI}
	 * @throws IOException
	 *             if any IO error occur
	 */
	public ChannelCopy(URI uri) throws DownloadLinkNotFoundException,
			DownloadLimitExceededException, DownloadNotAuthorizedException,
			IOException {
		final DownloadService<?> service = Services.matchURI(uri);
		if (service == null)
			throw new NoServiceFoundException(uri.toString());
		final DownloadChannel downloadChannel = service.getDownloader(uri)
				.openChannel();

		this.downloadChannel = downloadChannel;
		this.filename = downloadChannel.filename();
		this.filesize = downloadChannel.size();
	}

	/**
	 * Adds a new output channel in which data should be written
	 * 
	 * @param channel
	 *            the channel
	 */
	public void addOutput(UploadChannel channel) {
		uploadChannels.add(channel);
	}

	/**
	 * Adds a new output in which data should be written. Creates a new
	 * {@link UploadChannel} based on the {@link UploadService}.
	 * 
	 * @param service
	 *            the upload service
	 * @throws IOException
	 *             if any IO error occur
	 */
	public void addOutput(UploadService<?> service) throws IOException {
		addOutput(service.getUploader(filename, filesize).openChannel());
	}

	/**
	 * Adds a new output in which data should be written. Creates a new
	 * {@link UploadChannel} based on the {@link UploadService}.
	 * 
	 * @param service
	 *            the upload service
	 * @param configuration
	 *            the uploader configuration
	 * @throws IOException
	 *             if any IO error occur
	 */
	public <S extends UploadService<C>, C extends UploaderConfiguration> void addOutput(
			S service, C configuration) throws IOException {
		addOutput(service.getUploader(filename, filesize, configuration)
				.openChannel());
	}

	@Override
	public List<URI> call() throws IOException {
		final ByteBuffer buffer = ByteBuffer.allocate(16 * 1024);
		try {
			while (downloadChannel.read(buffer) >= 0) {
				buffer.flip();
				final int limit = buffer.limit();
				final int position = buffer.position();
				for (final UploadChannel channel : uploadChannels) {
					channel.write(buffer);
					buffer.limit(limit).position(position);
				}
				buffer.clear();
			}
		} finally {
			downloadChannel.close();
			for (final UploadChannel channel : uploadChannels) {
				try {
					channel.close();
				} catch (UploadLinkNotFoundException e) {
				}
			}
		}

		final List<URI> uris = new ArrayList<>();
		for (final UploadChannel channel : uploadChannels) {
			uris.add(channel.getDownloadLink());
		}

		return uris;
	}
}
