/**
 * 
 */
package com.rogiel.httpchannel.util;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 *
 */
public class Filesizes {
	public static long kb(long kb) {
		return kb * 1024;
	}
	
	public static long mb(long mb) {
		return kb(mb) * 1024;
	}
	
	public static long gb(long gb) {
		return mb(gb) * 1024;
	}
}
