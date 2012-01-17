/**
 * 
 */
package com.rogiel.httpchannel.captcha.exception;


/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class AuthenticationCaptchaServiceException extends CaptchaServiceException {
	/**
	 * The Java Serialization API ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance
	 */
	public AuthenticationCaptchaServiceException() {
	}

	/**
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public AuthenticationCaptchaServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 *            the message
	 */
	public AuthenticationCaptchaServiceException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 *            the cause
	 */
	public AuthenticationCaptchaServiceException(Throwable cause) {
		super(cause);
	}

}
