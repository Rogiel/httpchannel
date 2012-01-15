/**
 * 
 */
package com.rogiel.httpchannel.captcha;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public interface Captcha {
	/**
	 * Sets the captcha answer
	 * 
	 * @param answer
	 *            the captcha answer
	 */
	void setAnswer(String answer);

	/**
	 * Returns the captcha answer. <code>null</code> if the service was not able
	 * to resolve it automatically. In such case, {@link #setAnswer(String)}
	 * must be used to set the correct answer.
	 * 
	 * @return the captcha answer
	 */
	String getAnswer();

	/**
	 * @return <code>true</code> if, and only if, the service was able to
	 *         automatically resolve the captcha result
	 */
	boolean wasAutomaticallyResolved();
}
