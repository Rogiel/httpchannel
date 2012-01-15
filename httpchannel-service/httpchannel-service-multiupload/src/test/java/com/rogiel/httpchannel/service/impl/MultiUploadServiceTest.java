package com.rogiel.httpchannel.service.impl;

import java.net.MalformedURLException;
import java.net.URL;

import com.rogiel.httpchannel.service.AbstractServiceTest;
import com.rogiel.httpchannel.service.Credential;
import com.rogiel.httpchannel.service.Service;

public class MultiUploadServiceTest extends AbstractServiceTest {
	@Override
	protected Service createService() {
		return new MultiUploadService();
	}

	@Override
	protected URL createDownloadURL() throws MalformedURLException {
		return new URL("http://www.multiupload.com/QPDUXJDZZY");
	}

	@Override
	protected Credential createValidCredential() {
		return null;
	}

	@Override
	protected Credential createInvalidCredential() {
		return new Credential("invalid-"
				+ Double.toString(Math.random() * 1000), Double.toString(Math
				.random() * Integer.MAX_VALUE));
	}
}
