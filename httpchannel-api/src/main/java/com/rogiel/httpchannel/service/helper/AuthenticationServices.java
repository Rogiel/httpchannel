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
package com.rogiel.httpchannel.service.helper;

import com.rogiel.httpchannel.service.AuthenticationService;
import com.rogiel.httpchannel.service.Authenticator;
import com.rogiel.httpchannel.service.Authenticator.AuthenticatorConfiguration;
import com.rogiel.httpchannel.service.Credential;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class AuthenticationServices {
	public static <S extends AuthenticationService<C>, C extends AuthenticatorConfiguration> Authenticator<C> authenticate(
			S service, C configuration, String username, String password) {
		return service.getAuthenticator(new Credential(username, password),
				configuration);
	}

	public static <S extends AuthenticationService<C>, C extends AuthenticatorConfiguration> Authenticator<C> authenticate(
			S service, String username, String password) {
		return service.getAuthenticator(new Credential(username, password));
	}
}