/*
 * This file is part of seedbox <github.com/Rogiel/seedbox>.
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
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import com.rogiel.httpchannel.UploadChannel;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public interface UploadService {
	UploadChannel upload(String filename, long filesize);
	
	/**
	 * Simple delegating implementation for {@link UploadChannel}.
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 */
	public abstract class SimpleUploadChannel implements UploadChannel {
		protected final WritableByteChannel channel;

		public SimpleUploadChannel(WritableByteChannel channel) {
			this.channel = channel;
		}

		@Override
		public int write(ByteBuffer src) throws IOException {
			return channel.write(src);
		}

		@Override
		public boolean isOpen() {
			return channel.isOpen();
		}
	}
}
