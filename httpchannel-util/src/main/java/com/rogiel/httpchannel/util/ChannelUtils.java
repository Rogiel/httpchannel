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
package com.rogiel.httpchannel.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.rogiel.httpchannel.service.UploadChannel;
import com.rogiel.httpchannel.service.UploadService;
import com.rogiel.httpchannel.service.helper.UploadServices;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class ChannelUtils {
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

	public static byte[] toByteArray(ReadableByteChannel channel)
			throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(channel, Channels.newChannel(out));
		return out.toByteArray();
	}

	public static URI upload(UploadService<?> service, Path path)
			throws IOException {
		final UploadChannel uploadChannel = UploadServices
				.upload(service, path).openChannel();
		try {
			copy(Files.newByteChannel(path, StandardOpenOption.READ),
					uploadChannel);
		} finally {
			uploadChannel.close();
		}
		return uploadChannel.getDownloadLink();
	}
}
