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
 * This listener keeps an track on the progress on an {@link Downloader}
 * service.
 * 
 * @author Rogiel
 * @since 1.0
 */
public interface DownloadListener {
	/**
	 * Inform that the downloader will be waiting for an certain amount of time
	 * due to an timer in the download site.
	 * 
	 * @param time
	 *            the time in ms in which the service will be be waiting.
	 * @return true if desires to wait, false otherwise
	 */
	boolean timer(long time);
}
