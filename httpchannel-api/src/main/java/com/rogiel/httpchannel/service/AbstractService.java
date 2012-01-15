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

import com.rogiel.httpchannel.captcha.Captcha;
import com.rogiel.httpchannel.captcha.CaptchaResolver;
import com.rogiel.httpchannel.service.exception.UnresolvedCaptchaException;

/**
 * This is an abstract {@link Service} implementation.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @version 1.0
 */
public abstract class AbstractService implements Service {
	protected CaptchaResolver captchaResolver;

	@Override
	public Service clone() {
		try {
			return (Service) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public void setCaptchaResolver(CaptchaResolver captchaResolver) {
		this.captchaResolver = captchaResolver;
	}

	protected boolean resolveCaptcha(Captcha captcha)
			throws UnresolvedCaptchaException {
		if (captchaResolver == null)
			throw new UnresolvedCaptchaException();
		if (!captchaResolver.resolve(captcha))
			throw new UnresolvedCaptchaException();
		if (captcha.getAnswer() == null)
			throw new UnresolvedCaptchaException();
		return true;
	}
}
