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
 * This is an utility class to help manage Capabilities of all the services.
 * 
 * @author Rogiel
 * @param <T>
 *            the capability enumeration
 * @since 1.0
 */
public class CapabilityMatrix<T> {
	/**
	 * The list of all supported capabilities
	 */
	private final T[] matrix;

	/**
	 * Creates a new matrix of capabilities
	 * 
	 * @param matrix
	 *            all the capabilities this service support
	 */
	public CapabilityMatrix(T... matrix) {
		this.matrix = matrix;
	}

	/**
	 * Check whether an certatin capability is in the matrix or not.
	 * 
	 * @param capability
	 *            the capability being searched in the matrix
	 * @return true if existent, false otherwise
	 */
	public boolean has(T capability) {
		for (final T capScan : matrix) {
			if (capScan == capability) {
				return true;
			}
		}
		return false;
	}
}
