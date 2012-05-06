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
package com.rogiel.httpchannel.service.hotfile;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;

import com.rogiel.httpchannel.service.AbstractAccountDetails;
import com.rogiel.httpchannel.service.AbstractAuthenticator;
import com.rogiel.httpchannel.service.AbstractHttpDownloader;
import com.rogiel.httpchannel.service.AbstractHttpService;
import com.rogiel.httpchannel.service.AbstractUploader;
import com.rogiel.httpchannel.service.AccountDetails;
import com.rogiel.httpchannel.service.AccountDetails.HotLinkingAccountDetails;
import com.rogiel.httpchannel.service.AccountDetails.PremiumAccountDetails;
import com.rogiel.httpchannel.service.AccountDetails.ReferralAccountDetails;
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
import com.rogiel.httpchannel.util.Filesizes;
import com.rogiel.httpchannel.util.html.Page;
import com.rogiel.httpchannel.util.html.SearchResults;

/**
 * This service handles login, upload and download to HotFile.com.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @since 1.0
 */
public class HotFileService extends AbstractHttpService implements Service,
		UploadService<NullUploaderConfiguration>,
		DownloadService<NullDownloaderConfiguration>,
		AuthenticationService<NullAuthenticatorConfiguration> {
	/**
	 * This service ID
	 */
	public static final ServiceID SERVICE_ID = ServiceID.create("hotfile");

	private static final Pattern UPLOAD_URI_PATTERN = Pattern
			.compile("http[s]?://u[0-9]+\\.hotfile\\.com/upload\\.cgi\\?[0-9]*");

	private static final Pattern DOWNLOAD_DIRECT_LINK_PATTERN = Pattern
			.compile("http[s]?://hotfile\\.com/get/([0-9]+)/([A-Za-z0-9]+)/([A-Za-z0-9]+)/(.+)");
	// private static final Pattern DOWNLOAD_TIMER = Pattern
	// .compile("timerend=d\\.getTime\\(\\)\\+([0-9]*);");
	// private static final Pattern DOWNLOAD_FILESIZE = Pattern
	// .compile("[0-9]*(\\.[0-9]*)? (K|M|G)B");

	private static final Pattern DOWNLOAD_URI_PATTERN = Pattern
			.compile("http[s]?://hotfile\\.com/dl/([0-9]+)/([A-Za-z0-9]+)/(.+)");

	// account
	private static final Pattern ACCOUNT_NAME_PATTERN = Pattern
			.compile("User: ([^\\|]+)");

	private static final Pattern ACCOUNT_TYPE_PATTERN = Pattern
			.compile("Account: Free");

	private static final Pattern HOTLINK_TRAFFIC_PATTERN = Pattern.compile(
			"Hotlink traffic left: ([0-9]+(\\.[0-9]+))(K|M|G)b",
			Pattern.CASE_INSENSITIVE);

	private static final Pattern REFERRAL_URL_PATTERN = Pattern
			.compile("http[s]?://hotfile\\.com/register\\.html\\?reff=[0-9]+");

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
				DownloaderCapability.PREMIUM_ACCOUNT_DOWNLOAD);
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
		return new CapabilityMatrix<AuthenticatorCapability>(
				AuthenticatorCapability.ACCOUNT_DETAILS);
	}

	@Override
	public AccountDetails getAccountDetails() {
		return account;
	}

	protected class UploaderImpl extends
			AbstractUploader<NullUploaderConfiguration> implements
			Uploader<NullUploaderConfiguration>,
			LinkedUploadChannelCloseCallback {
		private Future<Page> uploadFuture;

		public UploaderImpl(String filename, long filesize,
				NullUploaderConfiguration configuration) {
			super(HotFileService.this, filename, filesize, configuration);
		}

		@Override
		public UploadChannel openChannel() throws IOException {
			logger.debug("Starting upload to hotfile.com");
			final Page page = get("http://www.hotfile.com/").asPage();
			final String action = page.form(UPLOAD_URI_PATTERN).asString();

			logger.debug("Upload URI is {}", action);

			final LinkedUploadChannel channel = createLinkedChannel(this);

			uploadFuture = multipartPost(action)
					.parameter("uploads[]", channel).asPageAsync();
			return waitChannelLink(channel);
		}

		@Override
		public String finish() throws IOException {
			try {
				return uploadFuture.get().input(DOWNLOAD_URI_PATTERN)
						.asString();
			} catch (InterruptedException e) {
				return null;
			} catch (ExecutionException e) {
				throw (IOException) e.getCause();
			}
		}
	}

	protected class DownloaderImpl extends
			AbstractHttpDownloader<NullDownloaderConfiguration> {
		public DownloaderImpl(URI uri, NullDownloaderConfiguration configuration) {
			super(HotFileService.this, uri, configuration);
		}

		@Override
		public DownloadChannel openChannel(DownloadListener listener,
				long position) throws IOException {
			logger.debug("Downloading {} from hotfile.com", uri);
			final Page page = get(uri).asPage();

			// // try to find timer
			// final String stringTimer = PatternUtils.find(DOWNLOAD_TIMER,
			// content, 2, 1);
			// int timer = 0;
			// if (stringTimer != null && stringTimer.length() > 0) {
			// timer = Integer.parseInt(stringTimer);
			// }
			// if (timer > 0) {
			// throw new DownloadLimitExceededException("Must wait " + timer
			// + " milliseconds");
			// }

			final String downloadUrl = page.link(DOWNLOAD_DIRECT_LINK_PATTERN)
					.asString();
			logger.debug("Download link is {}", downloadUrl);
			// final String tmHash = PatternUtils.find(DOWNLOAD_TMHASH_PATTERN,
			// content);F
			if (downloadUrl != null && downloadUrl.length() > 0) {
				return download(get(downloadUrl));
			} else {
				throw new IOException("Download link not found");
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
		public AccountDetails login() throws ClientProtocolException,
				IOException {
			logger.debug("Authenticating hotfile.com");
			Page page = post("http://www.hotfile.com/login.php")
					.parameter("returnto", "/index.php")
					.parameter("user", credential.getUsername())
					.parameter("pass", credential.getPassword()).asPage();

			page = get("http://www.hotfile.com/myreferals.html?lang=en")
					.asPage();
			
			final SearchResults usernameResults = page
					.search(ACCOUNT_NAME_PATTERN);
			if (!usernameResults.hasResults())
				throw new AuthenticationInvalidCredentialException();

			final String username = usernameResults.asString(1);
			final String type = page.search(ACCOUNT_TYPE_PATTERN).asString();

			final SearchResults trafficResults = page
					.search(HOTLINK_TRAFFIC_PATTERN);
			final long hotlinkTraffic = Filesizes.auto(
					trafficResults.asDouble(1), trafficResults.asString(3));

			final String referralURL = page.search(REFERRAL_URL_PATTERN)
					.asString();

			return (account = new AccountDetailsImpl(username, type == null,
					hotlinkTraffic, referralURL));
		}

		@Override
		public void logout() throws IOException {
			post("http://www.megaupload.com/?c=account").parameter("logout",
					true).request();
			// TODO check logout status
		}
	}

	private class AccountDetailsImpl extends AbstractAccountDetails implements
			PremiumAccountDetails, ReferralAccountDetails,
			HotLinkingAccountDetails {
		private final boolean premium;
		private final long hotlinkTraffic;
		private final String referralURL;

		/**
		 * @param username
		 *            the username
		 * @param premium
		 *            whether the account is premium
		 * @param hotlinkTraffic
		 *            the available hotlink traffic
		 * @param referralURL
		 *            the referral url
		 */
		public AccountDetailsImpl(String username, boolean premium,
				long hotlinkTraffic, String referralURL) {
			super(HotFileService.this, username);
			this.premium = premium;
			this.hotlinkTraffic = hotlinkTraffic;
			this.referralURL = referralURL;
		}

		@Override
		public boolean isPremium() {
			return premium;
		}

		@Override
		public long getHotlinkTraffic() {
			return hotlinkTraffic;
		}

		@Override
		public int getMembersReferred() {
			return -1;
		}

		@Override
		public String getReferralURL() {
			return referralURL;
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + getMajorVersion() + "."
				+ getMinorVersion();
	}
}
