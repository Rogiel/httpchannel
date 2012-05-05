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
package com.rogiel.httpchannel.service.megaupload;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import com.rogiel.httpchannel.service.AbstractAccountDetails;
import com.rogiel.httpchannel.service.AbstractAuthenticator;
import com.rogiel.httpchannel.service.AbstractHttpDownloader;
import com.rogiel.httpchannel.service.AbstractHttpService;
import com.rogiel.httpchannel.service.AbstractUploader;
import com.rogiel.httpchannel.service.AccountDetails;
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
import com.rogiel.httpchannel.service.AccountDetails.PremiumAccountDetails;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel.LinkedUploadChannelCloseCallback;
import com.rogiel.httpchannel.service.config.NullAuthenticatorConfiguration;
import com.rogiel.httpchannel.service.exception.AuthenticationInvalidCredentialException;
import com.rogiel.httpchannel.service.exception.DownloadLimitExceededException;
import com.rogiel.httpchannel.service.exception.DownloadLinkNotFoundException;
import com.rogiel.httpchannel.util.HttpClientUtils;
import com.rogiel.httpchannel.util.PatternUtils;
import com.rogiel.httpchannel.util.htmlparser.HTMLPage;

/**
 * This service handles login, upload and download to MegaUpload.com.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @since 1.0
 */
public class MegaUploadService extends AbstractHttpService implements Service,
		UploadService<MegaUploadUploaderConfiguration>,
		DownloadService<MegaUploadDownloaderConfiguration>,
		AuthenticationService<NullAuthenticatorConfiguration> {
	/**
	 * This service ID
	 */
	public static final ServiceID SERVICE_ID = ServiceID.create("megaupload");

	private static final Pattern UPLOAD_URL_PATTERN = Pattern
			.compile("http://www([0-9]*)\\.megaupload\\.com/upload_done\\.php\\?UPLOAD_IDENTIFIER=[0-9]*");

	private static final Pattern DOWNLOAD_DIRECT_LINK_PATTERN = Pattern
			.compile("http://www([0-9]*)\\.megaupload\\.com/files/([A-Za-z0-9]*)/(.*)");
	private static final Pattern DOWNLOAD_TIMER = Pattern
			.compile("count=([0-9]*);");
	// private static final Pattern DOWNLOAD_FILESIZE = Pattern
	// .compile("[0-9]*(\\.[0-9]*)? (K|M|G)B");

	private static final Pattern DOWNLOAD_URI_PATTERN = Pattern
			.compile("http://www\\.megaupload\\.com/\\?d=([A-Za-z0-9]*)");

	private static final Pattern LOGIN_USERNAME_PATTERN = Pattern
			.compile("flashvars\\.username = \"(.*)\";");

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
	public Uploader<MegaUploadUploaderConfiguration> getUploader(
			String filename, long filesize,
			MegaUploadUploaderConfiguration configuration) {
		return new UploaderImpl(filename, filesize, configuration);
	}

	@Override
	public Uploader<MegaUploadUploaderConfiguration> getUploader(
			String filename, long filesize) {
		return getUploader(filename, filesize, newUploaderConfiguration());
	}

	@Override
	public MegaUploadUploaderConfiguration newUploaderConfiguration() {
		return new MegaUploadUploaderConfiguration();
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
	public Downloader<MegaUploadDownloaderConfiguration> getDownloader(URI uri,
			MegaUploadDownloaderConfiguration configuration) {
		return new DownloaderImpl(uri, configuration);
	}

	@Override
	public Downloader<MegaUploadDownloaderConfiguration> getDownloader(URI uri) {
		return getDownloader(uri, newDownloaderConfiguration());
	}

	@Override
	public MegaUploadDownloaderConfiguration newDownloaderConfiguration() {
		return new MegaUploadDownloaderConfiguration();
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
				DownloaderCapability.NON_PREMIUM_ACCOUNT_RESUME,
				DownloaderCapability.PREMIUM_ACCOUNT_DOWNLOAD,
				DownloaderCapability.PREMIUM_ACCOUNT_RESUME);
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
			AbstractUploader<MegaUploadUploaderConfiguration> implements
			Uploader<MegaUploadUploaderConfiguration>,
			LinkedUploadChannelCloseCallback {
		private Future<String> uploadFuture;

		public UploaderImpl(String filename, long filesize,
				MegaUploadUploaderConfiguration configuration) {
			super(MegaUploadService.this, filename, filesize, configuration);
		}

		@Override
		public UploadChannel openChannel() throws IOException {
			logger.debug("Starting upload to megaupload.com");
			final HTMLPage page = get("http://www.megaupload.com/multiupload/")
					.asPage();
			final String uri = page.findFormAction(UPLOAD_URL_PATTERN);
			logger.debug("Upload URI is {}", uri);

			final LinkedUploadChannel channel = createLinkedChannel(this);
			uploadFuture = multipartPost(uri)
					.parameter("multimessage_0", configuration.description())
					.parameter("multifile_0", channel).asStringAsync();
			return waitChannelLink(channel);
		}

		@Override
		public String finish() throws IOException {
			try {
				return PatternUtils.find(DOWNLOAD_URI_PATTERN,
						uploadFuture.get());
			} catch (InterruptedException e) {
				return null;
			} catch (ExecutionException e) {
				throw (IOException) e.getCause();
			}
		}
	}

	protected class DownloaderImpl extends
			AbstractHttpDownloader<MegaUploadDownloaderConfiguration> implements
			Downloader<MegaUploadDownloaderConfiguration> {
		public DownloaderImpl(URI uri,
				MegaUploadDownloaderConfiguration configuration) {
			super(MegaUploadService.this, uri, configuration);
		}

		@Override
		public DownloadChannel openChannel(DownloadListener listener,
				long position) throws IOException {
			logger.debug("Starting {} download from megaupload.com", uri);
			HttpResponse response = get(uri).request();

			// disable direct downloads, we don't support them!
			if (response.getEntity().getContentType().getValue()
					.equals("application/octet-stream")) {
				logger.debug("Direct download is enabled, deactivating");
				// close connection
				response.getEntity().getContent().close();

				// execute update
				post("http://www.megaupload.com/?c=account")
						.parameter("do", "directdownloads")
						.parameter("accountupdate", "1")
						.parameter("set_ddl", "0").request();

				// execute and re-request download
				response = get(uri).request();
			}

			final HTMLPage page = HttpClientUtils.toPage(response);

			// try to find timer
			int timer = page.findScriptAsInt(DOWNLOAD_TIMER, 1);
			if (timer > 0 && configuration.getRespectWaitTime()) {
				logger.debug("");
				timer(listener, timer * 1000);
			}
			final String downloadUrl = page
					.findLink(DOWNLOAD_DIRECT_LINK_PATTERN);
			if (downloadUrl != null && downloadUrl.length() > 0) {
				final HttpResponse downloadResponse = get(downloadUrl)
						.position(position).request();
				if (downloadResponse.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN
						|| downloadResponse.getStatusLine().getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE) {
					downloadResponse.getEntity().getContent().close();
					throw new DownloadLimitExceededException("HTTP "
							+ downloadResponse.getStatusLine().getStatusCode()
							+ " response");
				} else {
					final String filename = FilenameUtils.getName(downloadUrl);
					final long contentLength = getContentLength(downloadResponse);

					return createInputStreamChannel(downloadResponse
							.getEntity().getContent(), contentLength, filename);
				}
			} else {
				throw new DownloadLinkNotFoundException();
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
		public AccountDetails login() throws IOException {
			logger.debug("Starting login to megaupload.com");
			final HTMLPage page = post("http://www.megaupload.com/?c=login")
					.parameter("login", true)
					.parameter("username", credential.getUsername())
					.parameter("", credential.getPassword()).asPage();

			String username = page.findScript(LOGIN_USERNAME_PATTERN, 1);
			if (username == null)
				throw new AuthenticationInvalidCredentialException();
			return (account = new AccountDetailsImpl(credential.getUsername()));
		}

		@Override
		public void logout() throws IOException {
			post("http://www.megaupload.com/?c=account").parameter("logout",
					true).request();
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
			super(MegaUploadService.this, username);
		}

		@Override
		public boolean isPremium() {
			// TODO implement this
			return false;
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + getMajorVersion() + "."
				+ getMinorVersion();
	}
}
