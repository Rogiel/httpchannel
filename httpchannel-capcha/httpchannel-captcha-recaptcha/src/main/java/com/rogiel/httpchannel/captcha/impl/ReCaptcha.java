package com.rogiel.httpchannel.captcha.impl;

import java.net.URL;

import com.rogiel.httpchannel.captcha.AbstractImageCaptcha;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class ReCaptcha extends AbstractImageCaptcha {
	public ReCaptcha(URL url, String ID) {
		super(url, ID);
	}
}
