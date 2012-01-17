/**
 * 
 */
package com.captchatrader.exception;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class SubmissionErrorException extends CaptchaTraderException {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public SubmissionErrorException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public SubmissionErrorException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public SubmissionErrorException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SubmissionErrorException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public SubmissionErrorException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
