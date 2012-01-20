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
package com.rogiel.httpchannel.service.wupload;

import java.io.IOException;
import java.net.URI;

import javax.xml.bind.JAXB;

import com.rogiel.httpchannel.http.HttpContext;
import com.rogiel.httpchannel.service.wupload.xml.FSAPI;
import com.rogiel.httpchannel.service.wupload.xml.FSGetUploadURL;
import com.rogiel.httpchannel.service.wupload.xml.FSUpload;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class WUploadAPI {
	private static final String BASE_URI = "http://api.wupload.com/";

	private final HttpContext ctx;

	private String email;
	private String password;

	public WUploadAPI(HttpContext ctx) {
		this.ctx = ctx;
	}

	public URI getUploadURI() throws IOException {
		return URI.create((((FSGetUploadURL) execute(FSUpload.class,
				"upload?method=getUploadUrl").getResponse()).getResponse()
				.getUploadURI()));
	}

	public long getMaxFilesize() throws IOException {
		return ((FSGetUploadURL) execute(FSUpload.class,
				"upload?method=getUploadUrl").getResponse()).getResponse()
				.getMaxFilesize();
	}

	public void login(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public void logout() {
		this.email = null;
		this.password = null;
	}

	private <T extends FSAPI> T execute(Class<T> type, String requestURI)
			throws IOException {
		final URI uri = URI.create(BASE_URI + requestURI + "&u=" + email
				+ "&p=" + password + "&format=xml");
		return JAXB.unmarshal(ctx.get(uri).asStream(), type);
	}
}
