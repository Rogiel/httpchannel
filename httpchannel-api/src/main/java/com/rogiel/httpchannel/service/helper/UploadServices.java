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
	public static <S extends UploadService<C>, C extends UploaderConfiguration> Uploader<C> upload(
			S service, C configuration, Path path) throws IOException {
		return service.getUploader(path.getFileName().toString(),
				Files.size(path), configuration);
	}

	public static <S extends UploadService<C>, C extends UploaderConfiguration> Uploader<C> upload(
			S service, Path path) throws IOException {
		return service.getUploader(path.getFileName().toString(),
				Files.size(path));
	}

	public static boolean canUpload(UploadService<?> service, Path path)
			throws IOException {
		return service.getMaximumFilesize() >= Files.size(path);
	}
}
