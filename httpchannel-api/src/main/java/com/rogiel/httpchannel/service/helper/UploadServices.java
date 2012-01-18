/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
