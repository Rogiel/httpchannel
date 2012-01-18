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
package com.rogiel.httpchannel.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import com.rogiel.httpchannel.service.Downloader.DownloaderConfiguration;
import com.rogiel.httpchannel.service.channel.InputStreamDownloadChannel;
import com.rogiel.httpchannel.service.exception.DownloadLimitExceededException;
import com.rogiel.httpchannel.service.exception.DownloadLinkNotFoundException;
import com.rogiel.httpchannel.service.exception.DownloadNotAuthorizedException;
import com.rogiel.httpchannel.service.exception.DownloadNotResumableException;

/**
 * An abstract {@link Downloader} that implements most of the general-purpose
 * methods
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 * @param <C>
 *            the {@link Downloader} configuration object type
 */
public abstract class AbstractDownloader<C extends DownloaderConfiguration>
		implements Downloader<C> {
	protected final DownloadService<?> service;
	/**
	 * The download URI
	 */
	protected final URI uri;

	/**
	 * The {@link Downloader} configuration
	 */
	protected final C configuration;

	/**
	 * Creates a new instance
	 * 
	 * @param service
	 *            the download service
	 * @param uri
	 *            the download uri
	 * @param configuration
	 *            the configuration object
	 */
	protected AbstractDownloader(DownloadService<?> service, URI uri,
			C configuration) {
		this.service = service;
		this.uri = uri;
		this.configuration = configuration;
	}

	@Override
	public DownloadChannel openChannel(long position) throws IOException,
			DownloadLinkNotFoundException, DownloadLimitExceededException,
			DownloadNotAuthorizedException, DownloadNotResumableException {
		return openChannel(null, position);
	}

	@Override
	public DownloadChannel openChannel(DownloadListener listener)
			throws IOException, DownloadLinkNotFoundException,
			DownloadLimitExceededException, DownloadNotAuthorizedException,
			DownloadNotResumableException {
		return openChannel(listener, 0);
	}

	@Override
	public DownloadChannel openChannel() throws IOException,
			DownloadLinkNotFoundException, DownloadLimitExceededException,
			DownloadNotAuthorizedException, DownloadNotResumableException {
		return openChannel(null, 0);
	}

	protected InputStreamDownloadChannel createInputStreamChannel(
			InputStream in, long length, String filename) {
		return new InputStreamDownloadChannel(service, this, in, length,
				filename);
	}

	@Override
	public C getConfiguration() {
		return configuration;
	}
}
