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
package com.rogiel.httpchannel.service.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

import net.sf.f2s.util.transformer.TransformerFactory;

/**
 * Helper class for {@link ServiceConfiguration} system.
 * 
 * @author Rogiel
 * @since 1.0
 */
public class ServiceConfigurationHelper {
	/**
	 * Creates a Proxy Class that returns all the default values of
	 * configuration interfaces. The values are mapped by
	 * {@link ServiceConfigurationProperty} annotation.
	 * 
	 * @param <T>
	 *            the interface extending {@link ServiceConfiguration}. Service
	 *            specific.
	 * @param type
	 *            the type Class representing T.
	 * @return the proxied {@link ServiceConfiguration} instance.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends ServiceConfiguration> T defaultConfiguration(
			Class<T> type) {
		return (T) Proxy.newProxyInstance(
				ServiceConfiguration.class.getClassLoader(),
				new Class<?>[] { type }, new InvocationHandler() {
					@Override
					public Object invoke(Object object, Method method,
							Object[] arguments) throws Throwable {
						final ServiceConfigurationProperty property = method
								.getAnnotation(ServiceConfigurationProperty.class);
						if (property != null)
							return TransformerFactory.getTransformer(
									method.getReturnType()).transform(
									property.defaultValue());
						return null;
					}
				});
	}

	/**
	 * Creates a Proxy Class that returns all the default values of
	 * configuration interfaces. The values are mapped by
	 * {@link ServiceConfigurationProperty} annotation.
	 * 
	 * @param <T>
	 *            the interface extending {@link ServiceConfiguration}. Service
	 *            specific.
	 * @param type
	 *            the type Class representing T.
	 * @return the proxied {@link ServiceConfiguration} instance.
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends ServiceConfiguration> T file(Class<T> type,
			File file) throws FileNotFoundException, IOException {
		final Properties properties = new Properties();
		properties.load(new FileInputStream(file));

		return (T) Proxy.newProxyInstance(
				ServiceConfiguration.class.getClassLoader(),
				new Class<?>[] { type }, new InvocationHandler() {
					@Override
					public Object invoke(Object object, Method method,
							Object[] arguments) throws Throwable {
						final ServiceConfigurationProperty property = method
								.getAnnotation(ServiceConfigurationProperty.class);
						if (property != null)
							return TransformerFactory.getTransformer(
									method.getReturnType()).transform(
									get(property));
						return null;
					}

					private String get(ServiceConfigurationProperty property) {
						String value = properties.getProperty(property.key());
						if (value == null)
							value = property.defaultValue();
						return value;
					}
				});
	}
}
