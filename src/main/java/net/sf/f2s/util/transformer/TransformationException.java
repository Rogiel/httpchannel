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
package net.sf.f2s.util.transformer;

/**
 * @author Rogiel
 * @since 1.0
 */
public class TransformationException extends Exception {
	private static final long serialVersionUID = 1L;

	public TransformationException() {
	}

	/**
	 * @param message
	 *            the message
	 */
	public TransformationException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 *            the cause
	 */
	public TransformationException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public TransformationException(String message, Throwable cause) {
		super(message, cause);
	}

}
