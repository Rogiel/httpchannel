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
package net.sf.f2s.util.transformer;

import java.net.URL;

import net.sf.f2s.util.transformer.impl.BooleanTransformer;
import net.sf.f2s.util.transformer.impl.IntegerTransformer;
import net.sf.f2s.util.transformer.impl.LongTransformer;
import net.sf.f2s.util.transformer.impl.StringTransformer;
import net.sf.f2s.util.transformer.impl.URLTransformer;

/**
 * @author Rogiel
 * @since 1.0
 */
public class TransformerFactory {
	@SuppressWarnings("unchecked")
	public static <T> Transformer<T> getTransformer(Class<T> type) {
		if (String.class.isAssignableFrom(type)) {
			return (Transformer<T>) new StringTransformer();
		} else if (Boolean.class.isAssignableFrom(type) || type == Boolean.TYPE) {
			return (Transformer<T>) new BooleanTransformer();
		} else if (Integer.class.isAssignableFrom(type) || type == Integer.TYPE) {
			return (Transformer<T>) new IntegerTransformer();
		} else if (Long.class.isAssignableFrom(type) || type == Long.TYPE) {
			return (Transformer<T>) new LongTransformer();
		} else if (URL.class.isAssignableFrom(type)) {
			return (Transformer<T>) new URLTransformer();
		}
		return null;
	}
}
