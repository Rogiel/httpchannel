/**
 * 
 */
package com.rogiel.httpchannel.service;

import com.rogiel.httpchannel.service.Authenticator.AuthenticatorConfiguration;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class AbstractAuthenticatorConfiguration implements
		AuthenticatorConfiguration {
	@Override
	public boolean is(Class<? extends AuthenticatorConfiguration> type) {
		return type.isAssignableFrom(this.getClass());
	}

	@Override
	public <T extends AuthenticatorConfiguration> T as(Class<T> type) {
		if (!is(type))
			return null;
		return type.cast(this);
	}
}
