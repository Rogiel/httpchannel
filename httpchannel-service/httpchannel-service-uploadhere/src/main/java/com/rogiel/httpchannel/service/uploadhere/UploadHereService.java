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
package com.rogiel.httpchannel.service.uploadhere;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import com.rogiel.httpchannel.captcha.ImageCaptcha;
import com.rogiel.httpchannel.captcha.ReCaptchaExtractor;
import com.rogiel.httpchannel.service.AbstractAuthenticator;
import com.rogiel.httpchannel.service.AbstractHttpDownloader;
import com.rogiel.httpchannel.service.AbstractHttpService;
import com.rogiel.httpchannel.service.AbstractUploader;
import com.rogiel.httpchannel.service.AuthenticationService;
import com.rogiel.httpchannel.service.Authenticator;
import com.rogiel.httpchannel.service.AuthenticatorCapability;
import com.rogiel.httpchannel.service.CapabilityMatrix;
import com.rogiel.httpchannel.service.Credential;
import com.rogiel.httpchannel.service.DownloadChannel;
import com.rogiel.httpchannel.service.DownloadListener;
import com.rogiel.httpchannel.service.DownloadService;
import com.rogiel.httpchannel.service.Downloader;
import com.rogiel.httpchannel.service.DownloaderCapability;
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
import com.rogiel.httpchannel.service.config.NullDownloaderConfiguration;
import com.rogiel.httpchannel.service.config.NullUploaderConfiguration;
import com.rogiel.httpchannel.service.exception.AuthenticationInvalidCredentialException;
import com.rogiel.httpchannel.service.exception.DownloadLinkNotFoundException;
import com.rogiel.httpchannel.service.exception.InvalidCaptchaException;
import com.rogiel.httpchannel.util.PatternUtils;
import com.rogiel.httpchannel.util.htmlparser.HTMLPage;

/**
 * This service handles uploads to UploadKing.com.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel</a>
 * @since 1.0
 */
public class UploadHereService extends AbstractHttpService implements Service,
		UploadService<NullUploaderConfiguration>,
		DownloadService<NullDownloaderConfiguration>,
		AuthenticationService<NullAuthenticatorConfiguration> {
	/**
	 * This service ID
	 */
	public static final ServiceID SERVICE_ID = ServiceID.create("uploadhere");

	private static final Pattern UPLOAD_URI_PATTERN = Pattern
			.compile("http://www([0-9]*)\\.uploadhere\\.com/upload/\\?UPLOAD_IDENTIFIER=[0-9]*");
	private static final Pattern DOWNLOAD_ID_PATTERN = Pattern
			.compile("\"downloadid\":\"([0-9a-zA-Z]*)\"");
	private static final Pattern DOWNLOAD_URI_PATTERN = Pattern
			.compile("http://(www\\.)?uploadhere.\\com/[0-9A-z]*");
	private static final Pattern TIMER_PATTERN = Pattern.compile(
			"count = ([0-9]*);", Pattern.COMMENTS);
	private static final Pattern DIERCT_DOWNLOAD_URI_PATTERN = Pattern
			.compile("(http:\\\\/\\\\/www[0-9]*\\.uploadhere\\.com(:[0-9]*)?\\\\/files\\\\/([0-9A-z]*)\\\\/(.*))\"");

	private static final String INVALID_LOGIN_STRING = "Incorrect username and/or password. Please try again!";

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
		return 1 * 1024 * 1024 * 1024;
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
	public Downloader<NullDownloaderConfiguration> getDownloader(URI uri,
			NullDownloaderConfiguration configuration) {
		return new DownloaderImpl(uri, configuration);
	}

	@Override
	public Downloader<NullDownloaderConfiguration> getDownloader(URI uri) {
		return getDownloader(uri, newDownloaderConfiguration());
	}

	@Override
	public NullDownloaderConfiguration newDownloaderConfiguration() {
		return NullDownloaderConfiguration.SHARED_INSTANCE;
	}

	@Override
	public boolean matchURI(URI uri) {
		return DOWNLOAD_URI_PATTERN.matcher(uri.toString()).matches();
	}

	@Override
	public CapabilityMatrix<DownloaderCapability> getDownloadCapabilities() {
		return new CapabilityMatrix<DownloaderCapability>(
				DownloaderCapability.UNAUTHENTICATED_DOWNLOAD,
				DownloaderCapability.UNAUTHENTICATED_RESUME,
				DownloaderCapability.NON_PREMIUM_ACCOUNT_DOWNLOAD,
				DownloaderCapability.NON_PREMIUM_ACCOUNT_RESUME);
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
		private Future<String> uploadFuture;

		public UploaderImpl(String filename, long filesize,
				NullUploaderConfiguration configuration) {
			super(UploadHereService.this, filename, filesize, configuration);
		}

		@Override
		public UploadChannel openChannel() throws IOException {
			final HTMLPage page = get("http://www.uploadhere.com/").asPage();

			final String userCookie = page.getInputValueById("usercookie");
			final String uri = page.findFormAction(UPLOAD_URI_PATTERN);
			final String uploadID = page.getInputValue("UPLOAD_IDENTIFIER");

			logger.debug("Upload URI: {}, UserCookie: {}, UploadID: {}",
					new Object[] { uri, userCookie, uploadID });

			final LinkedUploadChannel channel = createLinkedChannel(this);
			uploadFuture = multipartPost(uri).parameter("file_0", channel)
					.parameter("u", userCookie)
					.parameter("UPLOAD_IDENTIFIER", uploadID).asStringAsync();
			return waitChannelLink(channel, uploadFuture);
		}

		@Override
		public String finish() throws IOException {
			try {
				final String linkId = PatternUtils.find(DOWNLOAD_ID_PATTERN,
						uploadFuture.get(), 1);
				logger.debug("Upload to uploadhere.com finished");
				if (linkId == null)
					return null;
				return new StringBuilder("http://www.uploadhere.com/").append(
						linkId).toString();
			} catch (InterruptedException e) {
				return null;
			} catch (ExecutionException e) {
				if (e.getCause() instanceof IOException)
					throw (IOException) e.getCause();
				else
					throw new IOException(e.getCause());
			}
		}
	}

	protected class DownloaderImpl extends
			AbstractHttpDownloader<NullDownloaderConfiguration> implements
			Downloader<NullDownloaderConfiguration> {
		public DownloaderImpl(URI uri, NullDownloaderConfiguration configuration) {
			super(UploadHereService.this, uri, configuration);
		}

		@Override
		public DownloadChannel openChannel(DownloadListener listener,
				long position) throws IOException {
			HTMLPage page = get(uri).asPage();

			final int waitTime = page.findScriptAsInt(TIMER_PATTERN, 1) * 1000;
			logger.debug("Wait time is {}", waitTime);

			timer(listener, waitTime);
			final ImageCaptcha captcha = ReCaptchaExtractor.extractAjaxCaptcha(
					page, http);

			String content;
			if (captcha != null) {
				logger.debug("Service is requiring CAPTCHA {}", captcha);
				resolveCaptcha(captcha);

				content = post(uri)
						.parameter("recaptcha_challenge_field", captcha.getID())
						.parameter("recaptcha_response_field",
								captcha.getAnswer()).asString();
			} else {
				content = page.toString();
			}

			String downloadLink = PatternUtils.find(
					DIERCT_DOWNLOAD_URI_PATTERN, content, 1);
			if (downloadLink == null) {
				captchaService.invalid(captcha);
				throw new DownloadLinkNotFoundException(
						new InvalidCaptchaException());
			} else {
				captchaService.valid(captcha);
			}
			downloadLink = downloadLink.replaceAll(Pattern.quote("\\/"), "/");
			logger.debug("Direct download URI is {}", downloadLink);
			return download(get(downloadLink).position(position));
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
			final HTMLPage page = post("http://www.uploadhere.com/login")
					.parameter("do", "login")
					.parameter("username", credential.getUsername())
					.parameter("password", credential.getPassword()).asPage();
			if (page.contains(INVALID_LOGIN_STRING))
				throw new AuthenticationInvalidCredentialException();
			serviceMode = ServiceMode.NON_PREMIUM;
		}

		@Override
		public void logout() throws IOException {
			post("http://www.uploadhere.com/login").parameter("do", "logout")
					.request();
			// TODO check logout status
		}
	}
}
