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
package com.rogiel.httpchannel.service.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.rogiel.httpchannel.service.UploadService;
import com.rogiel.httpchannel.service.Uploader;
import com.rogiel.httpchannel.service.Uploader.UploaderConfiguration;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class UploadServices {
	/**
	 * Creates a new {@link Uploader} for the given NIO {@link Path}, using
	 * <code>configuration</code> as the {@link Uploader} configuration.
	 * 
	 * @param service
	 *            the upload service
	 * @param configuration
	 *            the uploader configuration
	 * @param path
	 *            the NIO.2 {@link Path}
	 * @return a newly created {@link Uploader}
	 * @throws IOException
	 *             if any exception occur while fetching {@link Path}
	 *             information
	 */
	public static <S extends UploadService<C>, C extends UploaderConfiguration> Uploader<C> upload(
			S service, C configuration, Path path) throws IOException {
		return service.getUploader(path.getFileName().toString(),
				Files.size(path), configuration);
	}

	/**
	 * Creates a new {@link Uploader} for the given NIO {@link Path}.
	 * 
	 * @param service
	 *            the upload service
	 * @param path
	 *            the NIO.2 {@link Path}
	 * @return a newly created {@link Uploader}
	 * @throws IOException
	 *             if any exception occur while fetching {@link Path}
	 *             information
	 */
	public static <S extends UploadService<C>, C extends UploaderConfiguration> Uploader<C> upload(
			S service, Path path) throws IOException {
		return service.getUploader(path.getFileName().toString(),
				Files.size(path));
	}

	/**
	 * Checks whether the given <code>service</code> can upload the file
	 * represented by <code>path</code>
	 * 
	 * @param service
	 *            the upload service
	 * @param path
	 *            the file {@link Path}
	 * @return <code>true</code> if the upload will be acepted
	 * @throws IOException
	 *             if any exception occur while fetching {@link Path}
	 *             information
	 */
	public static boolean canUpload(UploadService<?> service, Path path)
			throws IOException {
		return service.getMaximumFilesize() >= Files.size(path);
	}
}
