/**
 * 
 */
package com.rogiel.httpchannel.captcha.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.captchatrader.CaptchaTrader;
import com.captchatrader.ResolvedCaptcha;
import com.captchatrader.exception.CaptchaTraderException;
import com.rogiel.httpchannel.captcha.ImageCaptcha;
import com.rogiel.httpchannel.captcha.ImageCaptchaService;
import com.rogiel.httpchannel.captcha.exception.AuthenticationCaptchaServiceException;
import com.rogiel.httpchannel.captcha.exception.InvalidCaptchaServiceException;
import com.rogiel.httpchannel.captcha.exception.UnsolvableCaptchaServiceException;

/**
 * Implements CAPTCHA solving using CaptchaTrader.com service. If
 * username-password authentication is not desired, send the API key as username
 * and <code>null</code> password.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class CaptchaTraderService implements ImageCaptchaService {
	private static final Logger logger = LoggerFactory
			.getLogger(CaptchaTraderService.class);
	private static final String APP_KEY = "2acc44805ec208cc4d6b00c75a414996";

	/**
	 * The current application key to be used
	 */
	private String currentApplicationKey = CaptchaTraderService.APP_KEY;

	/**
	 * The CaptchaTrader.com API object
	 */
	private CaptchaTrader api;

	@Override
	public int authenticate(String username, String password)
			throws AuthenticationCaptchaServiceException {
		api = new CaptchaTrader(currentApplicationKey, username, password);
		logger.debug("Authenticating into CaptchaTrader");
		try {
			// this will validate the account
			return (int) Math.ceil(((double) api.getCredits()) / 10);
		} catch (IOException | CaptchaTraderException e) {
			throw new AuthenticationCaptchaServiceException(e);
		}
	}

	@Override
	public void solve(ImageCaptcha captcha)
			throws UnsolvableCaptchaServiceException {
		try {
			logger.debug("Resolving CAPTCHA {}", captcha.getImageURI());
			final ResolvedCaptcha resolved = api.submit(captcha.getImageURI());
			captcha.setAnswer(resolved.getAnswer());
			captcha.setAttachment(resolved);
			logger.debug("CAPTCHA solved, answer is \"{}\"",
					resolved.getAnswer());
		} catch (IOException | CaptchaTraderException e) {
			throw new UnsolvableCaptchaServiceException(e);
		}
	}

	@Override
	public void valid(ImageCaptcha captcha)
			throws InvalidCaptchaServiceException {
		final Object attachment = captcha.getAttachment();
		if (attachment instanceof ResolvedCaptcha) {
			try {
				logger.debug("Notifying server that CAPTCHA {} is valid",
						captcha);
				((ResolvedCaptcha) attachment).valid();
			} catch (CaptchaTraderException | IOException e) {
				throw new InvalidCaptchaServiceException(e);
			}
		} else {
			throw new InvalidCaptchaServiceException();
		}
	}

	@Override
	public void invalid(ImageCaptcha captcha)
			throws InvalidCaptchaServiceException {
		final Object attachment = captcha.getAttachment();
		if (attachment instanceof ResolvedCaptcha) {
			try {
				logger.debug("Notifying server that CAPTCHA {} is invalid",
						captcha);
				((ResolvedCaptcha) attachment).invalid();
			} catch (CaptchaTraderException | IOException e) {
				throw new InvalidCaptchaServiceException(e);
			}
		} else {
			throw new InvalidCaptchaServiceException();
		}
	}

	/**
	 * Sets the CaptchaTrader API key. Each application should provide a key,
	 * otherwise the default "HttpChannel" key will be used.
	 * 
	 * @param key
	 *            the application key
	 */
	public void setApplicationKey(String key) {
		if (key == null)
			key = APP_KEY;
		logger.debug("Setting new application key as {}", key);
		currentApplicationKey = key;
	}
}
