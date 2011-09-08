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

/**
 * Implements an service capable of uploading a file.
 * 
 * @author Rogiel
 * @since 1.0
 */
public interface UploadService extends Service {
	/**
	 * Creates a new instance of {@link Uploader}. This instance is attached
	 * with the parent {@link Service} instance.<br>
	 * <b>Note</b>: not all services might support <tt>description</tt>
	 * 
	 * @param file
	 *            the file to be uploaded
	 * @param description
	 *            the description of the upload. If supported.
	 * @return the new {@link Uploader} instance
	 */
	Uploader getUploader(String description);

	/**
	 * Get the maximum upload file size supported by this service.
	 * 
	 * @return the maximum filesize supported
	 */
	long getMaximumFilesize();

	/**
	 * Return the matrix of capabilities for this {@link Uploader}.
	 * 
	 * @return {@link CapabilityMatrix} with all capabilities of this
	 *         {@link Uploader}.
	 * @see UploaderCapability
	 */
	CapabilityMatrix<UploaderCapability> getUploadCapabilities();
}
