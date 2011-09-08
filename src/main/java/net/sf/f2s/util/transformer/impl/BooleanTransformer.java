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
package net.sf.f2s.util.transformer.impl;

import net.sf.f2s.util.transformer.Transformer;

/**
 * @author rogiel
 *
 */
public class BooleanTransformer implements Transformer<Boolean> {
	@Override
	public Boolean transform(String data) {
		return Boolean.parseBoolean(data);
	}
}
