/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.rogiel.httpchannel.service.depositfiles;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import com.rogiel.httpchannel.captcha.ImageCaptcha;
import com.rogiel.httpchannel.captcha.ReCaptchaExtractor;
import com.rogiel.httpchannel.captcha.exception.UnsolvableCaptchaServiceException;
import com.rogiel.httpchannel.service.AbstractAuthenticator;
import com.rogiel.httpchannel.service.AbstractHttpService;
import com.rogiel.httpchannel.service.AbstractUploader;
import com.rogiel.httpchannel.service.AuthenticationService;
import com.rogiel.httpchannel.service.Authenticator;
import com.rogiel.httpchannel.service.AuthenticatorCapability;
import com.rogiel.httpchannel.service.CapabilityMatrix;
import com.rogiel.httpchannel.service.Credential;
import com.rogiel.httpchannel.service.Service;
import com.rogiel.httpchannel.service.ServiceID;
import com.rogiel.httpchannel.service.ServiceMode;
import com.rogiel.httpchannel.service.UploadChannel;
import com.rogiel.httpchannel.service.UploadService;
import com.rogiel.httpchannel.service.Uploader;
import com.rogiel.httpchannel.service.UploaderCapability;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel.LinkedUploadChannelCloseCallback;
import com.rogiel.httpchannel.service.config.NullAuthenticatorConfiguration;
import com.rogiel.httpchannel.service.config.NullUploaderConfiguration;
import com.rogiel.httpchannel.service.exception.AuthenticationInvalidCredentialException;
import com.rogiel.httpchannel.util.htmlparser.HTMLPage;

/**
 * This service handles uploads to UploadKing.com.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel</a>
 * @since 1.0
 */
public class DepositFilesService extends AbstractHttpService implements
		Service, UploadService<NullUploaderConfiguration>,
		AuthenticationService<NullAuthenticatorConfiguration> {
	/**
	 * This service ID
	 */
	public static final ServiceID SERVICE_ID = ServiceID.create("depositfiles");

	private static final Pattern UPLOAD_URI_PATTERN = Pattern
			.compile("http://fileshare([0-9]*)\\.depositfiles\\.com/(.*)/\\?X-Progress-ID=(.*)");
	private static final Pattern DOWNLOAD_URI_PATTERN = Pattern
			.compile("http://(www\\.)?depositfiles\\.com/files/([0-9A-z]*)");

	private static final Pattern VALID_LOGIN_REDIRECT = Pattern
			.compile("window.location.href");

	@Override
	public ServiceID getServiceID() {
		return SERVICE_ID;
	}

	@Override
	public int getMajorVersion() {
		return 1;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public CapabilityMatrix<ServiceMode> getPossibleServiceModes() {
		return new CapabilityMatrix<ServiceMode>(ServiceMode.UNAUTHENTICATED,
				ServiceMode.NON_PREMIUM, ServiceMode.PREMIUM);
	}

	@Override
	public Uploader<NullUploaderConfiguration> getUploader(String filename,
			long filesize, NullUploaderConfiguration configuration) {
		return new UploaderImpl(filename, filesize, configuration);
	}

	@Override
	public Uploader<NullUploaderConfiguration> getUploader(String filename,
			long filesize) {
		return getUploader(filename, filesize, newUploaderConfiguration());
	}

	@Override
	public NullUploaderConfiguration newUploaderConfiguration() {
		return NullUploaderConfiguration.SHARED_INSTANCE;
	}

	@Override
	public long getMaximumFilesize() {
		return 2 * 1024 * 1024 * 1024;
	}

	@Override
	public String[] getSupportedExtensions() {
		return null;
	}

	@Override
	public CapabilityMatrix<UploaderCapability> getUploadCapabilities() {
		return new CapabilityMatrix<UploaderCapability>(
				UploaderCapability.UNAUTHENTICATED_UPLOAD,
				UploaderCapability.NON_PREMIUM_ACCOUNT_UPLOAD,
				UploaderCapability.PREMIUM_ACCOUNT_UPLOAD);
	}

	@Override
	public Authenticator<NullAuthenticatorConfiguration> getAuthenticator(
			Credential credential, NullAuthenticatorConfiguration configuration) {
		return new AuthenticatorImpl(credential, configuration);
	}

	@Override
	public Authenticator<NullAuthenticatorConfiguration> getAuthenticator(
			Credential credential) {
		return getAuthenticator(credential, newAuthenticatorConfiguration());
	}

	@Override
	public NullAuthenticatorConfiguration newAuthenticatorConfiguration() {
		return NullAuthenticatorConfiguration.SHARED_INSTANCE;
	}

	@Override
	public CapabilityMatrix<AuthenticatorCapability> getAuthenticationCapability() {
		return new CapabilityMatrix<AuthenticatorCapability>();
	}

	protected class UploaderImpl extends
			AbstractUploader<NullUploaderConfiguration> implements
			Uploader<NullUploaderConfiguration>,
			LinkedUploadChannelCloseCallback {
		private Future<HTMLPage> uploadFuture;

		public UploaderImpl(String filename, long filesize,
				NullUploaderConfiguration configuration) {
			super(DepositFilesService.this, filename, filesize, configuration);
		}

		@Override
		public UploadChannel openChannel() throws IOException {
			logger.debug("Starting upload to depositfiles.com");
			final HTMLPage page = get("http://www.depositfiles.com/").asPage();

			final String uri = page.findFormAction(UPLOAD_URI_PATTERN);
			final String uploadID = page.getInputValue("UPLOAD_IDENTIFIER");
			final String maxFileSize = page.getInputValue("MAX_FILE_SIZE");

			logger.debug("Upload URI: {}, ID: {}", uri, uploadID);

			final LinkedUploadChannel channel = createLinkedChannel(this);
			uploadFuture = multipartPost(uri).parameter("files", channel)
					.parameter("go", true)
					.parameter("UPLOAD_IDENTIFIER", uploadID)
					.parameter("agree", true)
					.parameter("MAX_FILE_SIZE", maxFileSize).asPageAsync();
			return waitChannelLink(channel, uploadFuture);
		}

		@Override
		public String finish() throws IOException {
			try {
				final String link = uploadFuture.get().findScript(
						DOWNLOAD_URI_PATTERN, 0);
				if (link == null)
					return null;
				return link;
			} catch (InterruptedException e) {
				return null;
			} catch (ExecutionException e) {
				throw (IOException) e.getCause();
			}
		}
	}

	protected class AuthenticatorImpl extends
			AbstractAuthenticator<NullAuthenticatorConfiguration> implements
			Authenticator<NullAuthenticatorConfiguration> {
		public AuthenticatorImpl(Credential credential,
				NullAuthenticatorConfiguration configuration) {
			super(credential, configuration);
		}

		@Override
		public void login() throws IOException {
			logger.debug("Authenticating into depositfiles.com");
			HTMLPage page = post("http://depositfiles.com/login.php?return=%2F")
					.parameter("go", true)
					.parameter("login", credential.getUsername())
					.parameter("password", credential.getPassword()).asPage();

			final ImageCaptcha captcha = ReCaptchaExtractor.extractCaptcha(
					page, http);
			if (captcha != null) {
				logger.debug("Service is requiring CAPTCHA {}", captcha);
				resolveCaptcha(captcha);
				page = post("http://depositfiles.com/login.php?return=%2F")
						.parameter("go", true)
						.parameter("login", credential.getUsername())
						.parameter("password", credential.getPassword())
						.parameter("recaptcha_challenge_field", captcha.getID())
						.parameter("recaptcha_response_field",
								captcha.getAnswer()).asPage();
			}

			final ImageCaptcha testCaptcha = ReCaptchaExtractor.extractCaptcha(
					page, http);
			if (testCaptcha != null) {
				captchaService.invalid(captcha);
				throw new UnsolvableCaptchaServiceException();
			} else {
				captchaService.valid(captcha);
				if (!page.contains(VALID_LOGIN_REDIRECT))
					throw new AuthenticationInvalidCredentialException();
				serviceMode = ServiceMode.NON_PREMIUM;
				return;
			}
		}

		@Override
		public void logout() throws IOException {
			post("http://www.uploadking.com/login").parameter("do", "logout")
					.request();
			// TODO check logout status
		}
	}
}
