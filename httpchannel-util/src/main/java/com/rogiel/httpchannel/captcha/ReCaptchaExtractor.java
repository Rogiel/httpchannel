/**
 * 
 */
package com.rogiel.httpchannel.captcha;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import com.rogiel.httpchannel.http.HttpContext;
import com.rogiel.httpchannel.util.PatternUtils;
import com.rogiel.httpchannel.util.htmlparser.HTMLPage;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class ReCaptchaExtractor {
	private static final Pattern CAPTCHA_ID_PATTERN = Pattern
			.compile("Recaptcha\\.create\\(\"([0-9A-z|_|\\-]*)\", ");
	private static final Pattern CAPTCHA_URL_PATTERN = Pattern
			.compile("http://www\\.google\\.com/recaptcha/api/challenge\\?k=([0-9A-z|\\-]*)(&(.*))?");
	private static final Pattern CAPTCHA_IMAGE_PATTERN = Pattern
			.compile("challenge : '(.*)'");

	private static final String CHALLENGE_BASE_URL = "http://www.google.com/recaptcha/api/challenge?ajax=1&k=";
	private static final String IMAGE_BASE_URL = "http://www.google.com/recaptcha/api/image?c=";

	public static ImageCaptcha extractCaptcha(HTMLPage page, HttpContext ctx) {
		final String url = page.findScriptSrc(CAPTCHA_URL_PATTERN);
		if (url == null)
			return null;
		try {
			return doExtract(ctx.get(url).asString());
		} catch (IOException e) {
			return null;
		}
	}

	public static ImageCaptcha extractAjaxCaptcha(HTMLPage page, HttpContext ctx) {
		final String siteID = page.findScript(CAPTCHA_ID_PATTERN, 1);
		try {
			return doExtract(ctx.get(CHALLENGE_BASE_URL + siteID).asString());
		} catch (IOException e) {
			return null;
		}
	}

	private static ImageCaptcha doExtract(String content) {
		final String id = PatternUtils.find(CAPTCHA_IMAGE_PATTERN, content, 1);
		try {
			return new ImageCaptcha(id, new URL(IMAGE_BASE_URL + id));
		} catch (MalformedURLException e) {
			return null;
		}
	}
}
