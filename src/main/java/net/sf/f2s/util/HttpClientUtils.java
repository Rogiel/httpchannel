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
package net.sf.f2s.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

public class HttpClientUtils {
	private static final ExecutorService threadPool = Executors
			.newCachedThreadPool();

	public static String get(HttpClient client, String url) throws IOException {
		return execute(client, new HttpGet(url));
	}

	public static String execute(HttpClient client, HttpUriRequest request)
			throws IOException {
		return toString(client.execute(request));
	}

	public static Future<String> executeAsync(final HttpClient client,
			final HttpUriRequest request) throws IOException {
		return threadPool.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				return HttpClientUtils.toString(client.execute(request));
			}
		});
	}

	public static String toString(HttpResponse response) throws IOException {
		final InputStream in = response.getEntity().getContent();
		try {
			return IOUtils.toString(in);
		} finally {
			in.close();
		}
	}
}
