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
package com.rogiel.httpchannel.util;

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

import com.rogiel.httpchannel.util.htmlparser.HTMLPage;

public class HttpClientUtils {
	private static final ExecutorService threadPool = Executors
			.newCachedThreadPool();

	public static HttpResponse get(HttpClient client, String uri)
			throws IOException {
		return client.execute(new HttpGet(uri));
	}

	public static String getString(HttpClient client, String uri)
			throws IOException {
		return toString(get(client, uri));
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

	public static Future<HttpResponse> executeAsyncHttpResponse(
			final HttpClient client, final HttpUriRequest request)
			throws IOException {
		return threadPool.submit(new Callable<HttpResponse>() {
			@Override
			public HttpResponse call() throws Exception {
				return client.execute(request);
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

	public static HTMLPage toPage(HttpResponse response) throws IOException {
		return HTMLPage.parse(toString(response));
	}
}
