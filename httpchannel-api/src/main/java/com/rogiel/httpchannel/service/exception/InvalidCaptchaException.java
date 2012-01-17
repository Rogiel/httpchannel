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
package com.rogiel.httpchannel.service.exception;

import com.rogiel.httpchannel.captcha.CaptchaService;

/**
 * Exception thrown if the {@link CaptchaService} has returned an invalid
 * captcha
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class InvalidCaptchaException extends ChannelServiceException {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new empty instance of this exception
	 */
	public InvalidCaptchaException() {
		super();
	}

	/**
	 * @param message
	 *            the message
	 * @param cause
	 *            the root cause
	 */
	public InvalidCaptchaException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 *            the message
	 */
	public InvalidCaptchaException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 *            the root cause
	 */
	public InvalidCaptchaException(Throwable cause) {
		super(cause);
	}
}
