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
package com.rogiel.httpchannel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rogiel.httpchannel.captcha.Captcha;
import com.rogiel.httpchannel.captcha.CaptchaService;
import com.rogiel.httpchannel.captcha.exception.UnsolvableCaptchaServiceException;
import com.rogiel.httpchannel.service.AccountDetails.PremiumAccountDetails;
import com.rogiel.httpchannel.service.exception.NoCaptchaServiceException;

/**
 * This is an abstract {@link Service} implementation. Service implementators
 * should try to implement this abstract class instead of directly implementing
 * {@link Service}.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @version 1.0
 */
public abstract class AbstractService implements Service {
	/**
	 * The service {@link Logger} instance
	 */
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The currently active account
	 */
	protected AccountDetails account;

	/**
	 * This service {@link CaptchaService} that is used to resolve CAPTCHAS
	 */
	protected CaptchaService<Captcha> captchaService;

	@Override
	public ServiceMode getServiceMode() {
		if (account == null) {
			return ServiceMode.UNAUTHENTICATED;
		} else {
			if (account.is(PremiumAccountDetails.class)) {
				return (account.as(PremiumAccountDetails.class).isPremium() ? ServiceMode.PREMIUM
						: ServiceMode.NON_PREMIUM);
			} else {
				return ServiceMode.NON_PREMIUM;
			}
		}
	}

	@Override
	public Service clone() {
		try {
			return (Service) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setCaptchaService(
			CaptchaService<? extends Captcha> captchaService) {
		this.captchaService = (CaptchaService<Captcha>) captchaService;
	}

	protected void resolveCaptcha(Captcha captcha)
			throws NoCaptchaServiceException, UnsolvableCaptchaServiceException {
		if (captchaService == null)
			throw new NoCaptchaServiceException(
					"No CaptchaService is configured");
		captchaService.solve((Captcha) captcha);
	}
}
