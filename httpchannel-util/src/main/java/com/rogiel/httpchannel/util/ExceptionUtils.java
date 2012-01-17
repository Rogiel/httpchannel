/**
 * 
 */
package com.rogiel.httpchannel.util;

import java.io.IOException;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class ExceptionUtils {
	public static void asIOException(Exception e) throws IOException {
		final Throwable e2 = e.getCause();
		if (e2 == null) {
			throw new IOException(e);
		} else if (e instanceof IOException) {
			throw (IOException) e;
		} else if (e2 instanceof IOException) {
			throw (IOException) e2;
		} else if (e2 instanceof RuntimeException) {
			throw (RuntimeException) e2;
		} else {
			throw new IOException(e);
		}
	}
}
