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

import java.net.URL;

import javax.tools.FileObject;

/**
 * Implements an service capable of downloading a file.
 * 
 * @author Rogiel
 * @since 1.0
 */
public interface DownloadService extends Service {
	/**
	 * Creates a new instance of the {@link Downloader}. This instance will be
	 * attached to the {@link URL}, {@link FileObject} provided through the the
	 * arguments and the parent {@link Service} instance.
	 * 
	 * @param url
	 *            the url to be downloaded
	 * @param file
	 *            the destination file
	 * @return an new instance of {@link Downloader}
	 */
	Downloader getDownloader(URL url);

	/**
	 * Check if this {@link Service} can download from this URL. Implemtations
	 * might or might not perform network activity.
	 * <p>
	 * <b>Please note</b> that the value returned by this method may vary based
	 * on it's state (i.e. premium or not).
	 * 
	 * @param url
	 *            the {@link URL} to be tested.
	 * @return true if supported, false otherwise.
	 */
	boolean matchURL(URL url);

	/**
	 * Return the matrix of capabilities for this {@link Downloader}.
	 * 
	 * @return {@link CapabilityMatrix} with all capabilities of this
	 *         {@link Downloader}.
	 * @see DownloaderCapability
	 */
	CapabilityMatrix<DownloaderCapability> getDownloadCapabilities();
}
