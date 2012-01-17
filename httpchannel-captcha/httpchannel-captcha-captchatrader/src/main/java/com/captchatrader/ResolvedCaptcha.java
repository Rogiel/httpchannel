/**
 * 
 */
package com.captchatrader;

import java.io.IOException;

import com.captchatrader.exception.CaptchaTraderException;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class ResolvedCaptcha {
	private final CaptchaTrader api;
	private final String id;
	private final String answer;

	public ResolvedCaptcha(CaptchaTrader api, String id, String answer) {
		this.api = api;
		this.id = id;
		this.answer = answer;
	}

	public String getID() {
		return id;
	}

	public String getAnswer() {
		return answer;
	}

	public void valid() throws CaptchaTraderException, IOException {
		api.respond(this, true);
	}

	public void invalid() throws CaptchaTraderException, IOException {
		api.respond(this, true);
	}
}
