/*
 * This file is part of seedbox <github.com/seedbox>.
 *
 * seedbox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * seedbox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with seedbox.  If not, see <http://www.gnu.org/licenses/>.
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
	void login() throws IOException, AuthenticationInvalidCredentialException,
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

	}
}
