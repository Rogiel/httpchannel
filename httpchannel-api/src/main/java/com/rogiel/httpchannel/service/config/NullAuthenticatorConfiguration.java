package com.rogiel.httpchannel.service.config;

import com.rogiel.httpchannel.service.AuthenticationService;
import com.rogiel.httpchannel.service.Authenticator.AuthenticatorConfiguration;

/**
 * An default {@link AuthenticatorConfiguration} implementation that is
 * generally returned by
 * {@link AuthenticationService#newAuthenticatorConfiguration()} when the
 * service does not support or require any kind of configuration.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public final class NullAuthenticatorConfiguration implements
		AuthenticatorConfiguration {
	public static final NullAuthenticatorConfiguration SHARED_INSTANCE = new NullAuthenticatorConfiguration();
}
