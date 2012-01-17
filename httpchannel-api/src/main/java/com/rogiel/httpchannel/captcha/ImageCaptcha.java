/**
 * 
 */
package com.rogiel.httpchannel.captcha;

import java.net.URL;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class ImageCaptcha implements Captcha {
	/**
	 * The CAPTCHA ID
	 */
	private final String ID;
	/**
	 * The CAPTCHA Image {@link URL}
	 */
	private final URL imageURL;
	/**
	 * The CAPTCHA answer
	 */
	private String answer;
	/**
	 * The CAPTCHA attachment
	 */
	private Object attachment;

	public ImageCaptcha(String id, URL imageURL) {
		this.ID = id;
		this.imageURL = imageURL;
	}

	@Override
	public String getID() {
		return ID;
	}

	public URL getImageURL() {
		return imageURL;
	}

	@Override
	public String getAnswer() {
		return answer;
	}

	@Override
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@Override
	public boolean isResolved() {
		return answer != null;
	}

	@Override
	public Object getAttachment() {
		return attachment;
	}

	@Override
	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	@Override
	public String toString() {
		return "ImageCaptcha [ID=" + ID + ", imageURL=" + imageURL
				+ ", answer=" + answer + ", attachment=" + attachment + "]";
	}
}
