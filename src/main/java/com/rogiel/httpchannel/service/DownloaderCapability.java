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
 * Capability an certain {@link Downloader} can have.
 * 
 * @author Rogiel
 * @since 1.0
 */
public enum DownloaderCapability {
	/**
	 * Can download files while not authenticated
	 */
	UNAUTHENTICATED_DOWNLOAD,
	/**
	 * Can download files while authenticated with non-premium account
	 */
	NON_PREMIUM_ACCOUNT_DOWNLOAD,
	/**
	 * Can download files while authenticated with premium account
	 */
	PREMIUM_ACCOUNT_DOWNLOAD,
	/**
	 * Resume interrupted downloads are possible and supported.
	 */
	RESUME,
	/**
	 * Can check the status of the given link before starting download.
	 */
	STATUS_CHECK;
}
