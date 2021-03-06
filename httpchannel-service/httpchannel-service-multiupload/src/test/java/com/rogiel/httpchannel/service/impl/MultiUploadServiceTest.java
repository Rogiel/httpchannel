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

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.rogiel.httpchannel.service.DownloadService;
import com.rogiel.httpchannel.service.multiupload.MultiUploadService;
import com.rogiel.httpchannel.util.ChannelUtils;

public class MultiUploadServiceTest {
	public static final Path TEST_UPLOAD_FILE = Paths
			.get("src/test/resources/upload-test-file.txt");
	public static final byte[] EXPECTED_FULL_CHECKSUM = new byte[] { 27, -93,
			-76, 6, 123, -31, -9, 1, -100, 103, 123, -108, -22, -3, 121, -54,
			-127, 27, 43, -8 };
	public static final byte[] EXPECTED_RESUME_CHECKSUM = new byte[] { 39, -29,
			-107, -76, -69, -122, -20, 78, -27, -60, 95, -23, 70, -127, -17,
			101, -39, -87, -2, -67 };

	private MultiUploadService service;

	@Before
	public void setUp() throws Exception {
		this.service = new MultiUploadService();
	}

	@Test
	public void testUploader() throws IOException {
		final URI uri = ChannelUtils.upload(service, TEST_UPLOAD_FILE);
		Assert.assertNotNull(uri);
		System.out.println("Uploaded file to " + uri);
	}

	@Test
	public void testDownloader() throws IOException, NoSuchAlgorithmException {
		final byte[] data = ChannelUtils
				.toByteArray(((DownloadService<?>) service).getDownloader(
						URI.create("http://www.multiupload.nl/ITPPI2YSYX"))
						.openChannel());
		assertChecksum("Downloaded data checksum did not matched", "SHA1",
				data, EXPECTED_FULL_CHECKSUM);
	}

	@Test
	public void testDownloaderResume() throws IOException,
			NoSuchAlgorithmException {
		final byte[] data = ChannelUtils
				.toByteArray(((DownloadService<?>) service).getDownloader(
						URI.create("http://www.multiupload.nl/ITPPI2YSYX"))
						.openChannel(50));
		assertChecksum("Downloaded data checksum did not matched", "SHA1",
				data, EXPECTED_RESUME_CHECKSUM);
	}

	public static void assertChecksum(String message, String algorithm,
			byte[] data, byte[] expected) throws NoSuchAlgorithmException {
		final MessageDigest md = MessageDigest.getInstance(algorithm);
		final byte[] actual = md.digest(data);
		Assert.assertArrayEquals(message, expected, actual);
	}
}
