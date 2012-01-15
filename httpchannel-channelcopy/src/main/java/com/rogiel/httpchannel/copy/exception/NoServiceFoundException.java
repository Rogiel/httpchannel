package com.rogiel.httpchannel.copy.exception;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class NoServiceFoundException extends ChannelCopyException {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance
	 */
	public NoServiceFoundException() {
	}

	/**
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public NoServiceFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 *            the message
	 */
	public NoServiceFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 *            the cause
	 */
	public NoServiceFoundException(Throwable cause) {
		super(cause);
	}
}
