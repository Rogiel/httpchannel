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
package com.rogiel.httpchannel.channel;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;

import com.rogiel.httpchannel.service.DownloadChannel;
import com.rogiel.httpchannel.service.DownloadService;
import com.rogiel.httpchannel.service.Downloader;
import com.rogiel.httpchannel.service.DownloaderCapability;

/**
 * Creates a pseudo-seekable {@link DownloadChannel}. This implementations opens
 * a new connection on every call to {@link #position(long)} and thus might
 * consume a lot of bandwidth to start downloads. Also, some services do not
 * support download resuming, those services are not supported by
 * {@link SeekableDownloadChannel}.
 * <p>
 * You can use {@link #isSupported(DownloadChannel)} or
 * {@link #isSupported(DownloadService)} to check whether an channel or a
 * service is supported.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @see SeekableDownloadChannel#isSupported(DownloadChannel)
 * @see SeekableDownloadChannel#isSupported(DownloadService))
 */
public class SeekableDownloadChannel implements DownloadChannel,
		SeekableByteChannel {
	/**
	 * The current opened channel.
	 * <p>
	 * This channel will be swapped at every call to {@link #position(long)}.
	 */
	private DownloadChannel channel;
	/**
	 * The current channel position
	 */
	private long position = 0;

	/**
	 * Creates a new {@link SeekableDownloadChannel} using an base
	 * {@link DownloadChannel}
	 * 
	 * @param channel
	 *            the base {@link DownloadChannel}
	 * @throws IOException
	 *             if the channel is not supported
	 * @see SeekableDownloadChannel#isSupported(DownloadChannel)
	 */
	private SeekableDownloadChannel(DownloadChannel channel,
			boolean closeIfNotSupported) throws IOException {
		if (!isSupported(channel)) {
			if (closeIfNotSupported)
				channel.close();
			throw new IOException("This channel is not supported");
		}
		this.channel = channel;
	}

	/**
	 * Creates a new {@link SeekableDownloadChannel} using an base
	 * {@link DownloadChannel}. If not supported, an {@link IOException} is
	 * thrown and the channel is <b>not</b> closed.
	 * 
	 * @param channel
	 *            the base {@link DownloadChannel}
	 * @throws IOException
	 *             if the channel is not supported
	 */
	public SeekableDownloadChannel(DownloadChannel channel) throws IOException {
		this(channel, false);
	}

	/**
	 * Creates a new {@link SeekableDownloadChannel} using an base
	 * {@link Downloader}
	 * 
	 * @param downloader
	 *            the base {@link Downloader}
	 * @throws IOException
	 *             if any exception occur while opening the channel or if the
	 *             channel is not supported
	 */
	public SeekableDownloadChannel(Downloader<?> downloader) throws IOException {
		this(downloader.openChannel(), true);
	}

	/**
	 * Creates a new {@link SeekableDownloadChannel} using an base
	 * {@link DownloadService} and an {@link URI}.
	 * 
	 * @param service
	 *            the base {@link DownloadService}
	 * @param uri
	 *            the base {@link URI}
	 * @throws IOException
	 *             if any exception occur while opening the channel or if the
	 *             channel is not supported
	 */
	public SeekableDownloadChannel(DownloadService<?> service, URI uri)
			throws IOException {
		this(service.getDownloader(uri));
	}

	@Override
	public long size() throws IOException {
		return channel.size();
	}

	@Override
	public String filename() throws IOException {
		return channel.filename();
	}

	@Override
	public DownloadService<?> getService() {
		return channel.getService();
	}

	@Override
	public Downloader<?> getDownloader() {
		return channel.getDownloader();
	}

	@Override
	public boolean isOpen() {
		return channel.isOpen();
	}

	@Override
	public void close() throws IOException {
		channel.close();
		channel = null;
		position = 0;
	}

	@Override
	public int read(ByteBuffer dst) throws IOException {
		int read = channel.read(dst);
		position += read;
		return read;
	}

	/**
	 * This implementation always throws an {@link NonWritableChannelException}.
	 * <p>
	 * 
	 * {@inheritDoc}
	 * 
	 * @throws NonWritableChannelException
	 *             always (download channels are read-only)
	 */
	@Override
	public int write(ByteBuffer src) throws IOException {
		throw new NonWritableChannelException();
	}

	@Override
	public long position() throws IOException {
		return position;
	}

	@Override
	public SeekableDownloadChannel position(long newPosition)
			throws IOException {
		// closes the current channel
		channel.close();
		// now open a new channel
		this.position = newPosition;
		channel = channel.getDownloader().openChannel(position);
		return this;
	}

	/**
	 * Always throws an {@link NonWritableChannelException}
	 * 
	 * @throws NonWritableChannelException
	 *             always (download channels are read-only)
	 */
	@Override
	public SeekableDownloadChannel truncate(long size) throws IOException {
		throw new NonWritableChannelException();
	}

	/**
	 * @return the current {@link DownloadChannel}
	 */
	public DownloadChannel channel() {
		return channel;
	}

	/**
	 * Checks whether the given <code>service</code> supports
	 * {@link SeekableDownloadChannel}
	 * 
	 * @param service
	 *            the service
	 * @return <code>true</code> if the service is supported
	 */
	public static boolean isSupported(DownloadService<?> service) {
		switch (service.getServiceMode()) {
		case UNAUTHENTICATED:
			return service.getDownloadCapabilities().has(
					DownloaderCapability.UNAUTHENTICATED_RESUME);
		case NON_PREMIUM:
			return service.getDownloadCapabilities().has(
					DownloaderCapability.NON_PREMIUM_ACCOUNT_RESUME);
		case PREMIUM:
			return service.getDownloadCapabilities().has(
					DownloaderCapability.PREMIUM_ACCOUNT_RESUME);
		default:
			return false;
		}
	}

	/**
	 * Checks whether the given <code>channel</code> supports
	 * {@link SeekableDownloadChannel}
	 * 
	 * @param channel
	 *            the channelO
	 * @return <code>true</code> if the channel is supported
	 */
	public static boolean isSupported(DownloadChannel channel) {
		return isSupported(channel.getService());
	}
}
