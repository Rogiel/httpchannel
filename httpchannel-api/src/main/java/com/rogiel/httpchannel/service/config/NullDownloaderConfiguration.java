package com.rogiel.httpchannel.service.config;

import com.rogiel.httpchannel.service.DownloadService;
import com.rogiel.httpchannel.service.Downloader.DownloaderConfiguration;

/**
 * An default {@link DownloaderConfiguration} implementation that is generally
 * returned by {@link DownloadService#newDownloaderConfiguration()} when the
 * service does not support or require any kind of configuration.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public final class NullDownloaderConfiguration implements
		DownloaderConfiguration {
	public static final NullDownloaderConfiguration SHARED_INSTANCE = new NullDownloaderConfiguration();

	private NullDownloaderConfiguration() {
	}
}
