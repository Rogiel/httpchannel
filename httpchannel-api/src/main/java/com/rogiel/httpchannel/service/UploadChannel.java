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
import java.net.URL;
import java.nio.channels.Channel;
import java.nio.channels.WritableByteChannel;

import com.rogiel.httpchannel.service.exception.UploadLinkNotFoundException;

/**
 * This is an {@link Channel} for uploads. Any data to be uploaded, must be
 * written into this channel.
 * <p>
 * Since this {@link Channel} <tt>implements</tt> {@link WritableByteChannel}
 * you can treat it as any other regular IO {@link Channel}.
 * <p>
 * <b>Remember</b>: always close the {@link Channel}, if you do otherwise, your
 * upload will not finish and will never return the link.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public interface UploadChannel extends HttpChannel, WritableByteChannel {
	/**
	 * The link is located after you call {@link UploadChannel#close()}, but it
	 * can only be retrieved by calling this method. If {@link #close()} throwed
	 * an exception, this method might return <tt>null</tt>.
	 * 
	 * @return the download link for this upload
	 */
	URL getDownloadLink();

	/**
	 * @throws UploadLinkNotFoundException
	 *             if after the upload, the download link cannot be found
	 */
	@Override
	void close() throws IOException, UploadLinkNotFoundException;
}
