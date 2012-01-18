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
package com.rogiel.httpchannel.service.exception;

/**
 * Exception thrown if the download limit has been exceeded.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class DownloadLimitExceededException extends DownloadServiceException {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new empty instance of this exception
	 */
	public DownloadLimitExceededException() {
		super();
	}

	/**
	 * @param message
	 *            the message
	 * @param cause
	 *            the root cause
	 */
	public DownloadLimitExceededException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 *            the message
	 */
	public DownloadLimitExceededException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 *            the root cause
	 */
	public DownloadLimitExceededException(Throwable cause) {
		super(cause);
	}
}
