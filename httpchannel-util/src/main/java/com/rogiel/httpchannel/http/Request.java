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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicHeader;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.rogiel.httpchannel.util.HttpClientUtils;
import com.rogiel.httpchannel.util.html.Page;

public abstract class Request {
	private static final JSONParser jsonParser = new JSONParser();
	
	protected final List<Header> headers = new ArrayList<>();
	
	protected final HttpContext ctx;
	protected final String uri;

	public Request(HttpContext ctx, String uri) {
		this.ctx = ctx;
		this.uri = uri;
	}
	
	public Request header(String name, String value) {
		headers.add(new BasicHeader(name, value));
		return this;
	}

	public abstract HttpResponse request() throws IOException;

	public Future<HttpResponse> requestAsync() throws IOException {
		return ctx.threadPool.submit(new Callable<HttpResponse>() {
			@Override
			public HttpResponse call() throws Exception {
				return request();
			}
		});
	}

	public String asString() throws ClientProtocolException, IOException {
		return HttpClientUtils.toString(request());
	}

	public Future<String> asStringAsync() throws IOException {
		return ctx.threadPool.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				return asString();
			}
		});
	}
	
	public InputStream asStream() throws ClientProtocolException, IOException {
		return request().getEntity().getContent();
	}

	public Future<InputStream> asStreamAsync() throws IOException {
		return ctx.threadPool.submit(new Callable<InputStream>() {
			@Override
			public InputStream call() throws Exception {
				return asStream();
			}
		});
	}

	public Page asPage() throws ClientProtocolException, IOException {
		return Page.parse(asString());
	}

	public Future<Page> asPageAsync() throws IOException {
		return ctx.threadPool.submit(new Callable<Page>() {
			@Override
			public Page call() throws Exception {
				return asPage();
			}
		});
	}
	
	public Object asJson() throws ClientProtocolException, IOException, ParseException {
		return jsonParser.parse(asString());
	}

	public Future<Object> asJsonAsync() throws IOException {
		return ctx.threadPool.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				return asJson();
			}
		});
	}

	public String getURI() {
		return uri;
	}
}