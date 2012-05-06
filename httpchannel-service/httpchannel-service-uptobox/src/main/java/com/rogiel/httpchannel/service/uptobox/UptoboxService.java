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
package com.rogiel.httpchannel.service.uptobox;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import com.rogiel.httpchannel.service.AbstractAccountDetails;
import com.rogiel.httpchannel.service.AbstractAuthenticator;
import com.rogiel.httpchannel.service.AbstractHttpService;
import com.rogiel.httpchannel.service.AbstractUploader;
import com.rogiel.httpchannel.service.AccountDetails;
import com.rogiel.httpchannel.service.AccountDetails.DiskQuotaAccountDetails;
import com.rogiel.httpchannel.service.AccountDetails.FilesizeLimitAccountDetails;
import com.rogiel.httpchannel.service.AccountDetails.PointAccountDetails;
import com.rogiel.httpchannel.service.AccountDetails.PremiumAccountDetails;
import com.rogiel.httpchannel.service.AccountDetails.ReferralAccountDetails;
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
import com.rogiel.httpchannel.service.exception.AuthenticationInvalidCredentialException;
import com.rogiel.httpchannel.util.Filesizes;
import com.rogiel.httpchannel.util.html.Page;
import com.rogiel.httpchannel.util.html.SearchResults;

/**
 * This service handles login, upload and download to uptobox.com.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @since 1.0
 */
public class UptoboxService extends AbstractHttpService implements Service,
		UploadService<UptoboxUploaderConfiguration>,
		AuthenticationService<NullAuthenticatorConfiguration> {
	/**
	 * This service ID
	 */
	public static final ServiceID SERVICE_ID = ServiceID.create("uptobox");

	private static final Pattern UPLOAD_URI_PATTERN = Pattern
			.compile("http://www[0-9]+\\.uptobox\\.com/cgi-bin/upload.cgi\\?upload_id=");

	private static final Pattern DOWNLOAD_URI_PATTERN = Pattern
			.compile("http://(www\\.)?uptobox\\.com/[a-z0-9]+");

	private static final Pattern DISK_USAGE_PATTERN = Pattern
			.compile("Used space ([0-9]+(\\.[0-9]+)?) (Kb|Mb) of ([0-9]+) Mb");

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
	public Uploader<UptoboxUploaderConfiguration> getUploader(String filename,
			long filesize, UptoboxUploaderConfiguration configuration) {
		return new UploaderImpl(filename, filesize, configuration);
	}

	@Override
	public Uploader<UptoboxUploaderConfiguration> getUploader(String filename,
			long filesize) {
		return getUploader(filename, filesize, newUploaderConfiguration());
	}

	@Override
	public UptoboxUploaderConfiguration newUploaderConfiguration() {
		return new UptoboxUploaderConfiguration();
	}

	@Override
	public long getMaximumFilesize() {
		if (account == null) {
			return 1 * 1024 * 1024 * 1024;
		} else {
			return account.as(FilesizeLimitAccountDetails.class)
					.getMaximumFilesize();
		}
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
		return new CapabilityMatrix<AuthenticatorCapability>(
				AuthenticatorCapability.ACCOUNT_DETAILS);
	}

	@Override
	public AccountDetails getAccountDetails() {
		return account;
	}

	protected class UploaderImpl extends
			AbstractUploader<UptoboxUploaderConfiguration> implements
			Uploader<UptoboxUploaderConfiguration>,
			LinkedUploadChannelCloseCallback {
		private Future<Page> uploadFuture;

		public UploaderImpl(String filename, long filesize,
				UptoboxUploaderConfiguration configuration) {
			super(UptoboxService.this, filename, filesize, configuration);
		}

		@Override
		public UploadChannel openChannel() throws IOException {
			logger.debug("Starting upload to uptobox.com");
			final Page page = get("http://uptobox.com/").asPage();
			String action = page.form(UPLOAD_URI_PATTERN).asString();
			final String srvTmpUrl = page.inputByName("srv_tmp_url").asString();

			if (account != null) {
				action += "&type=reg";
			}

			final String sessionID = page.inputByName("sess_id").asString();

			logger.debug("Upload URI is {}", action);

			final LinkedUploadChannel channel = createLinkedChannel(this);

			uploadFuture = multipartPost(action).parameter("file_0", channel)
					.parameter("file_0_descr", configuration.description())
					.parameter("sess_id", sessionID)
					.parameter("srv_tmp_url", srvTmpUrl).parameter("tos", true)
					.asPageAsync();
			return waitChannelLink(channel);
		}

		@Override
		public String finish() throws IOException {
			try {
				return uploadFuture.get().link(DOWNLOAD_URI_PATTERN).asString();
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
		public AccountDetails login() throws IOException {
			final Page page = post("http://uptobox.com/")
					.parameter("op", "login")
					.parameter("redirect", "http://uptobox.com/?op=my_account")
					.parameter("login", credential.getUsername())
					.parameter("password", credential.getPassword()).asPage();

			final SearchResults results = page.search(Pattern
					.compile("Username:(.+) Apply"));
			if (!results.hasResults())
				throw new AuthenticationInvalidCredentialException();
			final String username = results.asString(1);
			if (username == null)
				throw new AuthenticationInvalidCredentialException();
			final boolean premium = !page.search(
					Pattern.compile("Account type Free member",
							Pattern.MULTILINE)).hasResults();
			final int points = page.search(
					Pattern.compile("You have collected:([0-9])+"))
					.asInteger(1);
			final int referrals = page.search(
					Pattern.compile("My referrals:([0-9])+")).asInteger(1);
			final String referralURL = page.link(
					Pattern.compile("http://uptobox\\.com/affiliate/[0-9]+"))
					.asString();

			final Page index = get("http://uptobox.com/").asPage();
			final int maximumFileSize = index.search(
					Pattern.compile("Up to ([0-9]*) Mb")).asInteger(1);

			final Page disk = get("http://uptobox.com/?op=my_files").asPage();
			final double usedDiskSpace = disk.search(DISK_USAGE_PATTERN)
					.asDouble(1);
			final String usedDiskSpaceUnit = disk.search(DISK_USAGE_PATTERN)
					.asString(3);
			final double maximumDiskSpace = disk.search(DISK_USAGE_PATTERN)
					.asDouble(4);

			return (account = new AccountDetailsImpl(username, premium,
					Filesizes.mb(maximumFileSize),
					Filesizes.mb(maximumDiskSpace), Filesizes.auto(
							usedDiskSpace, usedDiskSpaceUnit), points,
					referrals, referralURL));
		}

		@Override
		public void logout() throws IOException {
			get("http://uptobox.com/?op=logout").request();
			account = null;
		}
	}

	private class AccountDetailsImpl extends AbstractAccountDetails implements
			PremiumAccountDetails, ReferralAccountDetails, PointAccountDetails,
			FilesizeLimitAccountDetails, DiskQuotaAccountDetails {
		private final boolean premium;
		private final long maximumFileSize;
		private final long maximumDiskSpace;
		private final long usedDiskSpace;
		private final int points;
		private final int referrals;
		private final String referralURL;

		/**
		 * @param username
		 *            the username
		 * @param premium
		 *            if the account is premium
		 * @param maximumFileSize
		 *            the maximum file size
		 * @param maximumDiskSpace
		 *            the maximum file size
		 * @param usedDiskSpace
		 *            the maximum file size
		 * @param points
		 *            the amount of points on the account
		 * @param referrals
		 *            the number of referrals on the account
		 * @param referralURL
		 *            the referral URL
		 */
		public AccountDetailsImpl(String username, boolean premium,
				long maximumFileSize, long maximumDiskSpace,
				long usedDiskSpace, int points, int referrals,
				String referralURL) {
			super(UptoboxService.this, username);
			this.premium = premium;
			this.maximumFileSize = maximumFileSize;
			this.maximumDiskSpace = maximumDiskSpace;
			this.usedDiskSpace = usedDiskSpace;
			this.points = points;
			this.referrals = referrals;
			this.referralURL = referralURL;
		}

		@Override
		public boolean isPremium() {
			return premium;
		}

		@Override
		public int getPoints() {
			return points;
		}

		@Override
		public int getMembersReferred() {
			return referrals;
		}

		@Override
		public String getReferralURL() {
			return referralURL;
		}

		@Override
		public long getMaximumFilesize() {
			return maximumFileSize;
		}

		@Override
		public long getUsedDiskSpace() {
			return usedDiskSpace;
		}

		@Override
		public long getMaximumDiskSpace() {
			return maximumDiskSpace;
		}

		@Override
		public long getFreeDiskSpace() {
			return getMaximumDiskSpace() - getUsedDiskSpace();
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + getMajorVersion() + "."
				+ getMinorVersion();
	}
}
