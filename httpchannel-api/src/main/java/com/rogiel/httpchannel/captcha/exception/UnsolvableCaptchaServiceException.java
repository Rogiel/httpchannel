/**
 * 
 */
package com.rogiel.httpchannel.captcha.exception;


/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class UnsolvableCaptchaServiceException extends CaptchaServiceException {
	/**
	 * The Java Serialization API ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance
	 */
	public UnsolvableCaptchaServiceException() {
	}

	/**
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public UnsolvableCaptchaServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 *            the message
	 */
	public UnsolvableCaptchaServiceException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 *            the cause
	 */
	public UnsolvableCaptchaServiceException(Throwable cause) {
		super(cause);
	}

}
