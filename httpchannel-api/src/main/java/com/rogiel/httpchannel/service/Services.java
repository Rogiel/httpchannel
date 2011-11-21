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

import java.net.URL;
import java.util.Iterator;
import java.util.ServiceLoader;

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
	 * Tries to detect which service should be used to download the given URL
	 * 
	 * @param url
	 *            the URL
	 * @return the matched service
	 */
	public static DownloadService matchURL(URL url) {
		for (final Service service : iterate()) {
			if (!(service instanceof DownloadService))
				continue;
			if (((DownloadService) service).matchURL(url))
				return (DownloadService) service;
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
			if (service.getID().equals(id))
				return service;
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
						return iterator.next().getID();
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
