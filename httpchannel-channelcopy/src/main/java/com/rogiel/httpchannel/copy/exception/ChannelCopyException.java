package com.rogiel.httpchannel.copy.exception;

import com.rogiel.httpchannel.service.exception.ChannelServiceException;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class ChannelCopyException extends ChannelServiceException {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance
	 */
	public ChannelCopyException() {
	}

	/**
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public ChannelCopyException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 *            the message
	 */
	public ChannelCopyException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 *            the cause
	 */
	public ChannelCopyException(Throwable cause) {
		super(cause);
	}
}
