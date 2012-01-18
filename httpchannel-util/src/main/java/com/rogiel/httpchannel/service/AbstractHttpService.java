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

import java.net.URI;
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

	public GetRequest get(String uri) {
		return http.get(uri);
	}

	public GetRequest get(URI uri) {
		return http.get(uri);
	}

	public PostRequest post(String uri) {
		return http.post(uri);
	}

	public PostRequest post(URI uri) {
		return post(uri.toString());
	}

	public PostMultipartRequest multipartPost(String uri) {
		return http.multipartPost(uri);
	}
}
