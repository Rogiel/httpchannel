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
	 * @return the captcha ID
	 */
	String getID();

	/**
	 * @return the resolved captcha answer
	 */
	String getAnswer();

	/**
	 * @param answer
	 *            the captcha answer
	 */
	void setAnswer(String answer);

	/**
	 * @return <code>true</code> if the captcha was resolved and
	 *         {@link #getAnswer()} will not return <code>null</code>.
	 */
	boolean isResolved();

	/**
	 * Get this CAPTCHA's attachment.
	 * <p>
	 * <b>Important note</b>: Attachments are for {@link CaptchaService}
	 * implementations usage! You should not touch any of the attachments!
	 * 
	 * @return the attachment
	 */
	Object getAttachment();

	/**
	 * Sets this CAPTCHA's attachment.
	 * <p>
	 * <b>Important note</b>: Attachments are for {@link CaptchaService}
	 * implementations usage! You should not touch any of the attachments!
	 * 
	 * @param attachment
	 *            the attachment
	 */
	void setAttachment(Object attachment);
}
