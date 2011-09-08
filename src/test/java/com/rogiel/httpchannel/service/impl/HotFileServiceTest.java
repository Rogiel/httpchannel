/*
 * This file is part of seedbox <github.com/seedbox>.
 *
 * seedbox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * seedbox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with seedbox.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.rogiel.httpchannel.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.rogiel.httpchannel.service.AuthenticationService;
import com.rogiel.httpchannel.service.Credential;
import com.rogiel.httpchannel.service.DownloadChannel;
import com.rogiel.httpchannel.service.DownloadListener;
import com.rogiel.httpchannel.service.DownloadService;
import com.rogiel.httpchannel.service.Service;
import com.rogiel.httpchannel.service.UploadChannel;
import com.rogiel.httpchannel.service.UploadService;
import com.rogiel.httpchannel.service.UploaderCapability;
import com.rogiel.httpchannel.service.captcha.Captcha;
import com.rogiel.httpchannel.service.config.ServiceConfigurationHelper;
import com.rogiel.httpchannel.service.impl.HotFileService.HotFileServiceConfiguration;

public class HotFileServiceTest {
	private Service service;

	/**
	 * See <b>src/test/resources/config/hotfile.properties</b>
	 * <p>
	 * Key: username
	 */
	private String VALID_USERNAME;
	/**
	 * See <b>src/test/resources/config/hotfile.properties</b>
	 * <p>
	 * Key: password
	 */
	private String VALID_PASSWORD;

	private static final String INVALID_USERNAME = "invalid";
	private static final String INVALID_PASSWORD = "abc";

	@Before
	public void setUp() throws Exception {
		// MegaUploadServiceConfiguration.class;
		service = new HotFileService(
				ServiceConfigurationHelper
						.defaultConfiguration(HotFileServiceConfiguration.class));

		final Properties properties = new Properties();
		properties.load(new FileInputStream(
				"src/test/resources/config/hotfile.properties"));
		VALID_USERNAME = properties.getProperty("username");
		VALID_PASSWORD = properties.getProperty("password");
	}

	@Test
	public void testServiceId() {
		System.out.println("Service: " + service.toString());
		assertEquals("hotfile", service.getId());
	}

	@Test
	public void testValidAuthenticator() throws IOException {
		Assert.assertTrue(((AuthenticationService) service).getAuthenticator(
				new Credential(VALID_USERNAME, VALID_PASSWORD)).login());
	}

	@Test
	public void testInvalidAuthenticator() throws IOException {
		Assert.assertFalse(((AuthenticationService) service).getAuthenticator(
				new Credential(INVALID_USERNAME, INVALID_PASSWORD)).login());
	}

	@Test
	public void testNonLoguedInUploader() throws IOException {
		assertTrue(
				"This service does not have the capability UploadCapability.FREE_UPLOAD",
				((UploadService) service).getUploadCapabilities().has(
						UploaderCapability.NON_PREMIUM_ACCOUNT_UPLOAD));
		final UploadChannel channel = ((UploadService) service).getUploader(
				"simulado_2010_1_res_all.zip",
				new File("simulado_2010_1_res_all.zip").length(), null)
				.upload();

		final FileChannel fileChannel = new FileInputStream(
				"simulado_2010_1_res_all.zip").getChannel();

		copy(fileChannel, channel);
		channel.close();

		System.out.println(channel.getDownloadLink());
		Assert.assertNotNull(channel.getDownloadLink());
	}

	@Test
	public void testLoguedInUploader() throws IOException {
		assertTrue(
				"This service does not have the capability UploadCapability.PREMIUM_UPLOAD",
				((UploadService) service).getUploadCapabilities().has(
						UploaderCapability.PREMIUM_ACCOUNT_UPLOAD));

		Assert.assertTrue(((AuthenticationService) service).getAuthenticator(
				new Credential(VALID_USERNAME, VALID_PASSWORD)).login());

		final UploadChannel channel = ((UploadService) service).getUploader(
				"simulado_2010_1_res_all.zip",
				new File("simulado_2010_1_res_all.zip").length(), null)
				.upload();

		final FileChannel fileChannel = new FileInputStream(
				"simulado_2010_1_res_all.zip").getChannel();

		copy(fileChannel, channel);
		channel.close();

		System.out.println(channel.getDownloadLink());
		Assert.assertNotNull(channel.getDownloadLink());
	}

	@Test
	public void testDownloader() throws IOException, MalformedURLException {
		final DownloadChannel channel = ((DownloadService) service)
				.getDownloader(
						new URL(
								"http://hotfile.com/dl/129251605/9b4faf2/simulado_2010_1_res_all.zip.html"))
				.download(new DownloadListener() {
					@Override
					public boolean timer(long time, TimerWaitReason reason) {
						System.out.println("Waiting " + time + " in " + reason);
						// if (reason == TimerWaitReason.DOWNLOAD_TIMER)
						// return true;
						return true;
					}

					@Override
					public String captcha(Captcha captcha) {
						return null;
					}
				});
		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		IOUtils.copy(Channels.newInputStream(channel), bout);
		System.out.println(bout.size());
	}

	@Test
	public void testLoggedInDownloader() throws IOException,
			MalformedURLException {
		Assert.assertTrue(((AuthenticationService) service).getAuthenticator(
				new Credential(VALID_USERNAME, VALID_PASSWORD)).login());

		final DownloadChannel channel = ((DownloadService) service)
				.getDownloader(
						new URL(
								"http://hotfile.com/dl/129251605/9b4faf2/simulado_2010_1_res_all.zip.html"))
				.download(new DownloadListener() {
					@Override
					public boolean timer(long time, TimerWaitReason reason) {
						System.out.println("Waiting " + time + " in " + reason);
						if (reason == TimerWaitReason.DOWNLOAD_TIMER)
							return true;
						return false;
					}

					@Override
					public String captcha(Captcha captcha) {
						// TODO Auto-generated method stub
						return null;
					}
				});

		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		IOUtils.copy(Channels.newInputStream(channel), bout);
		System.out.println(bout.size());
	}

	public static void copy(ReadableByteChannel in, WritableByteChannel out)
			throws IOException {
		// First, we need a buffer to hold blocks of copied bytes.
		ByteBuffer buffer = ByteBuffer.allocateDirect(32 * 1024);

		// Now loop until no more bytes to read and the buffer is empty
		while (in.read(buffer) != -1 || buffer.position() > 0) {
			// The read() call leaves the buffer in "fill mode". To prepare
			// to write bytes from the bufferwe have to put it in "drain mode"
			// by flipping it: setting limit to position and position to zero
			buffer.flip();

			// Now write some or all of the bytes out to the output channel
			out.write(buffer);

			// Compact the buffer by discarding bytes that were written,
			// and shifting any remaining bytes. This method also
			// prepares the buffer for the next call to read() by setting the
			// position to the limit and the limit to the buffer capacity.
			buffer.compact();
		}
	}
}
