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
package com.rogiel.httpchannel.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtils {
	public static String find(Pattern pattern, String text) {
		return find(pattern, text, 0);
	}

	public static String find(Pattern pattern, String text, int n) {
		final Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			return matcher.group(n);
		}
		return null;
	}

	public static String find(Pattern pattern, String text, int index, int n) {
		final Matcher matcher = pattern.matcher(text);
		int found = 0;
		while (matcher.find() && (++found) < index) {
		}
		return (found == 0 ? null : matcher.group(n));
	}

	public static String match(Pattern pattern, String text) {
		return match(pattern, text, 0);
	}

	public static String match(Pattern pattern, String text, int n) {
		final Matcher matcher = pattern.matcher(text);
		if (matcher.matches()) {
			return matcher.group(n);
		}
		return null;
	}
}
