/**
 * 
 */
package com.rogiel.httpchannel.captcha.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Pattern;

import com.rogiel.httpchannel.util.htmlparser.HTMLPage;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class EmbeddedReCaptchaService extends BaseReCaptchaService {
	private static final Pattern CAPTCHA_URL_PATTERN = Pattern
			.compile("http://www\\.google\\.com/recaptcha/api/challenge\\?k=([0-9A-z|\\-]*)(&(.*))?");

	@Override
	public ReCaptcha create(String content) throws MalformedURLException {
		try {
			return create(HTMLPage.parse(content));
		} catch (IOException e) {
			return null;
		}
	}

	public ReCaptcha create(HTMLPage page) throws IOException {
		final String url = page.findScriptSrc(CAPTCHA_URL_PATTERN);
		if (url == null)
			return null;
		return super.create(get(url).asString());
	}

	@Override
	public boolean resolve(ReCaptcha captcha) {
		// not supported!
		return false;
	}
}
