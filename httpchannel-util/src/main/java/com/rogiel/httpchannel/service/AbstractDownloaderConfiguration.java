/**
 * 
 */
package com.rogiel.httpchannel.service;

import com.rogiel.httpchannel.service.Downloader.DownloaderConfiguration;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class AbstractDownloaderConfiguration implements DownloaderConfiguration {
	@Override
	public boolean is(Class<? extends DownloaderConfiguration> type) {
		return type.isAssignableFrom(this.getClass());
	}

	@Override
	public <T extends DownloaderConfiguration> T as(Class<T> type) {
		if (!is(type))
			return null;
		return type.cast(this);
	}
}
