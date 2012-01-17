/**
 * 
 */
package com.rogiel.httpchannel.captcha.exception;

import com.rogiel.httpchannel.service.exception.ChannelServiceException;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class CaptchaServiceException extends ChannelServiceException {
	/**
	 * The Java Serialization API ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance
	 */
	public CaptchaServiceException() {
	}

	/**
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public CaptchaServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 *            the message
	 */
	public CaptchaServiceException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 *            the cause
	 */
	public CaptchaServiceException(Throwable cause) {
		super(cause);
	}

}
