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

import java.nio.channels.Channel;
import java.nio.channels.ReadableByteChannel;

/**
 * This is an {@link Channel} for downloads. Any data to be downloaded, must be
 * Redden from this channel.
 * <p>
 * Since this {@link Channel} <tt>implements</tt> {@link ReadableByteChannel}
 * you can treat it as any other regular IO {@link Channel}.
 * <p>
 * <b>Remember</b>: always close the {@link Channel}, if you do otherwise, the
 * resources will not be freed and will consume machine resources.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public interface DownloadChannel extends HttpChannel, ReadableByteChannel {
	/**
	 * @return the service instance providing this download
	 */
	@Override
	DownloadService<?> getService();

	/**
	 * @return the downloader providing this download
	 */
	Downloader<?> getDownloader();
}
