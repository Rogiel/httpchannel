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
