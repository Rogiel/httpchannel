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

import com.rogiel.httpchannel.service.UploadService;
import com.rogiel.httpchannel.service.Uploader;
import com.rogiel.httpchannel.service.Uploader.UploaderConfiguration;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel.LinkedUploadChannelCloseCallback;

/**
 * An abstract {@link Uploader} that implements most of the general-purpose
 * methods
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 * @param <C>
 *            the {@link Uploader} configuration object type
 */
public abstract class AbstractUploader<C extends UploaderConfiguration>
		implements Uploader<C> {
	/**
	 * The upload service
	 */
	protected final UploadService<?> service;
	/**
	 * The name of the file to be uploaded
	 */
	protected final String filename;
	/**
	 * The size of the file to be uploaded
	 */
	protected final long filesize;

	/**
	 * The {@link Uploader} configuration
	 */
	protected final C configuration;

	/**
	 * Creates a new instance
	 * 
	 * @param service
	 *            the upload service
	 * @param filename
	 *            the file name
	 * @param filesize
	 *            the file size
	 * @param configuration
	 *            the configuration object
	 */
	public AbstractUploader(UploadService<?> service, String filename,
			long filesize, C configuration) {
		this.service = service;
		this.filename = filename;
		this.filesize = filesize;
		this.configuration = configuration;
	}

	/**
	 * Creates a new linked channel
	 * 
	 * @param closeCallback
	 *            the close callback
	 * @return a newly created {@link LinkedUploadChannel}
	 */
	protected LinkedUploadChannel createLinkedChannel(
			LinkedUploadChannelCloseCallback closeCallback) {
		return new LinkedUploadChannel(service, this, closeCallback, filesize,
				filename);
	}

	@Override
	public C getConfiguration() {
		return configuration;
	}
}
