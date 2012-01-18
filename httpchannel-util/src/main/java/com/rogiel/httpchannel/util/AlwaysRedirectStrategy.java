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

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class AlwaysRedirectStrategy extends DefaultRedirectStrategy {
	@Override
	public boolean isRedirected(HttpRequest request, HttpResponse response,
			HttpContext context) throws ProtocolException {
		return response.getFirstHeader("location") != null;
	}

	@Override
	public HttpUriRequest getRedirect(HttpRequest request,
			HttpResponse response, HttpContext context)
			throws ProtocolException {
		return super.getRedirect(request, response, context);
	}
}
