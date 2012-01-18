/*
 * This file is part of seedbox <github.com/seedbox>.
 *
 * seedbox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * seedbox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with seedbox.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.rogiel.httpchannel.service.impl;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import com.rogiel.httpchannel.filesonic.FileSonicAPI;
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
import com.rogiel.httpchannel.util.PatternUtils;

/**
 * This service handles login, upload and download to MegaUpload.com.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @since 1.0
 */
public class FileSonicService extends AbstractHttpService implements Service,
		UploadService<NullUploaderConfiguration>,
		AuthenticationService<NullAuthenticatorConfiguration> {
	/**
	 * This service ID
	 */
	public static final ServiceID SERVICE_ID = ServiceID.create("megaupload");

	/**
	 * The download URI pattern
	 */
	private static final Pattern DOWNLOAD_URI_PATTERN = Pattern
			.compile("http://www.filesonic.com/file/[0-9A-z]*");
	/**
	 * The FileSonic API
	 */
	private final FileSonicAPI api = new FileSonicAPI();

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
		try {
			return api.getMaxFilesize();
		} catch (IOException e) {
			return -1;
		}
	}

	@Override
	public String[] getSupportedExtensions() {
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
		private Future<String> uploadFuture;

		public UploaderImpl(String filename, long filesize,
				NullUploaderConfiguration configuration) {
			super(FileSonicService.this, filename, filesize, configuration);
		}

		@Override
		public UploadChannel openChannel() throws IOException {
			logger.debug("Starting upload to filesonic.com");
			final LinkedUploadChannel channel = createLinkedChannel(this);
			uploadFuture = multipartPost(api.getUploadURI().toString())
					.parameter("files[]", channel).asStringAsync();
			return waitChannelLink(channel, uploadFuture);
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

	protected class AuthenticatorImpl extends
			AbstractAuthenticator<NullAuthenticatorConfiguration> implements
			Authenticator<NullAuthenticatorConfiguration> {
		public AuthenticatorImpl(Credential credential,
				NullAuthenticatorConfiguration configuration) {
			super(credential, configuration);
		}

		@Override
		public void login() throws IOException {
			logger.debug("Logging to filesonic.com");
			api.login(credential.getUsername(), credential.getPassword());
			serviceMode = ServiceMode.NON_PREMIUM;
			// if (username == null)
			// throw new AuthenticationInvalidCredentialException();
		}

		@Override
		public void logout() throws IOException {
			post("http://www.megaupload.com/?c=account").parameter("logout",
					true).request();
			// TODO check logout status
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + getMajorVersion() + "."
				+ getMinorVersion();
	}
}
