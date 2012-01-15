/**
 * 
 */
package com.rogiel.httpchannel.captcha.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import com.rogiel.httpchannel.captcha.AbstractImageCaptchaService;
import com.rogiel.httpchannel.util.PatternUtils;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class BaseReCaptchaService extends
		AbstractImageCaptchaService<ReCaptcha> {
	private static final Pattern CAPTCHA_IMAGE_PATTERN = Pattern
			.compile("challenge : '(.*)'");
	private static final String BASE_URL = "http://www.google.com/recaptcha/api/image?c=";

	@Override
	public ReCaptcha create(String content) throws MalformedURLException {
		final String id = PatternUtils.find(CAPTCHA_IMAGE_PATTERN, content, 1);
		return new ReCaptcha(new URL(BASE_URL + id), id);
	}

	@Override
	public boolean resolve(ReCaptcha captcha) {
		// not supported!
		return false;
	}
}
