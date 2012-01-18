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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

public class PostRequest extends Request {
	protected final List<NameValuePair> params = new ArrayList<NameValuePair>();

	public PostRequest(HttpContext ctx, String uri) {
		super(ctx, uri);
	}

	@Override
	public HttpResponse request() throws IOException {
		final HttpPost post = new HttpPost(uri);
		post.setEntity(new UrlEncodedFormEntity(params));
		return ctx.client.execute(post);
	}

	public PostRequest parameter(String name, String value)
			throws UnsupportedEncodingException {
		params.add(new BasicNameValuePair(name, value));
		return this;
	}

	public PostRequest parameter(String name, int value)
			throws UnsupportedEncodingException {
		return parameter(name, Integer.toString(value));
	}

	public PostRequest parameter(String name, boolean value)
			throws UnsupportedEncodingException {
		return parameter(name, (value ? "1" : "0"));
	}
}