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
package com.rogiel.httpchannel.service.impl;

import org.junit.Assert;
import org.junit.Test;

import com.rogiel.httpchannel.service.Service;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class ServiceCloningTest {
	@Test
	public void testDiscovery() {
		Service original = HotFileService.SERVICE_ID.getService();
		Service service = HotFileService.SERVICE_ID.getService().clone();
		
		// set configuration to anything else
		service.setServiceConfiguration(null);
		
		Assert.assertNotSame(service, original);
		Assert.assertNotSame(service.getServiceConfiguration(),
				original.getServiceConfiguration());
	}
}
