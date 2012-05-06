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
package com.rogiel.httpchannel.service.uptobox;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.rogiel.httpchannel.service.Credential;
import com.rogiel.httpchannel.service.ServiceID;
import com.rogiel.httpchannel.service.UploadChannel;
import com.rogiel.httpchannel.service.exception.AuthenticationInvalidCredentialException;
import com.rogiel.httpchannel.service.helper.UploadServices;
import com.rogiel.httpchannel.util.ChannelUtils;

public class UptoboxServiceTest {
	private UptoboxService service;

	/**
	 * See <b>src/test/resources/config/megaupload.properties</b>
	 * <p>
	 * Key: username
	 */
	private String VALID_USERNAME;
	/**
	 * See <b>src/test/resources/config/megaupload.properties</b>
	 * <p>
	 * Key: password
	 */
	private String VALID_PASSWORD;

	private static final String INVALID_USERNAME = "invalid";
	private static final String INVALID_PASSWORD = "abc";

	@Before
	public void setUp() throws Exception {
		service = new UptoboxService();

		final Properties properties = new Properties();
		properties.load(new FileInputStream(
				"../src/test/resources/login.properties"));
		VALID_USERNAME = properties.getProperty("uptobox.username");
		VALID_PASSWORD = properties.getProperty("uptobox.password");
	}

	@Test
	public void testServiceId() {
		assertEquals(ServiceID.create("uptobox"), service.getServiceID());
	}

	@Test
	public void testValidAuthenticator() throws IOException {
		service.getAuthenticator(new Credential(VALID_USERNAME, VALID_PASSWORD))
				.login();
	}

	@Test(expected = AuthenticationInvalidCredentialException.class)
	public void testInvalidAuthenticator() throws IOException {
		service.getAuthenticator(
				new Credential(INVALID_USERNAME, INVALID_PASSWORD)).login();
	}

	@Test
	public void testNonLoguedInUploader() throws IOException {
		final Path path = Paths
				.get("../src/test/resources/upload-test-file.txt");
		final UploadChannel channel = UploadServices.upload(service, path)
				.openChannel();
		final SeekableByteChannel inChannel = Files.newByteChannel(path);

		try {
			ChannelUtils.copy(inChannel, channel);
		} finally {
			inChannel.close();
			channel.close();
		}

		System.out.println(channel.getDownloadLink());
		Assert.assertNotNull(channel.getDownloadLink());
	}

	@Test
	public void testLoggedInUploader() throws IOException {
		service.getAuthenticator(new Credential(VALID_USERNAME, VALID_PASSWORD))
				.login();

		final Path path = Paths
				.get("../src/test/resources/upload-test-file.txt");
		final UploadChannel channel = UploadServices.upload(service, path)
				.openChannel();
		final SeekableByteChannel inChannel = Files.newByteChannel(path);

		try {
			ChannelUtils.copy(inChannel, channel);
		} finally {
			inChannel.close();
			channel.close();
		}

		System.out.println(channel.getDownloadLink());
		Assert.assertNotNull(channel.getDownloadLink());
	}
}
