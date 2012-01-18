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
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritableByteChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rogiel.httpchannel.service.UploadChannel;
import com.rogiel.httpchannel.service.UploadService;
import com.rogiel.httpchannel.service.Uploader;
import com.rogiel.httpchannel.service.exception.UploadLinkNotFoundException;

/**
 * This channel is linked onto another {@link Channel} that actually writes data
 * into the network stream.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class LinkedUploadChannel implements UploadChannel {
	/**
	 * The logger instance
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The upload service
	 */
	private final UploadService<?> service;
	/**
	 * The uploader instance
	 */
	private final Uploader<?> uploader;

	/**
	 * The destionation {@link Channel}. Data writted is forwarded to this
	 * channel.
	 */
	private WritableByteChannel channel;
	/**
	 * The close callback, that notifies once the channel has been closed
	 */
	private final LinkedUploadChannelCloseCallback closeCallback;

	/**
	 * The length of the data being uploaded
	 */
	private final long length;
	/**
	 * The file name
	 */
	private final String filename;
	/**
	 * Only set when {@link #close()} is called and the download has finished.
	 */
	private URI downloadLink;

	/**
	 * Whether the channel is still open or not
	 */
	private boolean open = true;

	public LinkedUploadChannel(UploadService<?> service, Uploader<?> uploader,
			LinkedUploadChannelCloseCallback closeCallback, long filesize,
			String filename) {
		this.service = service;
		this.uploader = uploader;
		this.closeCallback = closeCallback;
		this.filename = filename;
		this.length = filesize;
	}

	@Override
	public int write(ByteBuffer src) throws IOException {
		if (channel == null)
			throw new IOException("Channel is not linked yet");
		if (!open)
			throw new ClosedChannelException();
		try {
			return channel.write(src);
		} catch (IOException e) {
			close();
			throw e;
		}
	}

	@Override
	public boolean isOpen() {
		return (channel != null ? channel.isOpen() : true) && open;
	}

	@Override
	public void close() throws IOException {
		open = false;
		final String downloadLink = closeCallback.finish();
		logger.debug("Download link returned by service is {}", downloadLink);
		if (downloadLink == null)
			throw new UploadLinkNotFoundException();
		this.downloadLink = URI.create(downloadLink);
	}

	public interface LinkedUploadChannelCloseCallback {
		String finish() throws IOException;
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
	public URI getDownloadLink() {
		return downloadLink;
	}

	@Override
	public UploadService<?> getService() {
		return service;
	}

	@Override
	public Uploader<?> getUploader() {
		return uploader;
	}

	/**
	 * Links this {@link Channel} to the destionation {@link Channel}. All data
	 * written in this channel will be redirected to the destination
	 * {@link Channel}.
	 * 
	 * @param channel
	 *            the target channel
	 * @throws IOException
	 *             if the channel is already linked or the destination channel
	 *             is closed
	 */
	protected void linkChannel(WritableByteChannel channel) throws IOException {
		if (this.channel != null)
			throw new IOException("This channel is already linked");
		if (!channel.isOpen())
			throw new IOException("The destination channel is closed");
		this.channel = channel;
	}

	/**
	 * @return <code>true</code> if the channel is linked with the destination
	 *         {@link Channel}
	 */
	public boolean isLinked() {
		return channel != null;
	}
}
