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

import java.io.IOException;
import java.net.URI;

import com.rogiel.httpchannel.service.DownloadService;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class DownloadServices {
	/**
	 * Checks whether the given <code>uri</code> can be downloaded with the
	 * {@link DownloadService} <code>service</code>.
	 * 
	 * @param service
	 *            the {@link DownloadService}
	 * @param uri
	 *            the checking {@link URI}
	 * @return <code>true</code> if this {@link URI} can be downloaded with
	 *         <code>service</code>
	 * @throws IOException
	 *             if any exception is thrown while checking
	 */
	public static boolean canDownload(DownloadService<?> service, URI uri)
			throws IOException {
		return service.matchURI(uri);
	}
}
