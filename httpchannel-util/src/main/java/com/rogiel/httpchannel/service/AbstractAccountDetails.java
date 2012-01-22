/**
 * 
 */
package com.rogiel.httpchannel.service;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class AbstractAccountDetails implements AccountDetails {
	protected final String username;
	protected final AuthenticationService<?> service;

	public AbstractAccountDetails(AuthenticationService<?> service,
			String username) {
		this.service = service;
		this.username = username;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public AuthenticationService<?> getService() {
		return service;
	}

	@Override
	public boolean is(Class<? extends AccountDetails> type) {
		return type.isAssignableFrom(this.getClass());
	}

	@Override
	public <T extends AccountDetails> T as(Class<T> type) {
		if (!is(type))
			return null;
		return type.cast(this);
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
