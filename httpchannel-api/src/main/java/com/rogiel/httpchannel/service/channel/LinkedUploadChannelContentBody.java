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
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.ContentBody;

import com.rogiel.httpchannel.service.Uploader;

/**
 * {@link ContentBody} used to upload files in {@link Uploader} implementations.
 * 
 * @author Rogiel
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
		return channel.getFilename();
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		final WritableByteChannel outputChannel = Channels.newChannel(out);
		channel.linkChannel(outputChannel);

		while (channel.isOpen() && outputChannel.isOpen()) {
			try {
				Thread.sleep(500);
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
		return channel.getFilesize();
	}

	@Override
	public String getTransferEncoding() {
		return "binary";
	}
}
