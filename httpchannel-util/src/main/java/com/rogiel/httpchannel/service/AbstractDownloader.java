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

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import com.rogiel.httpchannel.util.ThreadUtils;

/**
 * @author rogiel
 */
public abstract class AbstractDownloader implements Downloader {
	protected int parseTimer(String stringTimer) {
		int timer = 0;
		if (stringTimer != null && stringTimer.length() > 0) {
			timer = Integer.parseInt(stringTimer);
		}
		return timer;
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
			listener.timer(timer);
		}
		ThreadUtils.sleep(timer);
	}
}
