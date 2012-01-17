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
