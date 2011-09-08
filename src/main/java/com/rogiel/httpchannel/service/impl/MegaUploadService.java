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
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import net.sf.f2s.util.HttpClientUtils;
import net.sf.f2s.util.PatternUtils;
import net.sf.f2s.util.ThreadUtils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;

import com.rogiel.httpchannel.service.AbstractDownloader;
import com.rogiel.httpchannel.service.AbstractHttpService;
import com.rogiel.httpchannel.service.AuthenticationService;
import com.rogiel.httpchannel.service.Authenticator;
import com.rogiel.httpchannel.service.AuthenticatorCapability;
import com.rogiel.httpchannel.service.AuthenticatorListener;
import com.rogiel.httpchannel.service.CapabilityMatrix;
import com.rogiel.httpchannel.service.Credential;
import com.rogiel.httpchannel.service.DownloadChannel;
import com.rogiel.httpchannel.service.DownloadListener;
import com.rogiel.httpchannel.service.DownloadService;
import com.rogiel.httpchannel.service.Downloader;
import com.rogiel.httpchannel.service.DownloaderCapability;
import com.rogiel.httpchannel.service.Service;
import com.rogiel.httpchannel.service.UploadChannel;
import com.rogiel.httpchannel.service.UploadListener;
import com.rogiel.httpchannel.service.UploadListenerContentBody;
import com.rogiel.httpchannel.service.UploadService;
import com.rogiel.httpchannel.service.Uploader;
import com.rogiel.httpchannel.service.UploaderCapability;
import com.rogiel.httpchannel.service.channel.InputStreamDownloadChannel;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel.LinkedUploadChannelCloseCallback;
import com.rogiel.httpchannel.service.config.ServiceConfiguration;
import com.rogiel.httpchannel.service.config.ServiceConfigurationProperty;
import com.rogiel.httpchannel.service.impl.MegaUploadService.MegaUploadServiceConfiguration;

/**
 * This service handles login, upload and download to MegaUpload.com.
 * 
 * @author Rogiel
 * @since 1.0
 */
public class MegaUploadService extends
		AbstractHttpService<MegaUploadServiceConfiguration> implements Service,
		UploadService, DownloadService, AuthenticationService {
	private static final Pattern UPLOAD_URL_PATTERN = Pattern
			.compile("http://www([0-9]*)\\.megaupload\\.com/upload_done\\.php\\?UPLOAD_IDENTIFIER=[0-9]*");

	private static final Pattern DOWNLOAD_DIRECT_LINK_PATTERN = Pattern
			.compile("http://www([0-9]*)\\.megaupload\\.com/files/([A-Za-z0-9]*)/([^\"]*)");
	private static final Pattern DOWNLOAD_TIMER = Pattern
			.compile("count=([0-9]*);");
	// private static final Pattern DOWNLOAD_FILESIZE = Pattern
	// .compile("[0-9]*(\\.[0-9]*)? (K|M|G)B");

	private static final Pattern DOWNLOAD_URL_PATTERN = Pattern
			.compile("http://www\\.megaupload\\.com/\\?d=([A-Za-z0-9]*)");

	public MegaUploadService(final MegaUploadServiceConfiguration configuration) {
		super(configuration);
	}

	@Override
	public String getId() {
		return "megaupload";
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
	public Uploader getUploader(String description) {
		return new MegaUploadUploader(description);
	}

	@Override
	public long getMaximumFilesize() {
		return 1 * 1024 * 1024 * 1024;
	}

	@Override
	public CapabilityMatrix<UploaderCapability> getUploadCapabilities() {
		return new CapabilityMatrix<UploaderCapability>(
				UploaderCapability.UNAUTHENTICATED_UPLOAD,
				UploaderCapability.NON_PREMIUM_ACCOUNT_UPLOAD,
				UploaderCapability.PREMIUM_ACCOUNT_UPLOAD);
	}

	@Override
	public Downloader getDownloader(URL url) {
		return new MegaUploadDownloader(url);
	}

	@Override
	public boolean matchURL(URL url) {
		return false;
	}

	@Override
	public CapabilityMatrix<DownloaderCapability> getDownloadCapabilities() {
		return new CapabilityMatrix<DownloaderCapability>(
				DownloaderCapability.UNAUTHENTICATED_DOWNLOAD,
				DownloaderCapability.NON_PREMIUM_ACCOUNT_DOWNLOAD,
				DownloaderCapability.PREMIUM_ACCOUNT_DOWNLOAD);
	}

	@Override
	public Authenticator getAuthenticator(Credential credential) {
		return new MegaUploadAuthenticator(credential);
	}

	@Override
	public CapabilityMatrix<AuthenticatorCapability> getAuthenticationCapability() {
		return new CapabilityMatrix<AuthenticatorCapability>();
	}

	protected class MegaUploadUploader implements Uploader,
			LinkedUploadChannelCloseCallback {
		private final String description;

		private Future<String> uploadFuture;

		public MegaUploadUploader(String description) {
			this.description = (description != null ? description
					: configuration.getDefaultUploadDescription());
		}

		@Override
		public UploadChannel upload(UploadListener listener) throws IOException {
			final String body = HttpClientUtils.get(client,
					"http://www.megaupload.com/multiupload/");
			final String url = PatternUtils.find(UPLOAD_URL_PATTERN, body);

			final HttpPost upload = new HttpPost(url);
			final MultipartEntity entity = new MultipartEntity();
			upload.setEntity(entity);

			final LinkedUploadChannel channel = new LinkedUploadChannel(this,
					listener.getFilesize(), listener.getFilename());

			entity.addPart("multifile_0",
					new UploadListenerContentBody(channel));
			entity.addPart("multimessage_0", new StringBody(description));

			uploadFuture = HttpClientUtils.executeAsync(client, upload);
			while (!channel.isLinked() && !uploadFuture.isDone()) {
				ThreadUtils.sleep(100);
			}
			return channel;
		}

		@Override
		public String finish() throws IOException {
			try {
				return PatternUtils.find(DOWNLOAD_URL_PATTERN,
						uploadFuture.get());
			} catch (InterruptedException e) {
				return null;
			} catch (ExecutionException e) {
				throw (IOException) e.getCause();
			}
		}
	}

	protected class MegaUploadDownloader extends AbstractDownloader {
		private final URL url;

		public MegaUploadDownloader(URL url) {
			this.url = url;
		}

		@Override
		public DownloadChannel download(DownloadListener listener)
				throws IOException {
			final HttpGet request = new HttpGet(url.toString());
			final HttpResponse response = client.execute(request);
			final String content = IOUtils.toString(response.getEntity()
					.getContent());

			// try to find timer
			final String stringTimer = PatternUtils.find(DOWNLOAD_TIMER,
					content, 1);
			int timer = 0;
			if (stringTimer != null && stringTimer.length() > 0) {
				timer = Integer.parseInt(stringTimer);
			}
			if (timer > 0 && configuration.respectWaitTime()) {
				timer(listener, timer * 1000);
			}

			final String downloadUrl = PatternUtils.find(
					DOWNLOAD_DIRECT_LINK_PATTERN, content, 0);
			if (downloadUrl != null && downloadUrl.length() > 0) {
				final HttpGet downloadRequest = new HttpGet(downloadUrl);
				final HttpResponse downloadResponse = client
						.execute(downloadRequest);
				if (downloadResponse.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN
						|| downloadResponse.getStatusLine().getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE) {
					downloadResponse.getEntity().getContent().close();
					if (cooldown(listener, 60 * 1000))
						return download(listener); // retry download
				} else {
					final String filename = FilenameUtils.getName(downloadUrl);
					// listener.fileName(filename);

					final Header contentLengthHeader = downloadResponse
							.getFirstHeader("Content-Length");
					long contentLength = -1;
					if (contentLengthHeader != null) {
						contentLength = Long.valueOf(contentLengthHeader
								.getValue());
						// listener.fileSize(contentLength);
					}

					return new InputStreamDownloadChannel(downloadResponse
							.getEntity().getContent(), contentLength, filename);
				}
			} else {
				throw new IOException("Download link not found");
			}
			throw new IOException("Unknown error");
		}
	}

	protected class MegaUploadAuthenticator implements Authenticator {
		private final Credential credential;

		public MegaUploadAuthenticator(Credential credential) {
			this.credential = credential;
		}

		@Override
		public void login(AuthenticatorListener listener) {
			try {
				final HttpPost login = new HttpPost(
						"http://www.megaupload.com/?c=login");
				final MultipartEntity entity = new MultipartEntity();
				login.setEntity(entity);

				entity.addPart("login", new StringBody("1"));
				entity.addPart("username",
						new StringBody(credential.getUsername()));
				entity.addPart("password",
						new StringBody(credential.getPassword()));

				final String response = HttpClientUtils.execute(client, login);
				if (response.contains("Username and password do "
						+ "not match. Please try again!")) {
					listener.invalidCredentials(credential);
					return;
				}
				listener.loginSuccessful(credential);
			} catch (IOException e) {
				// throw new NestedLoginServiceException(e);
				// TODO throw an exception here
			}
		}

		@Override
		public void logout(AuthenticatorListener listener) {
			try {
				final HttpPost logout = new HttpPost(
						"http://www.megaupload.com/?c=account");
				final MultipartEntity entity = new MultipartEntity();
				logout.setEntity(entity);

				entity.addPart("logout", new StringBody("1"));
				HttpClientUtils.execute(client, logout);

				// TODO check logout status

				listener.logout(credential);
				return;
			} catch (IOException e) {
				// throw new NestedLoginServiceException(e);
				// TODO throw an exception here
			}
		}
	}

	public static interface MegaUploadServiceConfiguration extends
			ServiceConfiguration {
		@ServiceConfigurationProperty(key = "megaupload.wait", defaultValue = "true")
		boolean respectWaitTime();

		@ServiceConfigurationProperty(key = "megaupload.port", defaultValue = "80")
		int getPreferedDownloadPort();

		@ServiceConfigurationProperty(key = "megaupload.description", defaultValue = "Uploaded by seedbox-httpchannel")
		String getDefaultUploadDescription();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + getMajorVersion() + "."
				+ getMinorVersion();
	}
}
