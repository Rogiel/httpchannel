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
package com.rogiel.httpchannel.http;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientParamBean;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class HttpContext {
	protected final ExecutorService threadPool = Executors
			.newCachedThreadPool();
	/**
	 * The {@link HttpClient} instance for this service
	 */
	protected final DefaultHttpClient client = new DefaultHttpClient();

	protected final ClientParamBean params;

	public HttpContext() {
		// default configuration
		params = new ClientParamBean(client.getParams());
		params.setHandleRedirects(true);
		params.setAllowCircularRedirects(true);
		params.setRejectRelativeRedirect(false);
		params.setMaxRedirects(10);
		
		// browser behavior
		client.setRedirectStrategy(new DefaultRedirectStrategy() {
			@Override
			public boolean isRedirected(HttpRequest request,
					HttpResponse response,
					org.apache.http.protocol.HttpContext context)
					throws ProtocolException {
				return response.containsHeader("Location");
			}
		});
	}

	public GetRequest get(String uri) {
		return new GetRequest(this, uri);
	}

	public GetRequest get(URI uri) {
		return get(uri.toString());
	}

	public PostRequest post(String uri) {
		return new PostRequest(this, uri);
	}

	public PostMultipartRequest multipartPost(String uri) {
		return new PostMultipartRequest(this, uri);
	}
}
