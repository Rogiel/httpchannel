/**
 * 
 */
package com.rogiel.httpchannel.service;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.Channel;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public interface HttpChannel extends Channel, Closeable {
	/**
	 * @return the file size
	 */
	long size() throws IOException;

	/**
	 * @return the file name
	 */
	String filename() throws IOException;

	/**
	 * @return the service providing data to this channel
	 */
	Service getService();
}
