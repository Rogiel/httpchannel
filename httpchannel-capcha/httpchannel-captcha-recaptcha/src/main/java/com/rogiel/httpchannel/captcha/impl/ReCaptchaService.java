/**
 * 
 */
package com.rogiel.httpchannel.captcha.impl;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

import com.rogiel.httpchannel.captcha.AbstractImageCaptchaService;
import com.rogiel.httpchannel.util.PatternUtils;
import com.rogiel.httpchannel.util.htmlparser.HTMLPage;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class ReCaptchaService extends AbstractImageCaptchaService<ReCaptcha> {
	// http://www.google.com/recaptcha/api/noscript?k=6LdRTL8SAAAAAE9UOdWZ4d0Ky-aeA7XfSqyWDM2m
	private static final Pattern CAPTCHA_URL_PATTERN = Pattern
			.compile("http://www\\.google\\.com/recaptcha/api/challenge\\?k=([0-9A-z|\\-]*)(&(.*))?");
	private static final Pattern CAPTCHA_IMAGE_PATTERN = Pattern
			.compile("challenge : '(.*)'");
	private static final String BASE_URL = "http://www.google.com/recaptcha/api/image?c=";

	@Override
	public ReCaptcha create(HTMLPage page) throws IOException {
		final String url = page.findScriptSrc(CAPTCHA_URL_PATTERN);

		if (url == null)
			return null;
		final String captchaPage = get(url).asString();

		final String id = PatternUtils.find(CAPTCHA_IMAGE_PATTERN, captchaPage,
				1);
		return new ReCaptcha(new URL(BASE_URL + id), id);
	}

	@Override
	public boolean resolve(ReCaptcha captcha) {
		// not supported!
		return false;
	}
}
