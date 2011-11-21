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

import com.rogiel.httpchannel.service.exception.DownloadLimitExceededException;
import com.rogiel.httpchannel.service.exception.DownloadLinkNotFoundException;
import com.rogiel.httpchannel.service.exception.DownloadNotAuthorizedException;
import com.rogiel.httpchannel.service.exception.DownloadNotResumableException;

/**
 * This interfaces provides downloading for an service.
 * 
 * @author Rogiel
 * @since 1.0
 */
public interface Downloader {
	/**
	 * Opens a new {@link DownloadChannel} that will be immediately ready to
	 * read data from the download stream.
	 * <p>
	 * Whether an channel is returned or any exception is thrown it is possible
	 * to reuse the same instance for more than one download, this, however, is
	 * very unlikely to be done. The most common usage usage scenario for this
	 * is when an {@link DownloadNotResumableException} is thrown and you wish
	 * to restart the download from start giving <tt>position</tt> equal to
	 * zero.
	 * <p>
	 * Please remember to close the channel by calling
	 * {@link DownloadChannel#close()}, otherwise some of the resources (such as
	 * network connections and file handlers) might still be open for the whole
	 * runtime or until they time out, which could never occur.
	 * 
	 * @param listener
	 *            the listener to keep a track on the download progress
	 * @param position
	 *            the download start position. If seek is supported by service.
	 *            If zero, download will start from the beginning.
	 * 
	 * @return the {@link DownloadChannel} instance
	 * @throws IOException
	 *             if any IO error occur
	 * @throws DownloadLinkNotFoundException
	 *             if the direct download link cannot be found (the file could
	 *             have been deleted)
	 * @throws DownloadLimitExceededException
	 *             if the download limit has been exceed, most times thrown when
	 *             downloading as a non-premium user
	 * @throws DownloadNotAuthorizedException
	 *             if the user (or guest) account does not have necessary rights
	 *             to download the file
	 * @throws DownloadNotResumableException
	 *             if the download cannot be started at <tt>position</tt>. Will
	 *             only be thrown if <tt>position > 0</tt>.
	 */
	DownloadChannel download(DownloadListener listener, long position)
			throws IOException, DownloadLinkNotFoundException,
			DownloadLimitExceededException, DownloadNotAuthorizedException,
			DownloadNotResumableException;
}
