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
 * Base exception for any {@link Service}.
 * 
 * @author Rogiel
 * @since 1.0
 */
public class ServiceException extends IOException {
	private static final long serialVersionUID = 1L;

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(Throwable t) {
		super(t);
	}

	public ServiceException(String message, Throwable t) {
		super(message, t);
	}
}
