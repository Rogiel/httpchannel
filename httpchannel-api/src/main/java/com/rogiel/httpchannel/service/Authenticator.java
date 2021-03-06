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

import com.rogiel.httpchannel.captcha.CaptchaService;
import com.rogiel.httpchannel.captcha.exception.UnsolvableCaptchaServiceException;
import com.rogiel.httpchannel.service.Authenticator.AuthenticatorConfiguration;
import com.rogiel.httpchannel.service.exception.AuthenticationInvalidCredentialException;
import com.rogiel.httpchannel.service.exception.NoCaptchaServiceException;

/**
 * This interfaces provides authentication for an service.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @since 1.0
 */
public interface Authenticator<C extends AuthenticatorConfiguration> {
	/**
	 * Login into the {@link Service}. Once the authentication is done, it is
	 * persistent for the entire service's operation.<br>
	 * <b>Note</b>: If you want to logout the user, see
	 * {@link Authenticator#logout()}
	 * <p>
	 * Return <code>null</code> is only allowed if
	 * {@link AuthenticatorCapability#ACCOUNT_DETAILS} is not supported by the
	 * service.
	 * 
	 * @return the authenticated account {@link AccountDetails}. If
	 *         {@link AuthenticationService#getAuthenticationCapability()}
	 *         contains {@link AuthenticatorCapability#ACCOUNT_DETAILS}
	 *         <code>null</code> cannot be returned. Otherwise,
	 *         <code>null</code> should always be returned.
	 * 
	 * @throws IOException
	 *             if any IO error occur
	 * @throws AuthenticationInvalidCredentialException
	 *             if the credentials are not valid or cannot be used
	 * @throws UnsolvableCaptchaServiceException
	 *             if the service required captcha resolving but no
	 *             {@link CaptchaService} was available or the service did not
	 *             solve the challenge
	 * @throws NoCaptchaServiceException
	 *             if the service required an {@link CaptchaService}
	 *             implementation to be present, but none was available
	 */
	AccountDetails login() throws IOException,
			AuthenticationInvalidCredentialException,
			UnsolvableCaptchaServiceException, NoCaptchaServiceException;

	/**
	 * Logout into the {@link Service}. The session is restored to an not
	 * logged-in state.
	 * 
	 * @throws IOException
	 *             if any IO error occur
	 */
	void logout() throws IOException;

	/**
	 * Returns this {@link Authenticator} configuration.
	 * <p>
	 * <b>IMPORTANT NOTE</b>: You should not modify any configuration within
	 * this configuration object once while the account is authenticated.
	 * Depending on the service, changing any setting could result in an state
	 * where the service cannot be logged out.
	 * 
	 * @return this {@link Authenticator} configuration
	 */
	C getConfiguration();

	/**
	 * This interface must be implemented in order to allow authentication
	 * configuration.
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 */
	public interface AuthenticatorConfiguration {
		/**
		 * Checks whether the configuration object can be casted to
		 * <code>type</code>
		 * 
		 * @param type
		 *            the casting type
		 * @return <code>true</code> if this object can be casted to
		 *         <code>type</code>
		 */
		boolean is(Class<? extends AuthenticatorConfiguration> type);

		/**
		 * Casts this object to <code>type</code>. If cannot be casted,
		 * <code>null</code> is returned.
		 * 
		 * @param type
		 *            the casting type
		 * @return the casted configuration
		 */
		<T extends AuthenticatorConfiguration> T as(Class<T> type);
	}
}
