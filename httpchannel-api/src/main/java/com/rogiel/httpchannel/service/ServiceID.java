/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
