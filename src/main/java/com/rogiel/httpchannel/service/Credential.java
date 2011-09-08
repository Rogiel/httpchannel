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
 * Pair of username-password used for authenticating into services.
 * 
 * @author Rogiel
 * @since 1.0
 */
public class Credential {
	private final String username;
	private final String password;

	/**
	 * Creates a new pair of username-password credential
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 */
	public Credential(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * Get the username
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Get the password
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
}
