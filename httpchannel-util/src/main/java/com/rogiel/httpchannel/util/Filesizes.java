/**
 * 
 */
package com.rogiel.httpchannel.util;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class Filesizes {
	public static long kb(double kb) {
		return (long) (kb * 1024);
	}

	public static long mb(double mb) {
		return kb(mb) * 1024;
	}

	public static long gb(double gb) {
		return mb(gb) * 1024;
	}

	public static long auto(double value, String unit) {
		unit = unit.toUpperCase().trim().substring(0, 1);
		switch (unit) {
		case "K":
			return kb(value);
		case "M":
			return mb(value);
		case "G":
			return gb(value);
		}
		return 0;
	}
}
