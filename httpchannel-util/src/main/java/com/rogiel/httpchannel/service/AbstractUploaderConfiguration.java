/**
 * 
 */
package com.rogiel.httpchannel.service;

import com.rogiel.httpchannel.service.Uploader.UploaderConfiguration;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class AbstractUploaderConfiguration implements UploaderConfiguration {
	@Override
	public boolean is(Class<? extends UploaderConfiguration> type) {
		return type.isAssignableFrom(this.getClass());
	}

	@Override
	public <T extends UploaderConfiguration> T as(Class<T> type) {
		if (!is(type))
			return null;
		return type.cast(this);
	}
}
