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
package com.rogiel.httpchannel.service.fourshared;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.pmstation.shared.soap.client.ApiException;
import com.pmstation.shared.soap.client.DesktopAppJax2;
import com.pmstation.shared.soap.client.DesktopAppJax2Service;
import com.rogiel.httpchannel.service.AbstractAccountDetails;
import com.rogiel.httpchannel.service.AbstractAuthenticator;
import com.rogiel.httpchannel.service.AbstractHttpService;
import com.rogiel.httpchannel.service.AbstractUploader;
import com.rogiel.httpchannel.service.AccountDetails;
import com.rogiel.httpchannel.service.AccountDetails.DiskQuotaAccountDetails;
import com.rogiel.httpchannel.service.AccountDetails.FilesizeLimitAccountDetails;
import com.rogiel.httpchannel.service.AccountDetails.PremiumAccountDetails;
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
import com.rogiel.httpchannel.service.exception.ChannelServiceException;
import com.rogiel.httpchannel.service.exception.DownloadLinkNotFoundException;
import com.rogiel.httpchannel.util.html.Page;

/**
 * This service handles uploads to 4shared.com.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel</a>
 * @since 1.0
 */
public class FourSharedService extends AbstractHttpService implements Service,
		UploadService<NullUploaderConfiguration>,
		AuthenticationService<NullAuthenticatorConfiguration> {
	/**
	 * This service ID
	 */
	public static final ServiceID SERVICE_ID = ServiceID.create("4shared");

	private final DesktopAppJax2 api = new DesktopAppJax2Service()
			.getDesktopAppJax2Port();

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
		return new CapabilityMatrix<ServiceMode>(ServiceMode.UNAUTHENTICATED);
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
		// no configuration
		return NullUploaderConfiguration.SHARED_INSTANCE;
	}

	@Override
	public long getMaximumFilesize() {
		final long max = account.as(FilesizeLimitAccountDetails.class)
				.getMaximumFilesize();
		if(max <= -1)
			return -1;
		final long free = account.as(DiskQuotaAccountDetails.class)
				.getFreeDiskSpace();
		if (max < free)
			return max;
		else
			return free;
	}

	@Override
	public String[] getSupportedExtensions() {
		// no extension restriction
		return null;
	}

	@Override
	public CapabilityMatrix<UploaderCapability> getUploadCapabilities() {
		return new CapabilityMatrix<UploaderCapability>(
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

	private class UploaderImpl extends
			AbstractUploader<NullUploaderConfiguration> implements
			Uploader<NullUploaderConfiguration>,
			LinkedUploadChannelCloseCallback {
		private Future<Page> uploadFuture;

		public UploaderImpl(String filename, long filesize,
				NullUploaderConfiguration configuration) {
			super(FourSharedService.this, filename, filesize, configuration);
		}

		@Override
		public UploadChannel openChannel() throws IOException {
			try {
				logger.debug("Starting upload to 4shared.com");
				final String sessionID = api.createUploadSessionKey(
						account.getUsername(), getPassword(), -1);
				logger.debug("SessionID: {}", sessionID);
				if (sessionID == null || sessionID.length() == 0)
					throw new ChannelServiceException("SessionID is invalid");

				final long datacenterID = api.getNewFileDataCenter(
						account.getUsername(), getPassword());
				logger.debug("DatacenterID: {}", datacenterID);
				if (datacenterID <= 0)
					throw new ChannelServiceException("DatacenterID is invalid");

				final String uri = api.getUploadFormUrl((int) datacenterID,
						sessionID);
				logger.debug("Upload URI: {}", uri);

				// create a new channel
				final LinkedUploadChannel channel = createLinkedChannel(this);
				uploadFuture = multipartPost(uri)
						.parameter("FilePart", channel).asPageAsync();

				// wait for channel link
				return waitChannelLink(channel);
			} catch (ApiException e) {
				throw new ChannelServiceException(e);
			}
		}

		@Override
		public String finish() throws IOException {
			try {
				final long linkID = uploadFuture.get()
						.inputByID("uploadedFileId").asLong();
				return api.getFileDownloadLink(account.getUsername(),
						getPassword(), linkID);
			} catch (InterruptedException e) {
				return null;
			} catch (ExecutionException e) {
				throw (IOException) e.getCause();
			} catch (ApiException e) {
				throw new DownloadLinkNotFoundException(e);
			}
		}
	}

	private class AuthenticatorImpl extends
			AbstractAuthenticator<NullAuthenticatorConfiguration> implements
			Authenticator<NullAuthenticatorConfiguration> {
		public AuthenticatorImpl(Credential credential,
				NullAuthenticatorConfiguration configuration) {
			super(credential, configuration);
		}

		@Override
		public AccountDetails login() throws IOException {
			logger.debug("Logging to 4shared.com");

			final String response = api.login(credential.getUsername(),
					credential.getPassword());

			if (!response.isEmpty())
				throw new AuthenticationInvalidCredentialException();
			return (account = new AccountDetailsImpl(credential.getUsername(),
					credential.getPassword()));
		}

		@Override
		public void logout() throws IOException {
			account = null;
		}
	}

	private class AccountDetailsImpl extends AbstractAccountDetails implements
			PremiumAccountDetails, DiskQuotaAccountDetails,
			FilesizeLimitAccountDetails {
		private final String password;

		private AccountDetailsImpl(String username, String password) {
			super(FourSharedService.this, username);
			this.password = password;
		}

		@Override
		public boolean isActive() {
			try {
				return api.isAccountActive(username, password);
			} catch (ApiException e) {
				return false;
			}
		}

		@Override
		public boolean isPremium() {
			try {
				return api.isAccountPremium(username, password);
			} catch (ApiException e) {
				return false;
			}
		}

		@Override
		public long getFreeDiskSpace() {
			try {
				return api.getFreeSpace(username, password);
			} catch (ApiException e) {
				return -1;
			}
		}

		@Override
		public long getUsedDiskSpace() {
			return getMaximumDiskSpace() - getFreeDiskSpace();
		}

		@Override
		public long getMaximumDiskSpace() {
			try {
				return api.getSpaceLimit(username, password);
			} catch (ApiException e) {
				return -1;
			}
		}

		@Override
		public long getMaximumFilesize() {
			try {
				return api.getMaxFileSize(username, password);
			} catch (ApiException e) {
				return -1;
			}
		}
	}

	private String getPassword() {
		if (account == null) {
			return null;
		} else {
			return ((AccountDetailsImpl) account).password;
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + getMajorVersion() + "."
				+ getMinorVersion();
	}
}
