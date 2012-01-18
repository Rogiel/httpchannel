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
package com.rogiel.httpchannel.service.helper;

import java.net.URI;
import java.util.Iterator;
import java.util.ServiceLoader;

import com.rogiel.httpchannel.service.DownloadService;
import com.rogiel.httpchannel.service.Service;
import com.rogiel.httpchannel.service.ServiceID;
import com.rogiel.httpchannel.service.UploadService;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class Services {
	private static ServiceLoader<Service> services = ServiceLoader
			.load(Service.class);

	/**
	 * Reloads the list of available services in the classpath
	 * 
	 * @see java.util.ServiceLoader#reload()
	 */
	public static void reload() {
		services.reload();
	}

	/**
	 * Tries to detect which service should be used to download the given URI
	 * 
	 * @param uri
	 *            the URI
	 * @return the matched service
	 */
	public static DownloadService<?> matchURI(URI uri) {
		for (final Service service : iterate()) {
			if (!(service instanceof DownloadService))
				continue;
			if (((DownloadService<?>) service).matchURI(uri))
				return (DownloadService<?>) service;
		}
		return null;
	}

	/**
	 * Tries to detect which service has the given <tt>id</tt>
	 * 
	 * @param id
	 *            the service id
	 * @return the matched service
	 */
	public static Service getService(ServiceID id) {
		for (final Service service : iterate()) {
			if (service.getServiceID().equals(id))
				return service;
		}
		return null;
	}
	
	/**
	 * Tries to detect which service has the given <tt>id</tt>
	 * 
	 * @param id
	 *            the service id
	 * @return the matched service
	 */
	public static UploadService<?> getUploadService(ServiceID id) {
		for (final Service service : iterate()) {
			if (service.getServiceID().equals(id))
				return (UploadService<?>) service;
		}
		return null;
	}
	
	/**
	 * Tries to detect which service has the given <tt>id</tt>
	 * 
	 * @param id
	 *            the service id
	 * @return the matched service
	 */
	public static DownloadService<?> getDownloadService(ServiceID id) {
		for (final Service service : iterate()) {
			if (service.getServiceID().equals(id))
				return (DownloadService<?>) service;
		}
		return null;
	}

	/**
	 * Creates a new {@link Iterable} instance to iterate over services
	 * 
	 * @return the {@link Iterable} instance
	 */
	public static Iterable<Service> iterate() {
		return new Iterable<Service>() {
			@Override
			public Iterator<Service> iterator() {
				return services.iterator();
			}
		};
	}

	/**
	 * Creates a new {@link Iterable} instance to iterate over service ids
	 * 
	 * @return the {@link Iterable} instance
	 */
	public static Iterable<ServiceID> iterateIDs() {
		return new Iterable<ServiceID>() {
			@Override
			public Iterator<ServiceID> iterator() {
				return new Iterator<ServiceID>() {
					private final Iterator<Service> iterator = iterate()
							.iterator();

					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}

					@Override
					public ServiceID next() {
						return iterator.next().getServiceID();
					}

					@Override
					public void remove() {
						iterator.remove();
					}
				};
			}
		};
	}
}
