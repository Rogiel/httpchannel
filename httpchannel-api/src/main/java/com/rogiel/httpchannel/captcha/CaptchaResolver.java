/**
 * 
 */
package com.rogiel.httpchannel.captcha;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public interface CaptchaResolver {
	/**
	 * Tries to resolve the captcha
	 * 
	 * @param captcha
	 *            the captcha
	 * @return <code>true</code> if the captcha was resolve, <code>false</code>
	 *         to abort
	 */
	boolean resolve(Captcha captcha);
}
