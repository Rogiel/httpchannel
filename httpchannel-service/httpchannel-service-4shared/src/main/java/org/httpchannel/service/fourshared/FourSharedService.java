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
package org.httpchannel.service.fourshared;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.pmstation.shared.soap.client.ApiException;
import com.pmstation.shared.soap.client.DesktopAppJax2;
import com.pmstation.shared.soap.client.DesktopAppJax2Service;
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
import com.rogiel.httpchannel.service.exception.AuthenticationServiceException;
import com.rogiel.httpchannel.service.exception.ChannelServiceException;
import com.rogiel.httpchannel.service.exception.DownloadLinkNotFoundException;
import com.rogiel.httpchannel.util.htmlparser.HTMLPage;

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

	private String username;
	private String password;

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
		try {
			final long free = api.getFreeSpace(username, password);
			final long max = api.getMaxFileSize(username, password);
			if (max < free)
				return max;
			else
				return free;
		} catch (ApiException e) {
			return 0;
		}
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
		return new CapabilityMatrix<AuthenticatorCapability>();
	}

	protected class UploaderImpl extends
			AbstractUploader<NullUploaderConfiguration> implements
			Uploader<NullUploaderConfiguration>,
			LinkedUploadChannelCloseCallback {
		private Future<HTMLPage> uploadFuture;

		public UploaderImpl(String filename, long filesize,
				NullUploaderConfiguration configuration) {
			super(FourSharedService.this, filename, filesize, configuration);
		}

		@Override
		public UploadChannel openChannel() throws IOException {
			try {
				logger.debug("Starting upload to 4shared.com");
				final String sessionID = api.createUploadSessionKey(username,
						password, -1);
				logger.debug("SessionID: {}", sessionID);
				if (sessionID == null || sessionID.length() == 0)
					throw new ChannelServiceException("SessionID is invalid");

				final long datacenterID = api.getNewFileDataCenter(username,
						password);
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
				return waitChannelLink(channel, uploadFuture);
			} catch (ApiException e) {
				throw new ChannelServiceException(e);
			}
		}

		@Override
		public String finish() throws IOException {
			try {
				final long linkID = Long.parseLong(uploadFuture.get()
						.getInputValueById("uploadedFileId"));
				return api.getFileDownloadLink(username, password, linkID);
			} catch (InterruptedException e) {
				return null;
			} catch (ExecutionException e) {
				throw (IOException) e.getCause();
			} catch (ApiException e) {
				throw new DownloadLinkNotFoundException(e);
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
			logger.debug("Logging to 4shared.com");

			final String response = api.login(credential.getUsername(),
					credential.getPassword());
			username = credential.getUsername();
			password = credential.getPassword();

			try {
				if (api.isAccountPremium(username, password))
					serviceMode = ServiceMode.PREMIUM;
				else
					serviceMode = ServiceMode.NON_PREMIUM;
			} catch (ApiException e) {
				throw new AuthenticationServiceException(e);
			}

			if (!response.isEmpty())
				throw new AuthenticationInvalidCredentialException();
		}

		@Override
		public void logout() throws IOException {
			username = null;
			password = null;
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + getMajorVersion() + "."
				+ getMinorVersion();
	}
}
