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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.rogiel.httpchannel.service.Credential;
import com.rogiel.httpchannel.service.DownloadChannel;
import com.rogiel.httpchannel.service.DownloadListener;
import com.rogiel.httpchannel.service.ServiceID;
import com.rogiel.httpchannel.service.UploadChannel;
import com.rogiel.httpchannel.service.UploaderCapability;
import com.rogiel.httpchannel.service.exception.AuthenticationInvalidCredentialException;
import com.rogiel.httpchannel.service.helper.UploadServices;
import com.rogiel.httpchannel.util.ChannelUtils;

public class MegaUploadServiceTest {
	private MegaUploadService service;

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
		// MegaUploadServiceConfiguration.class;
		service = new MegaUploadService();

		final Properties properties = new Properties();
		properties.load(new FileInputStream(
				"src/test/resources/login.properties"));
		VALID_USERNAME = properties.getProperty("username");
		VALID_PASSWORD = properties.getProperty("password");
	}

	@Test
	public void testServiceId() {
		assertEquals(ServiceID.create("megaupload"), service.getServiceID());
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
		assertTrue(
				"This service does not have the capability UploadCapability.FREE_UPLOAD",
				service.getUploadCapabilities().has(
						UploaderCapability.NON_PREMIUM_ACCOUNT_UPLOAD));
		final Path path = Paths.get("src/test/resources/upload-test-file.txt");
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
	public void testLoguedInUploader() throws IOException {
		assertTrue(
				"This service does not have the capability UploadCapability.PREMIUM_UPLOAD",
				service.getUploadCapabilities().has(
						UploaderCapability.PREMIUM_ACCOUNT_UPLOAD));

		service.getAuthenticator(new Credential(VALID_USERNAME, VALID_PASSWORD))
				.login();

		final Path path = Paths.get("src/test/resources/upload-test-file.txt");
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
	public void testFreeDownloader() throws IOException {
		final DownloadChannel channel = service.getDownloader(
				URI.create("http://www.megaupload.com/?d=CVQKJ1KM"))
				.openChannel(new DownloadListener() {
					@Override
					public boolean timer(long time) {
						System.out.println("Waiting " + time);
						// if (reason == TimerWaitReason.DOWNLOAD_TIMER)
						// return true;
						return true;
					}
				}, 0);
		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		IOUtils.copy(Channels.newInputStream(channel), bout);
		System.out.println(bout.size());
	}

	@Test
	public void testPremiumDownloader() throws IOException {
		service.getAuthenticator(new Credential(VALID_USERNAME, VALID_PASSWORD))
				.login();

		final DownloadChannel channel = service.getDownloader(
				URI.create("http://www.megaupload.com/?d=CVQKJ1KM"))
				.openChannel(new DownloadListener() {
					@Override
					public boolean timer(long time) {
						System.out.println("Waiting " + time);
						return true;
					}
				}, 0);
		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		IOUtils.copy(Channels.newInputStream(channel), bout);
		System.out.println(bout.size());
	}

	@Test
	public void testNoWaitDownloader() throws IOException {
		service = new MegaUploadService();
		// service.setServiceConfiguration(ServiceConfigurationHelper.file(
		// MegaUploadServiceConfiguration.class, new File(
		// "src/test/resources/megaupload-nowait.properties")));
		final MegaUploadDownloaderConfiguration config = new MegaUploadDownloaderConfiguration();
		config.setRespectWaitTime(false);

		@SuppressWarnings({ "unused" })
		final DownloadChannel channel = service.getDownloader(
				URI.create("http://www.megaupload.com/?d=CVQKJ1KM"), config)
				.openChannel(new DownloadListener() {
					@Override
					public boolean timer(long time) {
						System.out.println("Waiting " + time);
						return false;
					}
				}, 0);
	}
}
