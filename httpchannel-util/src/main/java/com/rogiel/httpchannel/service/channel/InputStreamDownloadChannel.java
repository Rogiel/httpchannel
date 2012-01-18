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
package com.rogiel.httpchannel.service.channel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import com.rogiel.httpchannel.service.DownloadChannel;
import com.rogiel.httpchannel.service.DownloadService;
import com.rogiel.httpchannel.service.Downloader;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class InputStreamDownloadChannel implements DownloadChannel {
	private final ReadableByteChannel channel;

	private final DownloadService<?> service;
	private final Downloader<?> downloader;

	private final long length;
	private final String filename;

	public InputStreamDownloadChannel(DownloadService<?> service,
			Downloader<?> downloader, InputStream in, final long length,
			final String filename) {
		this.service = service;
		this.downloader = downloader;
		this.channel = Channels.newChannel(in);
		this.length = length;
		this.filename = filename;
	}

	@Override
	public int read(ByteBuffer dst) throws IOException {
		return channel.read(dst);
	}

	@Override
	public boolean isOpen() {
		return channel.isOpen();
	}

	@Override
	public void close() throws IOException {
		channel.close();
	}

	@Override
	public long size() {
		return length;
	}

	@Override
	public String filename() {
		return filename;
	}

	@Override
	public DownloadService<?> getService() {
		return service;
	}

	@Override
	public Downloader<?> getDownloader() {
		return downloader;
	}
}
