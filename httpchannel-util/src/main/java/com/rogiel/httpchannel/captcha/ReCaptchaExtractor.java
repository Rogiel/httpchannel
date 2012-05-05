/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.rogiel.httpchannel.captcha;

import java.io.IOException;
import java.net.URI;
import java.util.regex.Pattern;

import com.rogiel.httpchannel.captcha.ImageCaptcha;
import com.rogiel.httpchannel.http.HttpContext;
import com.rogiel.httpchannel.util.PatternUtils;
import com.rogiel.httpchannel.util.htmlparser.HTMLPage;

/**
 * This class provides utility methods to extract an {@link ImageCaptcha} from
 * an page containing an Google ReCaptcha CAPTCHA embedded into it.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class ReCaptchaExtractor {
	/**
	 * This pattern extracts the SITE-ID from the Ajax ReCaptcha API
	 */
	private static final Pattern CAPTCHA_ID_PATTERN = Pattern
			.compile("Recaptcha\\.create\\(\"([0-9A-z|_|\\-]*)\", ");
	private static final Pattern CAPTCHA_URI_PATTERN = Pattern
			.compile("http://www\\.google\\.com/recaptcha/api/challenge\\?k=([0-9A-z|\\-]*)(&(.*))?");
	private static final Pattern CAPTCHA_IMAGE_PATTERN = Pattern
			.compile("challenge : '(.*)'");

	private static final String CHALLENGE_BASE_URI = "http://www.google.com/recaptcha/api/challenge?ajax=1&k=";
	private static final String IMAGE_BASE_URI = "http://www.google.com/recaptcha/api/image?c=";

	/**
	 * Extracts the {@link ImageCaptcha} for an ReCAPTCHA using the standard JS
	 * include method
	 * 
	 * @param page
	 *            the page
	 * @param ctx
	 *            the {@link HttpContext}
	 * @return the {@link ImageCaptcha} embedded at the given <code>page</code>
	 */
	public static ImageCaptcha extractCaptcha(HTMLPage page, HttpContext ctx) {
		final String uri = page.findScriptSrc(CAPTCHA_URI_PATTERN);
		if (uri == null)
			return null;
		try {
			return doExtract(ctx.get(uri).asString());
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Extracts the {@link ImageCaptcha} for an ReCAPTCHA using the Ajax API
	 * 
	 * @param page
	 *            the page
	 * @param ctx
	 *            the {@link HttpContext}
	 * @return the {@link ImageCaptcha} contained at the given <code>page</code>
	 */
	public static ImageCaptcha extractAjaxCaptcha(HTMLPage page, HttpContext ctx) {
		final String siteID = page.findScript(CAPTCHA_ID_PATTERN, 1);
		try {
			return doExtract(ctx.get(CHALLENGE_BASE_URI + siteID).asString());
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Actually performs the extracting job.
	 * 
	 * @param content
	 *            the page content
	 * @return the {@link ImageCaptcha}
	 */
	private static ImageCaptcha doExtract(String content) {
		final String id = PatternUtils.find(CAPTCHA_IMAGE_PATTERN, content, 1);
		return new ImageCaptcha(id, URI.create(IMAGE_BASE_URI + id));
	}
}
