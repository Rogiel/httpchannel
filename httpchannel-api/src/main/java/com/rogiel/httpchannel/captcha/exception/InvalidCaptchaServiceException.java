/**
 * 
 */
package com.rogiel.httpchannel.captcha.exception;


/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class InvalidCaptchaServiceException extends CaptchaServiceException {
	/**
	 * The Java Serialization API ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance
	 */
	public InvalidCaptchaServiceException() {
	}

	/**
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public InvalidCaptchaServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 *            the message
	 */
	public InvalidCaptchaServiceException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 *            the cause
	 */
	public InvalidCaptchaServiceException(Throwable cause) {
		super(cause);
	}

}
