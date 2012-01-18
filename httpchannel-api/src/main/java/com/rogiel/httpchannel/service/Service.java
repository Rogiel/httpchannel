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

import com.rogiel.httpchannel.captcha.Captcha;
import com.rogiel.httpchannel.captcha.CaptchaService;

/**
 * Base interface for all the services. Whenever the operation suported by the
 * {@link Service} it must implement this interface. Most of implementations
 * will benefit from abstract instances of this interface:
 * <ul>
 * <li>{@link AbstractHttpService}: provides an basic support for HTTP services.
 * </li>
 * </ul>
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @since 1.0
 */
public interface Service extends Cloneable {
	/**
	 * Get the {@link ServiceID}.
	 * 
	 * @return the id of the service
	 */
	ServiceID getServiceID();

	/**
	 * Get Major version of this service
	 * 
	 * @return the major version
	 */
	int getMajorVersion();

	/**
	 * Get the minor version of this service
	 * 
	 * @return the minor version
	 */
	int getMinorVersion();

	/**
	 * Returns the currently active service mode. The mode is not static and can
	 * be changed with an {@link Authenticator}
	 * 
	 * @return the service mode
	 */
	ServiceMode getServiceMode();

	/**
	 * Return the matrix of supported modes for this {@link Service}.
	 * 
	 * @return {@link CapabilityMatrix} with all supported modes of this
	 *         {@link Service}.
	 * @see DownloaderCapability
	 */
	CapabilityMatrix<ServiceMode> getPossibleServiceModes();

	/**
	 * Sets this service captcha service. CaptchaService are safe to be switched
	 * even after an transfer has begun.
	 * 
	 * @param captchaService
	 *            the captcha service
	 */
	void setCaptchaService(CaptchaService<? extends Captcha> captchaService);

	/**
	 * @return a cloned version of this service
	 */
	Service clone();
}
