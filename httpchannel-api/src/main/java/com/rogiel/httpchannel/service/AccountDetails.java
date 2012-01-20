/**
 * 
 */
package com.rogiel.httpchannel.service;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public interface AccountDetails {
	String getUsername();
	
	AuthenticationService<?> getService();
}
