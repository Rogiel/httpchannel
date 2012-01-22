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
package com.rogiel.httpchannel.service.multiupload;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import com.rogiel.httpchannel.http.PostMultipartRequest;
import com.rogiel.httpchannel.service.AbstractAccountDetails;
import com.rogiel.httpchannel.service.AbstractAuthenticator;
import com.rogiel.httpchannel.service.AbstractHttpDownloader;
import com.rogiel.httpchannel.service.AbstractHttpService;
import com.rogiel.httpchannel.service.AbstractUploader;
import com.rogiel.httpchannel.service.AccountDetails;
import com.rogiel.httpchannel.service.AccountDetails.PremiumAccountDetails;
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
import com.rogiel.httpchannel.service.exception.DownloadLimitExceededException;
import com.rogiel.httpchannel.service.exception.DownloadLinkNotFoundException;
import com.rogiel.httpchannel.service.exception.DownloadNotAuthorizedException;
import com.rogiel.httpchannel.service.exception.DownloadNotResumableException;
import com.rogiel.httpchannel.service.exception.AuthenticationInvalidCredentialException;
import com.rogiel.httpchannel.service.multiupload.MultiUploadUploaderConfiguration.MultiUploadMirrorService;
import com.rogiel.httpchannel.util.PatternUtils;
import com.rogiel.httpchannel.util.htmlparser.HTMLPage;


/**
 * This service handles uploads to MultiUpload.com.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel</a>
 * @since 1.0
 */
public class MultiUploadService extends AbstractHttpService implements Service,
		UploadService<MultiUploadUploaderConfiguration>,
		DownloadService<NullDownloaderConfiguration>,
		AuthenticationService<NullAuthenticatorConfiguration> {
	/**
	 * This service ID
	 */
	public static final ServiceID SERVICE_ID = ServiceID.create("multiupload");

	// http://www52.multiupload.com/upload/?UPLOAD_IDENTIFIER=73132658610746
	private static final Pattern UPLOAD_URI_PATTERN = Pattern
			.compile("http://www([0-9]*)\\.multiupload\\.com/upload/\\?UPLOAD_IDENTIFIER=[0-9]*");
	private static final Pattern DOWNLOAD_ID_PATTERN = Pattern
			.compile("\"downloadid\":\"([0-9a-zA-Z]*)\"");
	private static final Pattern DOWNLOAD_LINK_PATTERN = Pattern
			.compile("http://(www\\.)?multiupload\\.com/([0-9a-zA-Z]*)");
	private static final Pattern DIRECT_DOWNLOAD_LINK_PATTERN = Pattern
			.compile("http://www[0-9]*\\.multiupload\\.com(:[0-9]*)?/files/([0-9a-zA-Z]*)/(.*)");

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
	public Uploader<MultiUploadUploaderConfiguration> getUploader(
			String filename, long filesize,
			MultiUploadUploaderConfiguration configuration) {
		if (configuration == null)
			configuration = new MultiUploadUploaderConfiguration();
		return new UploaderImpl(filename, filesize, configuration);
	}

	@Override
	public Uploader<MultiUploadUploaderConfiguration> getUploader(
			String filename, long filesize) {
		return getUploader(filename, filesize, newUploaderConfiguration());
	}

	@Override
	public MultiUploadUploaderConfiguration newUploaderConfiguration() {
		return new MultiUploadUploaderConfiguration();
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
		return DOWNLOAD_LINK_PATTERN.matcher(uri.toString()).matches();
	}

	@Override
	public CapabilityMatrix<DownloaderCapability> getDownloadCapabilities() {
		return new CapabilityMatrix<>(
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
		return new CapabilityMatrix<AuthenticatorCapability>(AuthenticatorCapability.ACCOUNT_DETAILS);
	}
	
	@Override
	public AccountDetails getAccountDetails() {
		return account;
	}

	protected class UploaderImpl extends
			AbstractUploader<MultiUploadUploaderConfiguration> implements
			Uploader<MultiUploadUploaderConfiguration>,
			LinkedUploadChannelCloseCallback {
		private Future<String> uploadFuture;

		public UploaderImpl(String filename, long filesize,
				MultiUploadUploaderConfiguration configuration) {
			super(MultiUploadService.this, filename, filesize, configuration);
		}

		@Override
		public UploadChannel openChannel() throws IOException {
			logger.debug("Starting upload to multiupload.com");
			final String uri = get("http://www.multiupload.com/").asPage()
					.findFormAction(UPLOAD_URI_PATTERN);
			logger.debug("Upload URI is {}", uri);
			final LinkedUploadChannel channel = createLinkedChannel(this);

			PostMultipartRequest request = multipartPost(uri).parameter(
					"description_0", configuration.description()).parameter(
					"file_0", channel);
			for (final MultiUploadMirrorService mirror : configuration
					.uploadServices()) {
				logger.debug("Adding {} as mirror", mirror.name());
				request.parameter("service_" + mirror.id, 1);
				final String[] login = configuration
						.getAuthenticationForService(mirror);
				if (login != null && login.length == 2) {
					request.parameter("username_" + mirror.id, login[0]);
					request.parameter("password_" + mirror.id, login[1]);
				}
			}

			uploadFuture = request.asStringAsync();
			return waitChannelLink(channel);
		}

		@Override
		public String finish() throws IOException {
			try {
				final String linkId = PatternUtils.find(DOWNLOAD_ID_PATTERN,
						uploadFuture.get(), 1);
				logger.debug("Upload to multiupload.com finished");
				if (linkId == null)
					return null;
				return new StringBuilder("http://www.multiupload.com/").append(
						linkId).toString();
			} catch (InterruptedException e) {
				return null;
			} catch (ExecutionException e) {
				throw (IOException) e.getCause();
			}
		}
	}

	protected class DownloaderImpl extends
			AbstractHttpDownloader<NullDownloaderConfiguration> implements
			Downloader<NullDownloaderConfiguration> {
		protected DownloaderImpl(URI uri,
				NullDownloaderConfiguration configuration) {
			super(MultiUploadService.this, uri, configuration);
		}

		@Override
		public DownloadChannel openChannel(DownloadListener listener,
				long position) throws IOException,
				DownloadLinkNotFoundException, DownloadLimitExceededException,
				DownloadNotAuthorizedException, DownloadNotResumableException {
			final HTMLPage page = get(uri).asPage();
			final String link = page.findLink(DIRECT_DOWNLOAD_LINK_PATTERN);
			logger.debug("Direct download link is {}", link);
			if (link == null)
				throw new DownloadLinkNotFoundException();
			return download(get(link).position(position));
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
		public AccountDetails login() throws IOException {
			final HTMLPage page = post("http://www.multiupload.com/login")
					.parameter("username", credential.getUsername())
					.parameter("password", credential.getPassword()).asPage();

			if (!page.containsIgnoreCase(credential.getUsername()))
				throw new AuthenticationInvalidCredentialException();
			return (account = new AccountDetailsImpl(credential.getUsername()));
		}

		@Override
		public void logout() throws IOException {
			post("http://www.multiupload.com/login").parameter("do", "logout")
					.request();
			// TODO check logout status
		}
	}

	private class AccountDetailsImpl extends AbstractAccountDetails implements
			PremiumAccountDetails {
		/**
		 * @param username
		 *            the username
		 */
		public AccountDetailsImpl(String username) {
			super(MultiUploadService.this, username);
		}

		@Override
		public boolean isPremium() {
			// TODO implement this
			return false;
		}
	}
}
