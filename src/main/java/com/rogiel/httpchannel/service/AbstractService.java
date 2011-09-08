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
 * This is an abstract {@link Service} implementation.
 * 
 * @author Rogiel
 * @version 1.0
 * @param <T>
 *            The {@link ServiceConfiguration} <b>interface</b> type used by the
 *            {@link Service}. Note that this <b>must</b> be an interface!s
 * @see ServiceConfiguration ServiceConfiguration for details on the configuration interface.
 */
public abstract class AbstractService<T extends ServiceConfiguration>
		implements Service {
	protected final T configuration;

	protected AbstractService(T configuration) {
		this.configuration = configuration;
	}

	@Override
	public T getServiceConfiguration() {
		return configuration;
	}
}
