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

import java.io.Serializable;

import com.rogiel.httpchannel.service.helper.Services;

/**
 * An ID used to represent the given service
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class ServiceID implements Serializable, Cloneable {
	/**
	 * This class serialization version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The raw ID
	 */
	private final String id;

	/**
	 * @param id
	 *            the raw id
	 */
	private ServiceID(final String id) {
		this.id = id;
	}

	/**
	 * @return the raw id
	 */
	public String getRawID() {
		return id;
	}

	/**
	 * @param id
	 *            the raw id
	 */
	public static ServiceID create(String id) {
		return new ServiceID(id);
	}

	/**
	 * Returns the service associated to this ID. This has the same effect as
	 * calling:
	 * 
	 * <pre>
	 * <code> 	ServiceID id = ...;
	 * 	Services.getService(id);</code>
	 * </pre>
	 * 
	 * @return the associated service, if any.
	 */
	public Service getService() {
		return Services.getService(this);
	}

	@Override
	public ServiceID clone() {
		try {
			return (ServiceID) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceID other = (ServiceID) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
