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

import java.net.URL;
import java.util.concurrent.Future;

import com.rogiel.httpchannel.http.GetRequest;
import com.rogiel.httpchannel.http.HttpContext;
import com.rogiel.httpchannel.http.PostMultipartRequest;
import com.rogiel.httpchannel.http.PostRequest;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel;
import com.rogiel.httpchannel.util.ThreadUtils;

/**
 * Abstract base service for HTTP enabled services.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @since 1.0
 */
public abstract class AbstractHttpService extends AbstractService implements
		Service {
	protected final HttpContext http = new HttpContext();

	protected LinkedUploadChannel waitChannelLink(LinkedUploadChannel channel,
			Future<?> future) {
		logger.debug("Waiting channel {} to link", channel);
		while (!channel.isLinked() && !future.isDone()) {
			ThreadUtils.sleep(100);
		}
		return channel;
	}

	public GetRequest get(String url) {
		return http.get(url);
	}

	public GetRequest get(URL url) {
		return http.get(url);
	}

	public PostRequest post(String url) {
		return http.post(url);
	}

	public PostRequest post(URL url) {
		return post(url.toString());
	}

	public PostMultipartRequest multipartPost(String url) {
		return http.multipartPost(url);
	}
}
