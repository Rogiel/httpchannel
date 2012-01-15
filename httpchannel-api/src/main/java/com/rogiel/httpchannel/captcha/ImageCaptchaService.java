/**
 * 
 */
package com.rogiel.httpchannel.captcha;

import java.net.URL;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public interface ImageCaptchaService<C extends ImageCaptcha> extends
		CaptchaService<C> {
	/**
	 * Creates a new captcha from the given HTML content
	 * 
	 * @param image
	 *            the image {@link URL}
	 * @return a new captcha
	 */
	C create(String html);

	/**
	 * Tries to automatically resolve the captcha
	 * 
	 * @param captcha
	 *            the captcha to be resolved
	 * @return <code>true</code> if the captcha was successfully resolved
	 */
	boolean resolve(C captcha);
}
