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

/**
 * This interfaces provides downloading for an service.
 * 
 * @author Rogiel
 * @since 1.0
 */
public interface Downloader {
	/**
	 * Starts the download process.
	 * 
	 * @param listener
	 *            the listener to keep a track on the download progress
	 * @param position
	 *            the download start position. If seek is supported by service.
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
	 */
	DownloadChannel download(DownloadListener listener, long position)
			throws IOException, DownloadLinkNotFoundException,
			DownloadLimitExceededException;
}
