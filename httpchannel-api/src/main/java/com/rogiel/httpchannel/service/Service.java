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

import com.rogiel.httpchannel.service.config.ServiceConfiguration;

/**
 * Base interface for all the services. Whenever the operation suported by the
 * {@link Service} it must implement this interface. Most of implementations
 * will benefit from abstract instances of this interface:
 * <ul>
 * <li>{@link AbstractHttpService}: provides an basic support for HTTP services.
 * </li>
 * </ul>
 * 
 * @author Rogiel
 * @since 1.0
 */
public interface Service extends Cloneable {
	/**
	 * Get the {@link ServiceID}.
	 * 
	 * @return the id of the service
	 */
	ServiceID getID();

	/**
	 * Get Major version of this service
	 * 
	 * @return the major version
	 */
	int getMajorVersion();

	/**
	 * Get the minor version of this service
	 * 
	 * @return the minor version
	 */
	int getMinorVersion();

	/**
	 * Returns this {@link ServiceConfiguration} instance
	 * 
	 * @return the {@link ServiceConfiguration} instance
	 */
	ServiceConfiguration getServiceConfiguration();

	/**
	 * Sets this {@link ServiceConfiguration} instance
	 * 
	 * @param configuration
	 *            the {@link ServiceConfiguration} instance
	 */
	void setServiceConfiguration(ServiceConfiguration configuration);

	/**
	 * @return a cloned version of this service
	 */
	Service clone();
}
