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

import java.net.URI;

import javax.tools.FileObject;

import com.rogiel.httpchannel.service.Downloader.DownloaderConfiguration;
import com.rogiel.httpchannel.service.config.NullDownloaderConfiguration;

/**
 * Implements an service capable of downloading a file.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @since 1.0
 */
public interface DownloadService<C extends DownloaderConfiguration> extends
		Service {
	/**
	 * Creates a new instance of the {@link Downloader}. This instance will be
	 * attached to the {@link URI}, {@link FileObject} provided through the the
	 * arguments and the parent {@link Service} instance.
	 * 
	 * @param uri
	 *            the uri to be downloaded
	 * @param configuration
	 *            the downloader configuration
	 * @return an new instance of {@link Downloader}
	 */
	Downloader<C> getDownloader(URI uri, C configuration);

	/**
	 * Creates a new instance of the {@link Downloader}. This instance will be
	 * attached to the {@link URI}, {@link FileObject} provided through the the
	 * arguments and the parent {@link Service} instance.
	 * 
	 * @param uri
	 *            the uri to be downloaded
	 * @return an new instance of {@link Downloader}
	 */
	Downloader<C> getDownloader(URI uri);

	/**
	 * Creates a new configuration object. If a service does not support or
	 * require configuration, {@link NullDownloaderConfiguration} should be
	 * returned.
	 * 
	 * @return a new configuration object or {@link NullDownloaderConfiguration}
	 */
	C newDownloaderConfiguration();

	/**
	 * Check if this {@link Service} can download from this URI. Implementations
	 * might or might not perform network activity.
	 * <p>
	 * <b>Please note</b> that the value returned by this method may vary based
	 * on it's state (i.e. premium or not).
	 * 
	 * @param uri
	 *            the {@link URI} to be tested.
	 * @return true if supported, false otherwise.
	 */
	boolean matchURI(URI uri);

	/**
	 * Return the matrix of capabilities for this {@link Downloader}.
	 * 
	 * @return {@link CapabilityMatrix} with all capabilities of this
	 *         {@link Downloader}.
	 * @see DownloaderCapability
	 */
	CapabilityMatrix<DownloaderCapability> getDownloadCapabilities();
}
