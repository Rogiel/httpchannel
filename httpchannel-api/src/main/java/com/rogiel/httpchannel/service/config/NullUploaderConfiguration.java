package com.rogiel.httpchannel.service.config;

import com.rogiel.httpchannel.service.UploadService;
import com.rogiel.httpchannel.service.Uploader.UploaderConfiguration;

/**
 * An default {@link UploaderConfiguration} implementation that is generally
 * returned by {@link UploadService#newUploaderConfiguration()} when the service
 * does not support or require any kind of configuration.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public final class NullUploaderConfiguration implements UploaderConfiguration {
	public static final NullUploaderConfiguration SHARED_INSTANCE = new NullUploaderConfiguration();
}
