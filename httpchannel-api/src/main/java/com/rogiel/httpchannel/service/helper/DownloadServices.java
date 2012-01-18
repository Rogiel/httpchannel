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
import java.net.URI;

import com.rogiel.httpchannel.service.DownloadService;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class DownloadServices {
	/**
	 * Checks whether the given <code>uri</code> can be downloaded with the
	 * {@link DownloadService} <code>service</code>.
	 * 
	 * @param service
	 *            the {@link DownloadService}
	 * @param uri
	 *            the checking {@link URI}
	 * @return <code>true</code> if this {@link URI} can be downloaded with
	 *         <code>service</code>
	 * @throws IOException
	 *             if any exception is thrown while checking
	 */
	public static boolean canDownload(DownloadService<?> service, URI uri)
			throws IOException {
		return service.matchURI(uri);
	}
}
