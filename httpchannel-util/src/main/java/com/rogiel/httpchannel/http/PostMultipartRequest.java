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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;

import com.rogiel.httpchannel.service.channel.LinkedUploadChannel;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannelContentBody;

public class PostMultipartRequest extends PostRequest {
	private final MultipartEntity entity;

	public PostMultipartRequest(HttpContext ctx, String uri) {
		super(ctx, uri);
		this.entity = new MultipartEntity();
	}

	@Override
	public HttpResponse request() throws IOException {
		final HttpPost post = new HttpPost(uri);
		post.setEntity(entity);
		return ctx.client.execute(post);
	}

	public PostMultipartRequest parameter(String name, ContentBody body) {
		entity.addPart(name, body);
		return this;
	}

	@Override
	public PostMultipartRequest parameter(String name, String value)
			throws UnsupportedEncodingException {
		return parameter(name, new StringBody(value));
	}

	public PostMultipartRequest parameter(String name,
			LinkedUploadChannel channel) {
		return parameter(name, new LinkedUploadChannelContentBody(channel));
	}
}