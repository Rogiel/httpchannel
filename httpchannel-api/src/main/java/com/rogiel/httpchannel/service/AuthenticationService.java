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

import com.rogiel.httpchannel.service.Authenticator.AuthenticatorConfiguration;
import com.rogiel.httpchannel.service.config.NullAuthenticatorConfiguration;

/**
 * Implements an service capable of authenticating into an account.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @since 1.0
 */
public interface AuthenticationService<C extends AuthenticatorConfiguration>
		extends Service {
	/**
	 * Creates {@link Authenticator} instance for this service. This instance is
	 * attached to an {@link Credential} and to its parent {@link Service}.
	 * 
	 * @param credential
	 *            the credential
	 * @param configuration
	 *            the authenticator configuration
	 * @return an new {@link Authenticator} instance
	 */
	Authenticator<C> getAuthenticator(Credential credential, C configuration);

	/**
	 * Creates {@link Authenticator} instance for this service. This instance is
	 * attached to an {@link Credential} and to its parent {@link Service}.
	 * 
	 * @param credential
	 *            the credential
	 * @return an new {@link Authenticator} instance
	 */
	Authenticator<C> getAuthenticator(Credential credential);

	/**
	 * Creates a new configuration object. If a service does not support or
	 * require configuration, {@link NullAuthenticatorConfiguration} should be
	 * returned.
	 * 
	 * @return a new configuration object or
	 *         {@link NullAuthenticatorConfiguration}
	 */
	C newAuthenticatorConfiguration();

	/**
	 * Return the matrix of capabilities for this {@link Authenticator}.
	 * 
	 * @return {@link CapabilityMatrix} with all capabilities of this
	 *         {@link Authenticator}.
	 * @see AuthenticatorCapability
	 */
	CapabilityMatrix<AuthenticatorCapability> getAuthenticationCapability();
}
