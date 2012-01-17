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
import com.rogiel.httpchannel.util.ChannelUtils;

public class DepositFilesServiceTest {
	private DepositFilesService service;

	@Before
	public void setUp() throws Exception {
		service = new DepositFilesService();
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
}
