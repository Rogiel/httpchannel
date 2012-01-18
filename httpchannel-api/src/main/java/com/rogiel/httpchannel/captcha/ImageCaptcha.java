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
