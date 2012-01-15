package com.rogiel.httpchannel.service;

import com.rogiel.httpchannel.service.Authenticator.AuthenticatorConfiguration;

/**
 * An abstract {@link Authenticator} that implements most of the general-purpose
 * methods
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 * @param <C>
 *            the {@link Authenticator} configuration object type
 */
public abstract class AbstractAuthenticator<C extends AuthenticatorConfiguration>
		implements Authenticator<C> {
	protected final Credential credential;
	/**
	 * The {@link Authenticator} configuration
	 */
	protected final C configuration;

	/**
	 * Creates a new instance
	 * 
	 * @param credential
	 *            the authentication credential
	 * @param configuration
	 *            the configuration object
	 */
	protected AbstractAuthenticator(Credential credential, C configuration) {
		this.credential = credential;
		this.configuration = configuration;
	}

	@Override
	public C getConfiguration() {
		return configuration;
	}
}
