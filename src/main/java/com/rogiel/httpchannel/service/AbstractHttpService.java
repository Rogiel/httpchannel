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

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.rogiel.httpchannel.service.config.ServiceConfiguration;
import com.rogiel.httpchannel.util.AlwaysRedirectStrategy;

/**
 * Abstract base service for HTTP enabled services.
 * 
 * @author Rogiel
 * @since 1.0
 */
public abstract class AbstractHttpService<T extends ServiceConfiguration>
		extends AbstractService<T> implements Service {
	/**
	 * The {@link HttpClient} instance for this service
	 */
	protected DefaultHttpClient client = new DefaultHttpClient();

	protected AbstractHttpService(T configuration) {
		super(configuration);
		client.setRedirectStrategy(new AlwaysRedirectStrategy());
		// client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS,
		// true);
		// client.getParams().setIntParameter(ClientPNames.MAX_REDIRECTS, 10);
		// client.setRedirectStrategy(new DefaultRedirectStrategy());
	}
}
