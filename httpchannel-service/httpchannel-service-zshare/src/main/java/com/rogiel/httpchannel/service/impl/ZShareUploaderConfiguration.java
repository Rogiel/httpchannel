package com.rogiel.httpchannel.service.impl;

import com.rogiel.httpchannel.service.Uploader.DescriptionableUploaderConfiguration;
import com.rogiel.httpchannel.service.Uploader.UploaderConfiguration;

/**
 * Describes an configuration for an {@link UploaderImpl}
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class ZShareUploaderConfiguration implements UploaderConfiguration,
		DescriptionableUploaderConfiguration {
	/**
	 * The upload description
	 */
	private String description = DescriptionableUploaderConfiguration.DEFAULT_DESCRIPTION;

	@Override
	public String description() {
		return description;
	}

	@Override
	public ZShareUploaderConfiguration description(String description) {
		this.description = description;
		return this;
	}
}
