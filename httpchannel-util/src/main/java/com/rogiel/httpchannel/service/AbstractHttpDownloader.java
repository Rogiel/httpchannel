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
import java.net.URI;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rogiel.httpchannel.http.Request;
import com.rogiel.httpchannel.service.DownloadListener;
import com.rogiel.httpchannel.service.DownloadService;
import com.rogiel.httpchannel.service.Downloader;
import com.rogiel.httpchannel.service.Downloader.DownloaderConfiguration;
import com.rogiel.httpchannel.service.channel.InputStreamDownloadChannel;
import com.rogiel.httpchannel.service.exception.DownloadLinkNotFoundException;
import com.rogiel.httpchannel.util.ThreadUtils;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public abstract class AbstractHttpDownloader<C extends DownloaderConfiguration>
		extends AbstractDownloader<C> implements Downloader<C> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected AbstractHttpDownloader(DownloadService<?> service, URI uri,
			C configuration) {
		super(service, uri, configuration);
	}

	protected long getContentLength(HttpResponse response) {
		final Header contentLengthHeader = response
				.getFirstHeader("Content-Length");
		long contentLength = -1;
		if (contentLengthHeader != null) {
			contentLength = Long.valueOf(contentLengthHeader.getValue());
		}
		return contentLength;
	}

	protected void timer(DownloadListener listener, long timer) {
		if (listener != null) {
			if (!listener.timer(timer))
				return;
		}
		logger.debug("Download timer waiting {}", timer);
		ThreadUtils.sleep(timer);
	}

	protected InputStreamDownloadChannel download(Request request)
			throws IOException {
		final HttpResponse response = request.request();
		if (!(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK
				|| response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_MODIFIED || response
				.getStatusLine().getStatusCode() == HttpStatus.SC_PARTIAL_CONTENT))
			throw new DownloadLinkNotFoundException();

		final String filename = FilenameUtils.getName(request.getURI());
		final long contentLength = getContentLength(response);
		return createInputStreamChannel(response.getEntity().getContent(),
				contentLength, filename);
	}
}
