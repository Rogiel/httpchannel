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
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.ContentBody;

import com.rogiel.httpchannel.service.Uploader;

/**
 * {@link ContentBody} used to upload files in {@link Uploader} implementations.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @since 1.0
 */
public class LinkedUploadChannelContentBody extends AbstractContentBody {
	private final LinkedUploadChannel channel;

	public LinkedUploadChannelContentBody(LinkedUploadChannel channel) {
		super("application/octet-stream");
		this.channel = channel;
	}

	@Override
	public String getFilename() {
		return channel.filename();
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		final WritableByteChannel outputChannel = Channels.newChannel(out);
		channel.linkChannel(outputChannel);
		while (channel.isOpen() && outputChannel.isOpen()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public String getCharset() {
		return null;
	}

	@Override
	public long getContentLength() {
		return channel.size();
	}

	@Override
	public String getTransferEncoding() {
		return "binary";
	}
}
