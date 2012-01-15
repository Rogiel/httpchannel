/**
 * 
 */
package com.rogiel.httpchannel.captcha;

import java.net.URL;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public interface ImageCaptcha extends Captcha {
	/**
	 * @return the captcha identifier
	 */
	String getID();

	/**
	 * @return the captcha image {@link URL}
	 */
	URL getImageURL();
}
