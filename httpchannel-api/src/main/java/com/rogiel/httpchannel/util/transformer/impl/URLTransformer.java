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
package com.rogiel.httpchannel.util.transformer.impl;

import java.net.MalformedURLException;
import java.net.URL;

import com.rogiel.httpchannel.util.transformer.TransformationException;
import com.rogiel.httpchannel.util.transformer.Transformer;


/**
 * @author rogiel
 * 
 */
public class URLTransformer implements Transformer<URL> {
	@Override
	public URL transform(String data) throws TransformationException {
		try {
			return new URL(data);
		} catch (MalformedURLException e) {
			throw new TransformationException(e);
		}
	}
}
