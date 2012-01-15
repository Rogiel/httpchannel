/**
 * 
 */
package com.rogiel.httpchannel.captcha.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Pattern;

import com.rogiel.httpchannel.util.PatternUtils;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class AjaxReCaptchaService extends BaseReCaptchaService {
	private static final Pattern CAPTCHA_URL_PATTERN = Pattern
			.compile("Recaptcha\\.create\\(\"([0-9A-z|_|\\-]*)\", ");
	private static final String BASE_URL = "http://www.google.com/recaptcha/api/challenge?ajax=1&k=";

	@Override
	public ReCaptcha create(String content) throws MalformedURLException {
		final String siteID = PatternUtils
				.find(CAPTCHA_URL_PATTERN, content, 1);
		try {
			return super.create(get(BASE_URL + siteID).asString());
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public boolean resolve(ReCaptcha captcha) {
		// not supported!
		return false;
	}
}
