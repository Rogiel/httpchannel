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

/**
 * This listener keeps an track on the Authentication process.
 * 
 * @author Rogiel
 * @since 1.0
 */
public interface AuthenticatorListener {
	/**
	 * The username and password informed was not valid.
	 * 
	 * @param credential
	 *            the authenticating credential
	 */
	void invalidCredentials(Credential credential);

	/**
	 * The username and password informed was valid. User is authenticated.
	 * 
	 * @param credential
	 *            the authenticating credential
	 */
	void loginSuccessful(Credential credential);

	void logout(Credential credential);
	
	void exception(IOException e) throws IOException;
}
