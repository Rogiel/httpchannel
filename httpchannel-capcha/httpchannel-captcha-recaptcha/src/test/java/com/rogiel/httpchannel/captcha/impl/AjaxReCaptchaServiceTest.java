/**
 * 
 */
package com.rogiel.httpchannel.captcha.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class AjaxReCaptchaServiceTest {
	@Test
	public void test() throws MalformedURLException, IOException {
		final String content = IOUtils.toString(new URL(
				"http://www.uploadking.com/WM3PHD9JAY").openStream());
		final AjaxReCaptchaService ajax = new AjaxReCaptchaService();
		ajax.create(content);
	}
}
