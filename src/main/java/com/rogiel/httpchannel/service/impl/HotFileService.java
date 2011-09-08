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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;

import com.rogiel.httpchannel.service.AbstractDownloader;
import com.rogiel.httpchannel.service.AbstractHttpService;
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
import com.rogiel.httpchannel.service.UploadChannel;
import com.rogiel.httpchannel.service.UploadListenerContentBody;
import com.rogiel.httpchannel.service.UploadService;
import com.rogiel.httpchannel.service.Uploader;
import com.rogiel.httpchannel.service.UploaderCapability;
import com.rogiel.httpchannel.service.channel.InputStreamDownloadChannel;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel.LinkedUploadChannelCloseCallback;
import com.rogiel.httpchannel.service.config.ServiceConfiguration;
import com.rogiel.httpchannel.service.impl.HotFileService.HotFileServiceConfiguration;
import com.rogiel.httpchannel.util.HttpClientUtils;
import com.rogiel.httpchannel.util.PatternUtils;
import com.rogiel.httpchannel.util.ThreadUtils;

/**
 * This service handles login, upload and download to HotFile.com.
 * 
 * @author Rogiel
 * @since 1.0
 */
public class HotFileService extends
		AbstractHttpService<HotFileServiceConfiguration> implements Service,
		UploadService, DownloadService, AuthenticationService {
	private static final Pattern UPLOAD_URL_PATTERN = Pattern
			.compile("http://u[0-9]*\\.hotfile\\.com/upload\\.cgi\\?[0-9]*");

	private static final Pattern DOWNLOAD_DIRECT_LINK_PATTERN = Pattern
			.compile("http://hotfile\\.com/get/([0-9]*)/([A-Za-z0-9]*)/([A-Za-z0-9]*)/([^\"]*)");
	private static final Pattern DOWNLOAD_TIMER = Pattern
			.compile("timerend=d\\.getTime\\(\\)\\+([0-9]*);");
	// private static final Pattern DOWNLOAD_FILESIZE = Pattern
	// .compile("[0-9]*(\\.[0-9]*)? (K|M|G)B");

	private static final Pattern DOWNLOAD_URL_PATTERN = Pattern
			.compile("http://hotfile\\.com/dl/([0-9]*)/([A-Za-z0-9]*)/([^\"]*)");

	public HotFileService(final HotFileServiceConfiguration configuration) {
		super(configuration);
	}

	@Override
	public String getId() {
		return "hotfile";
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
	public Uploader getUploader(String filename, long filesize,
			String description) {
		return new HotFileUploader(filename, filesize);
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
	public Downloader getDownloader(URL url) {
		return new HotFileDownloader(url);
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
		return new HotFileAuthenticator(credential);
	}

	@Override
	public CapabilityMatrix<AuthenticatorCapability> getAuthenticationCapability() {
		return new CapabilityMatrix<AuthenticatorCapability>();
	}

	protected class HotFileUploader implements Uploader,
			LinkedUploadChannelCloseCallback {
		private final String filename;
		private final long filesize;

		private Future<String> uploadFuture;

		public HotFileUploader(String filename, long filesize) {
			super();
			this.filename = filename;
			this.filesize = filesize;
		}

		@Override
		public UploadChannel upload() throws IOException {
			final String body = HttpClientUtils.get(client,
					"http://www.hotfile.com/");
			final String url = PatternUtils.find(UPLOAD_URL_PATTERN, body);

			final HttpPost upload = new HttpPost(url);
			final MultipartEntity entity = new MultipartEntity();
			upload.setEntity(entity);

			final LinkedUploadChannel channel = new LinkedUploadChannel(this,
					filesize, filename);

			entity.addPart("uploads[]", new UploadListenerContentBody(channel));

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

	protected class HotFileDownloader extends AbstractDownloader {
		private final URL url;

		public HotFileDownloader(URL url) {
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
					content, 2, 1);
			int timer = 0;
			if (stringTimer != null && stringTimer.length() > 0) {
				timer = Integer.parseInt(stringTimer);
			}
			if (timer > 0) {
				cooldown(listener, timer);
				return download(listener);
			}

			final String downloadUrl = PatternUtils.find(
					DOWNLOAD_DIRECT_LINK_PATTERN, content, 0);
			if (downloadUrl != null && downloadUrl.length() > 0) {
				final HttpGet downloadRequest = new HttpGet(downloadUrl);
				final HttpResponse downloadResponse = client
						.execute(downloadRequest);
				final String filename = FilenameUtils.getName(downloadUrl);

				final Header contentLengthHeader = downloadResponse
						.getFirstHeader("Content-Length");
				long contentLength = -1;
				if (contentLengthHeader != null) {
					contentLength = Long
							.valueOf(contentLengthHeader.getValue());
				}

				return new InputStreamDownloadChannel(downloadResponse
						.getEntity().getContent(), contentLength, filename);
			} else {
				throw new IOException("Download link not found");
			}
		}
	}

	protected class HotFileAuthenticator implements Authenticator {
		private final Credential credential;

		public HotFileAuthenticator(Credential credential) {
			this.credential = credential;
		}

		@Override
		public boolean login() throws ClientProtocolException, IOException {
			final HttpPost login = new HttpPost(
					"http://www.hotfile.com/login.php");
			final MultipartEntity entity = new MultipartEntity();
			login.setEntity(entity);

			entity.addPart("returnto", new StringBody("/index.php"));
			entity.addPart("user", new StringBody(credential.getUsername()));
			entity.addPart("pass", new StringBody(credential.getPassword()));

			String response = HttpClientUtils.execute(client, login);
			if (response.toLowerCase().contains(
					credential.getUsername().toLowerCase()))
				return true;
			return false;
		}

		@Override
		public boolean logout() throws IOException {
			final HttpPost logout = new HttpPost(
					"http://www.megaupload.com/?c=account");
			final MultipartEntity entity = new MultipartEntity();
			logout.setEntity(entity);

			entity.addPart("logout", new StringBody("1"));
			HttpClientUtils.execute(client, logout);

			// TODO check logout status

			return true;
		}
	}

	public static interface HotFileServiceConfiguration extends
			ServiceConfiguration {
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + getMajorVersion() + "."
				+ getMinorVersion();
	}
}
