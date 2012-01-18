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
