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

import java.lang.reflect.Proxy;

import com.rogiel.httpchannel.util.transformer.Transformer;


/**
 * This is an flag interface to indicate that an certain Interface is the
 * configuration for the service.<br>
 * <br>
 * Every service must create an <tt>interface</tt> with the configuration
 * methods, additionally an Annotation informing the default value.
 * ServiceConfiguration implementations might use reflection ({@link Proxy}),
 * hard-coding or any other way for fetching the data.<br>
 * <br>
 * String data stored in the annotation is converted to Java Types using the
 * {@link Transformer} class.
 * 
 * @author Rogiel
 * @version 1.0
 * @see Transformer
 */
public interface ServiceConfiguration {
}
