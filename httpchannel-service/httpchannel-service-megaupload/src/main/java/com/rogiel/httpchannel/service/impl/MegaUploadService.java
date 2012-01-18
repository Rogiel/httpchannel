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
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

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

	private static final Pattern UPLOAD_URI_PATTERN = Pattern
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
		return new CapabilityMatrix<AuthenticatorCapability>();
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
			final String uri = page.findFormAction(UPLOAD_URI_PATTERN);
			logger.debug("Upload URI is {}", uri);

			final LinkedUploadChannel channel = createLinkedChannel(this);
			uploadFuture = multipartPost(uri)
					.parameter("multimessage_0", configuration.description())
					.parameter("multifile_0", channel).asStringAsync();
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
		public void login() throws IOException {
			logger.debug("Starting login to megaupload.com");
			final HTMLPage page = post("http://www.megaupload.com/?c=login")
					.parameter("login", true)
					.parameter("username", credential.getUsername())
					.parameter("", credential.getPassword()).asPage();

			String username = page.findScript(LOGIN_USERNAME_PATTERN, 1);
			if (username == null)
				throw new AuthenticationInvalidCredentialException();
			serviceMode = ServiceMode.NON_PREMIUM;
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
