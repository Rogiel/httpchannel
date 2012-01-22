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
package com.rogiel.httpchannel.service.twoshared;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import junit.framework.Assert;

import org.junit.Test;

import com.rogiel.httpchannel.service.Downloader;
import com.rogiel.httpchannel.util.ChannelUtils;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class TwoSharedServiceTest {
	private final TwoSharedService service = new TwoSharedService();

	@Test
	public void testUpload() throws IOException {
		final Path path = Paths
				.get("../src/test/resources/upload-test-file.txt");
		final URI uri = ChannelUtils.upload(service, path);

		Assert.assertNotNull(uri);
		System.out.println(uri);
	}

	@Test
	public void testDownload() throws IOException {
		final Downloader<?> downloader = service
				.getDownloader(URI
						.create("http://www.2shared.com/document/04tjgnAr/upload-test-file.html"));
		System.out.println(new String(ChannelUtils.toByteArray(downloader.openChannel())));
	}
}
