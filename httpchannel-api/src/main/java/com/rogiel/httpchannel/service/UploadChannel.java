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

import java.io.IOException;
import java.net.URI;
import java.nio.channels.Channel;
import java.nio.channels.WritableByteChannel;

import com.rogiel.httpchannel.service.exception.UploadLinkNotFoundException;

/**
 * This is an {@link Channel} for uploads. Any data to be uploaded, must be
 * written into this channel.
 * <p>
 * Since this {@link Channel} <tt>implements</tt> {@link WritableByteChannel}
 * you can treat it as any other regular IO {@link Channel}.
 * <p>
 * <b>Remember</b>: always close the {@link Channel}, if you do otherwise, your
 * upload will not finish and will never return the link.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public interface UploadChannel extends HttpChannel, WritableByteChannel {
	/**
	 * The link is located after you call {@link UploadChannel#close()}, but it
	 * can only be retrieved by calling this method. If {@link #close()} throwed
	 * an exception, this method might return <tt>null</tt>.
	 * 
	 * @return the download link for this upload
	 */
	URI getDownloadLink();

	/**
	 * @return the service instance providing this upload
	 */
	@Override
	UploadService<?> getService();

	/**
	 * @return the {@link Uploader} providing this upload
	 */
	Uploader<?> getUploader();

	/**
	 * @throws UploadLinkNotFoundException
	 *             if after the upload, the download link cannot be found
	 */
	@Override
	void close() throws IOException, UploadLinkNotFoundException;
}
