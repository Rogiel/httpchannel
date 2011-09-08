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

/**
 * Implements an service capable of authenticating into an account.
 * 
 * @author Rogiel
 * @since 1.0
 */
public interface AuthenticationService extends Service {
	/**
	 * Creates {@link Authenticator} instance for this service. This instance is
	 * attached to an {@link Credential} and to its parent {@link Service}.
	 * 
	 * @param credential
	 *            the credential
	 * @return an new {@link Authenticator} instance
	 */
	Authenticator getAuthenticator(Credential credential);

	/**
	 * Return the matrix of capabilities for this {@link Authenticator}.
	 * 
	 * @return {@link CapabilityMatrix} with all capabilities of this
	 *         {@link Authenticator}.
	 * @see AuthenticatorCapability
	 */
	CapabilityMatrix<AuthenticatorCapability> getAuthenticationCapability();
}
