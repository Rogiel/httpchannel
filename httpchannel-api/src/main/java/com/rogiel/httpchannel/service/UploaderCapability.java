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
 * Capability an certain {@link Uploader} can have.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @since 1.0
 */
public enum UploaderCapability {
	/**
	 * Can upload while not authenticated into any account
	 */
	UNAUTHENTICATED_UPLOAD,
	/**
	 * Can upload while authenticated with a non-premium account
	 */
	NON_PREMIUM_ACCOUNT_UPLOAD,
	/**
	 * Can upload while authenticated with a premium account
	 */
	PREMIUM_ACCOUNT_UPLOAD;
}
