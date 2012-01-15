/*
 * This file is part of seedbox <github.com/seedbox>.
 *
 * seedbox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * seedbox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with seedbox.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.rogiel.httpchannel.service;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import com.rogiel.httpchannel.http.Request;
import com.rogiel.httpchannel.service.Downloader.DownloaderConfiguration;
import com.rogiel.httpchannel.service.channel.InputStreamDownloadChannel;
import com.rogiel.httpchannel.service.exception.DownloadLinkNotFoundException;
import com.rogiel.httpchannel.util.ThreadUtils;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public abstract class AbstractHttpDownloader<C extends DownloaderConfiguration>
		extends AbstractDownloader<C> implements Downloader<C> {
	protected AbstractHttpDownloader(URL url, C configuration) {
		super(url, configuration);
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
		ThreadUtils.sleep(timer);
	}

	protected InputStreamDownloadChannel download(Request request)
			throws IOException {
		final HttpResponse response = request.request();
		if (!(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK
				|| response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_MODIFIED || response
				.getStatusLine().getStatusCode() == HttpStatus.SC_PARTIAL_CONTENT))
			throw new DownloadLinkNotFoundException();

		final String filename = FilenameUtils.getName(request.getURL());
		final long contentLength = getContentLength(response);
		return createInputStreamChannel(response.getEntity().getContent(),
				contentLength, filename);
	}
}
