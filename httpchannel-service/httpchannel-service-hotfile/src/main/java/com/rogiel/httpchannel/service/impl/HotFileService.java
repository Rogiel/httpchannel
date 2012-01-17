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
import org.apache.http.client.ClientProtocolException;
import org.htmlparser.Tag;

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
import com.rogiel.httpchannel.service.UploadChannel;
import com.rogiel.httpchannel.service.UploadService;
import com.rogiel.httpchannel.service.Uploader;
import com.rogiel.httpchannel.service.UploaderCapability;
import com.rogiel.httpchannel.service.channel.InputStreamDownloadChannel;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel.LinkedUploadChannelCloseCallback;
import com.rogiel.httpchannel.service.config.NullAuthenticatorConfiguration;
import com.rogiel.httpchannel.service.config.NullDownloaderConfiguration;
import com.rogiel.httpchannel.service.config.NullUploaderConfiguration;
import com.rogiel.httpchannel.service.exception.AuthenticationInvalidCredentialException;
import com.rogiel.httpchannel.util.htmlparser.HTMLPage;

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
			.compile("http://u[0-9]*\\.hotfile\\.com/upload\\.cgi\\?[0-9]*");

	private static final Pattern DOWNLOAD_DIRECT_LINK_PATTERN = Pattern
			.compile("http://hotfile\\.com/get/([0-9]*)/([A-Za-z0-9]*)/([A-Za-z0-9]*)/(.*)");
	// private static final Pattern DOWNLOAD_TIMER = Pattern
	// .compile("timerend=d\\.getTime\\(\\)\\+([0-9]*);");
	// private static final Pattern DOWNLOAD_FILESIZE = Pattern
	// .compile("[0-9]*(\\.[0-9]*)? (K|M|G)B");

	private static final Pattern DOWNLOAD_URI_PATTERN = Pattern
			.compile("http://hotfile\\.com/dl/([0-9]*)/([A-Za-z0-9]*)/(.*)");

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
		return new CapabilityMatrix<AuthenticatorCapability>();
	}

	protected class UploaderImpl extends
			AbstractUploader<NullUploaderConfiguration> implements
			Uploader<NullUploaderConfiguration>,
			LinkedUploadChannelCloseCallback {
		private Future<HTMLPage> uploadFuture;

		public UploaderImpl(String filename, long filesize,
				NullUploaderConfiguration configuration) {
			super(filename, filesize, configuration);
		}

		@Override
		public UploadChannel openChannel() throws IOException {
			logger.debug("Starting upload to hotfile.com");
			final HTMLPage page = get("http://www.hotfile.com/").asPage();
			final String action = page.findFormAction(UPLOAD_URI_PATTERN);

			logger.debug("Upload URI is {}", action);

			final LinkedUploadChannel channel = createLinkedChannel(this);

			uploadFuture = multipartPost(action)
					.parameter("uploads[]", channel).asPageAsync();
			return waitChannelLink(channel, uploadFuture);
		}

		@Override
		public String finish() throws IOException {
			try {
				return uploadFuture.get().getInputValue(DOWNLOAD_URI_PATTERN);
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
			super(uri, configuration);
		}

		@Override
		public DownloadChannel openChannel(DownloadListener listener,
				long position) throws IOException {
			logger.debug("Downloading {} from hotfile.com", uri);
			final HTMLPage page = get(uri).asPage();

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

			final String downloadUrl = page
					.findLink(DOWNLOAD_DIRECT_LINK_PATTERN);
			logger.debug("Download link is {}", downloadUrl);
			// final String tmHash = PatternUtils.find(DOWNLOAD_TMHASH_PATTERN,
			// content);F
			if (downloadUrl != null && downloadUrl.length() > 0) {
				final HttpResponse downloadResponse = get(downloadUrl)
						.request();

				final String filename = FilenameUtils.getName(downloadUrl);
				long contentLength = getContentLength(downloadResponse);

				return new InputStreamDownloadChannel(downloadResponse
						.getEntity().getContent(), contentLength, filename);
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
		public void login() throws ClientProtocolException, IOException {
			logger.debug("Authenticating hotfile.com");
			HTMLPage page = post("http://www.hotfile.com/login.php")
					.parameter("returnto", "/index.php")
					.parameter("user", credential.getUsername())
					.parameter("pass", credential.getPassword()).asPage();

			final Tag accountTag = page.getTagByID("account");
			if (accountTag == null)
				throw new AuthenticationInvalidCredentialException();
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
