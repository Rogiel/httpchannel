package com.rogiel.httpchannel.service.impl;

import com.rogiel.httpchannel.service.Downloader.DownloaderConfiguration;

public class MegaUploadDownloaderConfiguration implements
		DownloaderConfiguration {
	private boolean respectWaitTime = true;

	public boolean getRespectWaitTime() {
		return respectWaitTime;
	}

	public void setRespectWaitTime(boolean respectWaitTime) {
		this.respectWaitTime = respectWaitTime;
	}
}
