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

import com.rogiel.httpchannel.service.Uploader.UploaderConfiguration;
import com.rogiel.httpchannel.service.config.NullUploaderConfiguration;

/**
 * Implements an service capable of uploading a file.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @since 1.0
 */
public interface UploadService<C extends UploaderConfiguration> extends Service {
	/**
	 * Creates a new instance of {@link Uploader}. This instance is attached
	 * with the parent {@link Service} instance.<br>
	 * <b>Note</b>: not all services might support <tt>description</tt>
	 * 
	 * @param filename
	 *            the name of the file to be uploaded
	 * @param filesize
	 *            the size of the file to be uploaded. This must be exact.
	 * @param configuration
	 *            the uploader configuration
	 * @return the new {@link Uploader} instance
	 */
	Uploader<C> getUploader(String filename, long filesize, C configuration);
	
	/**
	 * Creates a new instance of {@link Uploader}. This instance is attached
	 * with the parent {@link Service} instance.<br>
	 * <b>Note</b>: not all services might support <tt>description</tt>
	 * 
	 * @param filename
	 *            the name of the file to be uploaded
	 * @param filesize
	 *            the size of the file to be uploaded. This must be exact.
	 * @return the new {@link Uploader} instance
	 */
	Uploader<C> getUploader(String filename, long filesize);
	
	/**
	 * Creates a new configuration object. If a service does not support or
	 * require configuration, {@link NullUploaderConfiguration} should be
	 * returned.
	 * 
	 * @return a new configuration object or
	 *         {@link NullUploaderConfiguration}
	 */
	C newUploaderConfiguration();

	/**
	 * Get the maximum upload file size supported by this service.
	 * <p>
	 * <b>Please note</b> that the value returned by this method may vary based
	 * on it's state (i.e. premium or not).
	 * 
	 * @return the maximum filesize supported
	 */
	long getMaximumFilesize();

	/**
	 * Get the list of all supported extensions. Might return <tt>null</tt> if
	 * there is no restriction.
	 * <p>
	 * <b>Please note</b> that the value returned by this method may vary based
	 * on it's state (i.e. premium or not).
	 * 
	 * @return the list of supported file extensions. Can return <tt>null</tt>
	 *         if there is not restriction
	 */
	String[] getSupportedExtensions();

	/**
	 * Return the matrix of capabilities for this {@link Uploader}.
	 * 
	 * @return {@link CapabilityMatrix} with all capabilities of this
	 *         {@link Uploader}.
	 * @see UploaderCapability
	 */
	CapabilityMatrix<UploaderCapability> getUploadCapabilities();
}
