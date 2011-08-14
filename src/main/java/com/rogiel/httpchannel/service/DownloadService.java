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
package com.rogiel.httpchannel.service;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import com.rogiel.httpchannel.DownloadChannel;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public interface DownloadService {
	DownloadChannel download(URI uri, CaptchaResolver captchaResolver);

	/**
	 * Simple delegating implementation for {@link DownloadChannel}.
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 */
	public abstract class SimpleDownloadChannel implements DownloadChannel {
		protected final ReadableByteChannel channel;

		public SimpleDownloadChannel(ReadableByteChannel channel) {
			this.channel = channel;
		}

		public int read(ByteBuffer dst) throws IOException {
			return channel.read(dst);
		}

		public boolean isOpen() {
			return channel.isOpen();
		}

		public void close() throws IOException {
			channel.close();
		}
	}
}
