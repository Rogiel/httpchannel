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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.rogiel.httpchannel.captcha.CaptchaService;
import com.rogiel.httpchannel.captcha.impl.CaptchaTraderService;
import com.rogiel.httpchannel.service.DownloadChannel;
import com.rogiel.httpchannel.service.UploaderCapability;
import com.rogiel.httpchannel.service.uploadking.UploadKingService;
import com.rogiel.httpchannel.util.ChannelUtils;

public class UploadKingServiceTest {
	private UploadKingService service;

	@Before
	public void setUp() throws Exception {
		service = new UploadKingService();
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

	@Test
	public void testDownloader() throws IOException {
		final Properties p = new Properties();
		p.load(Files.newInputStream(
				Paths.get("../../httpchannel-captcha/src/test/resources/captchatrader.properties"),
				StandardOpenOption.READ));

		final CaptchaService<?> s = new CaptchaTraderService();
		s.authenticate(p.getProperty("username"), p.getProperty("password"));
		service.setCaptchaService(s);

		final DownloadChannel channel = service.getDownloader(
				URI.create("http://www.uploadking.com/WM3PHD9JAY"))
				.openChannel(0);
		System.out.println(new String(ChannelUtils.toByteArray(channel)));
	}
}
