/**
 * 
 */
package com.rogiel.httpchannel.captcha;

import java.net.URI;

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
	 * The CAPTCHA Image {@link URI}
	 */
	private final URI imageURI;
	/**
	 * The CAPTCHA answer
	 */
	private String answer;
	/**
	 * The CAPTCHA attachment
	 */
	private Object attachment;

	public ImageCaptcha(String id, URI imageURI) {
		this.ID = id;
		this.imageURI = imageURI;
	}

	@Override
	public String getID() {
		return ID;
	}

	public URI getImageURI() {
		return imageURI;
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
		return "ImageCaptcha [ID=" + ID + ", imageURI=" + imageURI
				+ ", answer=" + answer + ", attachment=" + attachment + "]";
	}
}
