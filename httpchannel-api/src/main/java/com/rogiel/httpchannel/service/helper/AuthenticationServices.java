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

import com.rogiel.httpchannel.service.AuthenticationService;
import com.rogiel.httpchannel.service.Authenticator;
import com.rogiel.httpchannel.service.Authenticator.AuthenticatorConfiguration;
import com.rogiel.httpchannel.service.Credential;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class AuthenticationServices {
	/**
	 * Creates a new {@link Credential} with <code>username</code> and
	 * <code>password</code> and creates a new {@link Authenticator} with it and
	 * <code>configuration</code>. {@link Authenticator#login()} is not called.
	 * 
	 * @param service
	 *            the service
	 * @param configuration
	 *            the authenticator configuration
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return a newly created {@link Authenticator}
	 */
	public static <S extends AuthenticationService<C>, C extends AuthenticatorConfiguration> Authenticator<C> authenticator(
			S service, C configuration, String username, String password) {
		return service.getAuthenticator(new Credential(username, password),
				configuration);
	}

	/**
	 * Creates a new {@link Credential} with <code>username</code> and
	 * <code>password</code> and creates a new {@link Authenticator} with it.
	 * {@link Authenticator#login()} is not called.
	 * 
	 * @param service
	 *            the service
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return a newly created {@link Authenticator}
	 */
	public static <S extends AuthenticationService<C>, C extends AuthenticatorConfiguration> Authenticator<C> authenticator(
			S service, String username, String password) {
		return service.getAuthenticator(new Credential(username, password));
	}
}
