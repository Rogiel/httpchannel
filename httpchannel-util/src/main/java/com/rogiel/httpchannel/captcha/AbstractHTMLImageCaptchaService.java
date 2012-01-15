/**
 * 
 */
package com.rogiel.httpchannel.captcha;

import java.io.IOException;

import com.rogiel.httpchannel.util.htmlparser.HTMLPage;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public abstract class AbstractHTMLImageCaptchaService<C extends AbstractImageCaptcha>
		extends AbstractImageCaptchaService<C> {
	@Override
	public final C create(String html) {
		try {
			return create(HTMLPage.parse(html));
		} catch (IOException e) {
			return null;
		}
	}

	public abstract C create(HTMLPage page) throws IOException;
}
