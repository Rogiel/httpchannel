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
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.rogiel.httpchannel.service.AuthenticationService;
import com.rogiel.httpchannel.service.AuthenticatorListener;
import com.rogiel.httpchannel.service.Credential;
import com.rogiel.httpchannel.service.DownloadChannel;
import com.rogiel.httpchannel.service.DownloadListener;
import com.rogiel.httpchannel.service.DownloadService;
import com.rogiel.httpchannel.service.Service;
import com.rogiel.httpchannel.service.UploadChannel;
import com.rogiel.httpchannel.service.UploadListener;
import com.rogiel.httpchannel.service.UploadService;
import com.rogiel.httpchannel.service.UploaderCapability;
import com.rogiel.httpchannel.service.captcha.Captcha;
import com.rogiel.httpchannel.service.config.ServiceConfigurationHelper;
import com.rogiel.httpchannel.service.impl.MegaUploadService.MegaUploadServiceConfiguration;

public class MegaUploadServiceTest {
	private Service service;

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
		service = new MegaUploadService(
				ServiceConfigurationHelper
						.defaultConfiguration(MegaUploadServiceConfiguration.class));

		final Properties properties = new Properties();
		properties.load(new FileInputStream(
				"src/test/resources/config/megaupload.properties"));
		VALID_USERNAME = properties.getProperty("username");
		VALID_PASSWORD = properties.getProperty("password");
	}

	@Test
	public void testServiceId() {
		System.out.println("Service: "+service.toString());
		assertEquals("megaupload", service.getId());
	}

	@Test
	public void testValidAuthenticator() throws IOException {
		((AuthenticationService) service).getAuthenticator(
				new Credential(VALID_USERNAME, VALID_PASSWORD)).login(
				new AuthenticatorListener() {
					@Override
					public void invalidCredentials(Credential credential) {
						fail("This login attemp should've been successful.");
					}

					@Override
					public void loginSuccessful(Credential credential) {
					}

					@Override
					public void logout(Credential credential) {
					}

					@Override
					public void exception(IOException e) throws IOException {
						throw e;
					}
				});
	}

	@Test
	public void testInvalidAuthenticator() throws IOException {
		((AuthenticationService) service).getAuthenticator(
				new Credential(INVALID_USERNAME, INVALID_PASSWORD)).login(
				new AuthenticatorListener() {
					@Override
					public void invalidCredentials(Credential credential) {
					}

					@Override
					public void loginSuccessful(Credential credential) {
						fail("This login attemp should't have been successful.");
					}

					@Override
					public void logout(Credential credential) {
					}

					@Override
					public void exception(IOException e) throws IOException {
						throw e;
					}
				});
	}

	@Test
	public void testNonLoguedInUploader() throws IOException {
		assertTrue(
				"This service does not have the capability UploadCapability.FREE_UPLOAD",
				((UploadService) service).getUploadCapabilities().has(
						UploaderCapability.NON_PREMIUM_ACCOUNT_UPLOAD));
		final UploadChannel channel = ((UploadService) service).getUploader(
				"Upload by httpchannel").upload(new UploadListener() {
			@Override
			public long getFilesize() {
				return 10;
			}

			@Override
			public String getFilename() {
				return "test.bin";
			}
		});
		final ByteBuffer buffer = ByteBuffer.allocate(10);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);

		buffer.flip();

		channel.write(buffer);
		channel.close();
		Assert.assertNotNull(channel.getDownloadLink());
	}

	@Test
	public void testLoguedInUploader() throws IOException {
		assertTrue(
				"This service does not have the capability UploadCapability.PREMIUM_UPLOAD",
				((UploadService) service).getUploadCapabilities().has(
						UploaderCapability.PREMIUM_ACCOUNT_UPLOAD));

		((AuthenticationService) service).getAuthenticator(
				new Credential(VALID_USERNAME, VALID_PASSWORD)).login(
				new AuthenticatorListener() {
					@Override
					public void invalidCredentials(Credential credential) {
						fail("Invalid credentials");
					}

					@Override
					public void loginSuccessful(Credential credential) {
					}

					@Override
					public void logout(Credential credential) {
					}

					@Override
					public void exception(IOException e) throws IOException {
						// TODO Auto-generated method stub

					}
				});

		final UploadChannel channel = ((UploadService) service).getUploader(
				"Upload by httpchannel").upload(new UploadListener() {
			@Override
			public long getFilesize() {
				return 10;
			}

			@Override
			public String getFilename() {
				return "test.bin";
			}
		});
		final ByteBuffer buffer = ByteBuffer.allocate(10);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);

		buffer.flip();

		channel.write(buffer);
		channel.close();
		Assert.assertNotNull(channel.getDownloadLink());
	}

	@Test
	public void testDownloader() throws IOException, MalformedURLException {
		final DownloadChannel channel = ((DownloadService) service)
				.getDownloader(new URL("http://www.megaupload.com/?d=CVQKJ1KM"))
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
	public void testNoWaitDownloader() throws IOException,
			MalformedURLException {
		service = new MegaUploadService(ServiceConfigurationHelper.file(
				MegaUploadServiceConfiguration.class, new File(
						"src/test/resources/megaupload-nowait.properties")));

		final DownloadChannel channel = ((DownloadService) service)
				.getDownloader(new URL("http://www.megaupload.com/?d=CVQKJ1KM"))
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
	}
}
