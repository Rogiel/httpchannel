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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that defines the default value for an {@link ServiceConfiguration}
 * method.<br>
 * <br>
 * <h1>Usage example</h1>
 * 
 * <pre>
 * public interface DummyServiceConfiguration extends ServiceConfiguration {
 * 	&#064;ServiceConfigurationProperty(defaultValue = &quot;true&quot;)
 * 	boolean retryAllowed();
 * }
 * </pre>
 * 
 * The default implementation created by
 * {@link ServiceConfigurationHelper#defaultConfiguration()} will always return
 * the <tt>defaultValue</tt>.
 * 
 * @author Rogiel
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ServiceConfigurationProperty {
	String key();
	String defaultValue();
}
