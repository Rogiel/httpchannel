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

import com.rogiel.httpchannel.captcha.exception.AuthenticationCaptchaServiceException;
import com.rogiel.httpchannel.captcha.exception.InvalidCaptchaServiceException;
import com.rogiel.httpchannel.captcha.exception.UnsolvableCaptchaServiceException;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public interface CaptchaService<C extends Captcha> {
	/**
	 * Authenticates the service instance into the CAPTCHA solving server, if
	 * any. Implementations that do not use authentication, should do nothing
	 * and return <code>-1</code>
	 * 
	 * @param username
	 * @param password
	 * @return amount of remaining CAPTCHA solving, <code>-1</code> if no limit
	 *         is applied
	 */
	int authenticate(String username, String password)
			throws AuthenticationCaptchaServiceException;

	/**
	 * Tries to resolve the captcha and returns the correct answer.
	 * 
	 * @param captcha
	 * @throws UnsolvableCaptchaServiceException
	 *             if the CAPTCHA could not be solved by the service
	 */
	void solve(C captcha) throws UnsolvableCaptchaServiceException;

	/**
	 * Notifies the service that the given captcha answer was correct
	 * 
	 * @param captcha
	 *            the captcha
	 * @throws InvalidCaptchaServiceException
	 *             normally thrown if the CAPTCHA was not solved by this service
	 */
	void valid(C captcha) throws InvalidCaptchaServiceException;

	/**
	 * Notifies the service that the given captcha answer was incorrect
	 * 
	 * @param captcha
	 * @throws InvalidCaptchaServiceException
	 *             normally thrown if the CAPTCHA was not solved by this service
	 */
	void invalid(C captcha) throws InvalidCaptchaServiceException;
}
