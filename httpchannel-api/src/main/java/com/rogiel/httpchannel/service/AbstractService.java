/*
 * This file is part of seedbox <github.com/seedbox>.
 *
 * seedbox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * seedbox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with seedbox.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.rogiel.httpchannel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rogiel.httpchannel.captcha.Captcha;
import com.rogiel.httpchannel.captcha.CaptchaService;
import com.rogiel.httpchannel.captcha.exception.UnsolvableCaptchaServiceException;
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
	 * The currently active service mode
	 */
	protected ServiceMode serviceMode = ServiceMode.UNAUTHENTICATED;

	/**
	 * This service {@link CaptchaService} that is used to resolve CAPTCHAS
	 */
	protected CaptchaService<Captcha> captchaService;

	@Override
	public ServiceMode getServiceMode() {
		return serviceMode;
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
