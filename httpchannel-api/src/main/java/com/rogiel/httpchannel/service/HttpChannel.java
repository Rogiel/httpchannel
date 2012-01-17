/**
 * 
 */
package com.rogiel.httpchannel.service;

import java.io.Closeable;
import java.nio.channels.Channel;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public interface HttpChannel extends Channel, Closeable {
	/**
	 * @return the file size
	 */
	long getFilesize();

	/**
	 * @return the file name
	 */
	String getFilename();
}
