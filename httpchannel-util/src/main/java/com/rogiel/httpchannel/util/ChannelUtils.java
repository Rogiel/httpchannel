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
