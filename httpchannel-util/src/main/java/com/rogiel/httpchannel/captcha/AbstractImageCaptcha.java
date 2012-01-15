/**
 * 
 */
package com.rogiel.httpchannel.captcha;

import java.net.URL;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public abstract class AbstractImageCaptcha implements ImageCaptcha {
	private final URL imageURL;
	private final String ID;

	private String answer;
	private boolean automaticallyResolved;

	public AbstractImageCaptcha(URL imageURL, String ID) {
		this.imageURL = imageURL;
		this.ID = ID;
	}

	@Override
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@Override
	public String getAnswer() {
		return answer;
	}

	@Override
	public boolean wasAutomaticallyResolved() {
		return automaticallyResolved;
	}

	@Override
	public URL getImageURL() {
		return imageURL;
	}

	public String getID() {
		return ID;
	}
}
