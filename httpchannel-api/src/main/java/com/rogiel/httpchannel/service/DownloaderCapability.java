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

/**
 * Capability an certain {@link Downloader} can have.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @since 1.0
 */
public enum DownloaderCapability {
	/**
	 * Can download files while not authenticated
	 */
	UNAUTHENTICATED_DOWNLOAD,
	/**
	 * Can download files while authenticated with non-premium account
	 */
	NON_PREMIUM_ACCOUNT_DOWNLOAD,
	/**
	 * Can download files while authenticated with premium account
	 */
	PREMIUM_ACCOUNT_DOWNLOAD,

	/**
	 * Can resume interrupted downloads even without authenticating
	 */
	UNAUTHENTICATED_RESUME,
	/**
	 * Can resume interrupted downloads but require to be logged with any
	 * account
	 */
	NON_PREMIUM_ACCOUNT_RESUME,
	/**
	 * Can resume interrupted downloads but requires an premium account
	 */
	PREMIUM_ACCOUNT_RESUME,

	/**
	 * Can check the status of the given link before starting download.
	 */
	STATUS_CHECK;
}
