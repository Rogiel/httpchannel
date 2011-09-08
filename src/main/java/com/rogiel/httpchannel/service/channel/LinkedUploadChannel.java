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
package com.rogiel.httpchannel.service.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import com.rogiel.httpchannel.service.UploadChannel;


/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class LinkedUploadChannel implements UploadChannel {
	private WritableByteChannel channel;
	private final LinkedUploadChannelCloseCallback closeCallback;

	private final long length;
	private final String filename;
	private String downloadLink;

	private boolean open = true;

	public LinkedUploadChannel(LinkedUploadChannelCloseCallback closeCallback,
			long filesize, String filename) {
		this.closeCallback = closeCallback;
		this.filename = filename;
		this.length = filesize;
	}

	@Override
	public int write(ByteBuffer src) throws IOException {
		if (channel == null)
			throw new IOException("Channel is not linked yet");
		return channel.write(src);
	}

	@Override
	public boolean isOpen() {
		return (channel != null ? channel.isOpen() : true) && open;
	}

	@Override
	public void close() throws IOException {
		open = false;
		downloadLink = closeCallback.finish();
	}

	public interface LinkedUploadChannelCloseCallback {
		String finish() throws IOException;
	}

	@Override
	public long getLength() {
		return length;
	}

	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public String getDownloadLink() {
		return downloadLink;
	}

	public void linkChannel(WritableByteChannel channel) throws IOException {
		if(this.channel != null)
			throw new IOException("This channel is already linked.");
		this.channel = channel;
	}

	public boolean isLinked() {
		return channel != null;
	}
}
