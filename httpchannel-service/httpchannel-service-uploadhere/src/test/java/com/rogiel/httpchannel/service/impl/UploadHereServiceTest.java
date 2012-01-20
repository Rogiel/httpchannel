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
package com.rogiel.httpchannel.service.impl;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.rogiel.httpchannel.service.UploaderCapability;
import com.rogiel.httpchannel.service.uploadhere.UploadHereService;
import com.rogiel.httpchannel.util.ChannelUtils;

public class UploadHereServiceTest {
	private UploadHereService service;

	@Before
	public void setUp() throws Exception {
		service = new UploadHereService();
	}

	@Test
	public void testNonLoguedInUploader() throws IOException {
		assertTrue(
				"This service does not have the capability UploadCapability.UNAUTHENTICATED_UPLOAD",
				service.getUploadCapabilities().has(
						UploaderCapability.UNAUTHENTICATED_UPLOAD));

		final Path path = Paths.get("src/test/resources/upload-test-file.txt");
		final URI uri = ChannelUtils.upload(service, path);

		Assert.assertNotNull(uri);
		System.out.println(uri);
	}

	// @Test
	// public void testDownloader() throws IOException {
	// service.setCaptchaResolver(new CaptchaResolver() {
	// @Override
	// public boolean resolve(Captcha rawCaptcha) {
	// final ImageCaptcha captcha = (ImageCaptcha) rawCaptcha;
	// System.out.println(captcha.getImageURI());
	// try {
	// captcha.setAnswer(new BufferedReader(new InputStreamReader(
	// System.in)).readLine());
	// System.out.println("Answer is: " + captcha.getAnswer());
	// return true;
	// } catch (IOException e) {
	// return false;
	// }
	// }
	// });
	//
	// final DownloadChannel channel = service.getDownloader(
	// new URI("http://www.uploadhere.com/9WCEQF1Q07")).openChannel();
	// System.out.println(new String(ChannelUtils.toByteArray(channel)));
	// }
}
