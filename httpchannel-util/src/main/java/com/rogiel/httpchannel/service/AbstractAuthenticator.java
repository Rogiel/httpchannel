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

/**
 * An abstract {@link Authenticator} that implements most of the general-purpose
 * methods
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 * @param <C>
 *            the {@link Authenticator} configuration object type
 */
public abstract class AbstractAuthenticator<C extends AuthenticatorConfiguration>
		implements Authenticator<C> {
	protected final Credential credential;
	/**
	 * The {@link Authenticator} configuration
	 */
	protected final C configuration;

	/**
	 * Creates a new instance
	 * 
	 * @param credential
	 *            the authentication credential
	 * @param configuration
	 *            the configuration object
	 */
	protected AbstractAuthenticator(Credential credential, C configuration) {
		this.credential = credential;
		this.configuration = configuration;
	}

	@Override
	public C getConfiguration() {
		return configuration;
	}
}
