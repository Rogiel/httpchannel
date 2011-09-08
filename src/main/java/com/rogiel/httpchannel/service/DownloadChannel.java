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

import java.nio.channels.Channel;
import java.nio.channels.ReadableByteChannel;

/**
 * This is an {@link Channel} for downloads. Any data to be downloaded, must be
 * Redden from this channel.
 * <p>
 * Since this {@link Channel} <tt>implements</tt> {@link ReadableByteChannel}
 * you can treat it as any other regular IO {@link Channel}.
 * <p>
 * <b>Remember</b>: always close the {@link Channel}, if you do otherwise, the
 * resources will not be freed and will consume machine resources.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public interface DownloadChannel extends ReadableByteChannel {
	/**
	 * @return the file size
	 */
	long getFilesize();

	/**
	 * @return the file name
	 */
	String getFilename();
}
