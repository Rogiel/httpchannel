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
 * This interfaces provides authentication for an service.
 * 
 * @author Rogiel
 * @since 1.0
 */
public interface Authenticator {
	/**
	 * Login into the {@link Service}. Once the authentication is done, it is
	 * persistent for the entire service's operation.<br>
	 * <b>Note</b>: If you want to logout the user, see
	 * {@link Authenticator#logout()}
	 * 
	 * @return true if login was successful
	 */
	boolean login() throws IOException;

	/**
	 * Logout into the {@link Service}. The session is restored to an not
	 * logged-in state.
	 * 
	 * @return true if logout was successful
	 */
	boolean logout() throws IOException;
}
