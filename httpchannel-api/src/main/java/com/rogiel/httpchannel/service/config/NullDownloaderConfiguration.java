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
package com.rogiel.httpchannel.service.config;

import com.rogiel.httpchannel.service.DownloadService;
import com.rogiel.httpchannel.service.Downloader.DownloaderConfiguration;

/**
 * An default {@link DownloaderConfiguration} implementation that is generally
 * returned by {@link DownloadService#newDownloaderConfiguration()} when the
 * service does not support or require any kind of configuration.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public final class NullDownloaderConfiguration implements
		DownloaderConfiguration {
	public static final NullDownloaderConfiguration SHARED_INSTANCE = new NullDownloaderConfiguration();

	private NullDownloaderConfiguration() {
	}

	@Override
	public boolean is(Class<? extends DownloaderConfiguration> type) {
		return false;
	}

	@Override
	public <T extends DownloaderConfiguration> T as(Class<T> type) {
		return null;
	}
}
