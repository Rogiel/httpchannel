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

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public interface Captcha {
	/**
	 * @return the captcha ID
	 */
	String getID();

	/**
	 * @return the resolved captcha answer
	 */
	String getAnswer();

	/**
	 * @param answer
	 *            the captcha answer
	 */
	void setAnswer(String answer);

	/**
	 * @return <code>true</code> if the captcha was resolved and
	 *         {@link #getAnswer()} will not return <code>null</code>.
	 */
	boolean isResolved();

	/**
	 * Get this CAPTCHA's attachment.
	 * <p>
	 * <b>Important note</b>: Attachments are for {@link CaptchaService}
	 * implementations usage! You should not touch any of the attachments!
	 * 
	 * @return the attachment
	 */
	Object getAttachment();

	/**
	 * Sets this CAPTCHA's attachment.
	 * <p>
	 * <b>Important note</b>: Attachments are for {@link CaptchaService}
	 * implementations usage! You should not touch any of the attachments!
	 * 
	 * @param attachment
	 *            the attachment
	 */
	void setAttachment(Object attachment);
}
