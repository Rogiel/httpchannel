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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;

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
import com.rogiel.httpchannel.service.UploadService;
import com.rogiel.httpchannel.service.Uploader;
import com.rogiel.httpchannel.service.UploaderCapability;
import com.rogiel.httpchannel.service.channel.InputStreamDownloadChannel;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel.LinkedUploadChannelCloseCallback;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannelContentBody;
import com.rogiel.httpchannel.service.config.ServiceConfiguration;
import com.rogiel.httpchannel.service.config.ServiceConfigurationProperty;
import com.rogiel.httpchannel.service.exception.AuthenticationInvalidCredentialException;
import com.rogiel.httpchannel.service.exception.DownloadLimitExceededException;
import com.rogiel.httpchannel.service.exception.DownloadLinkNotFoundException;
import com.rogiel.httpchannel.service.exception.UploadLinkNotFoundException;
import com.rogiel.httpchannel.service.impl.MegaUploadService.MegaUploadServiceConfiguration;
import com.rogiel.httpchannel.util.HttpClientUtils;
import com.rogiel.httpchannel.util.PatternUtils;
import com.rogiel.httpchannel.util.ThreadUtils;
import com.rogiel.httpchannel.util.htmlparser.HTMLPage;

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
			.compile("http://www([0-9]*)\\.megaupload\\.com/files/([A-Za-z0-9]*)/(.*)");
	private static final Pattern DOWNLOAD_TIMER = Pattern
			.compile("count=([0-9]*);");
	// private static final Pattern DOWNLOAD_FILESIZE = Pattern
	// .compile("[0-9]*(\\.[0-9]*)? (K|M|G)B");

	private static final Pattern DOWNLOAD_URL_PATTERN = Pattern
			.compile("http://www\\.megaupload\\.com/\\?d=([A-Za-z0-9]*)");

	private static final Pattern LOGIN_USERNAME_PATTERN = Pattern
			.compile("flashvars\\.username = \"(.*)\";");

	public MegaUploadService(final MegaUploadServiceConfiguration configuration) {
		super(configuration);
	}

	@Override
	public String getID() {
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
	public Uploader getUploader(String filename, long filesize,
			String description) {
		return new MegaUploadUploader(filename, filesize, description);
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
		return new MegaUploadDownloader(url);
	}

	@Override
	public boolean matchURL(URL url) {
		return DOWNLOAD_URL_PATTERN.matcher(url.toString()).matches();
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
		private final String filename;
		private final long filesize;
		private final String description;

		private Future<String> uploadFuture;

		public MegaUploadUploader(String filename, long filesize,
				String description) {
			this.filename = filename;
			this.filesize = filesize;
			this.description = (description != null ? description
					: configuration.getDefaultUploadDescription());
		}

		@Override
		public UploadChannel upload() throws IOException {
			final HTMLPage page = getAsPage("http://www.megaupload.com/multiupload/");
			final String url = page.getFormAction(UPLOAD_URL_PATTERN);

			final LinkedUploadChannel channel = new LinkedUploadChannel(this,
					filesize, filename);
			final MultipartEntity entity = new MultipartEntity();

			entity.addPart("multifile_0", new LinkedUploadChannelContentBody(
					channel));
			entity.addPart("multimessage_0", new StringBody(description));

			uploadFuture = postAsStringAsync(url, entity);
			while (!channel.isLinked() && !uploadFuture.isDone()) {
				ThreadUtils.sleep(100);
			}
			return channel;
		}

		@Override
		public String finish() throws IOException {
			try {
				String link = PatternUtils.find(DOWNLOAD_URL_PATTERN,
						uploadFuture.get());
				if (link == null)
					throw new UploadLinkNotFoundException();
				return link;
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
		public DownloadChannel download(DownloadListener listener, long position)
				throws IOException {
			HttpResponse response = get(url.toString());

			// disable direct downloads, we don't support them!
			if (response.getEntity().getContentType().getValue()
					.equals("application/octet-stream")) {
				// close connection
				response.getEntity().getContent().close();

				final List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("do", "directdownloads"));
				pairs.add(new BasicNameValuePair("accountupdate", "1"));
				pairs.add(new BasicNameValuePair("set_ddl", "0"));

				// execute update
				postAsString("http://www.megaupload.com/?c=account",
						new UrlEncodedFormEntity(pairs));

				// execute and re-request download
				response = get(url.toString());
			}

			final HTMLPage page = HttpClientUtils.toPage(response);

			// try to find timer
			int timer = page.findIntegerInScript(DOWNLOAD_TIMER, 1);
			if (timer > 0 && configuration.respectWaitTime()) {
				timer(listener, timer * 1000);
			}
			final String downloadUrl = page
					.getLink(DOWNLOAD_DIRECT_LINK_PATTERN);
			if (downloadUrl != null && downloadUrl.length() > 0) {
				final HttpResponse downloadResponse = get(downloadUrl, position);
				if (downloadResponse.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN
						|| downloadResponse.getStatusLine().getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE) {
					downloadResponse.getEntity().getContent().close();
					throw new DownloadLimitExceededException("HTTP "
							+ downloadResponse.getStatusLine().getStatusCode()
							+ " response");
				} else {
					final String filename = FilenameUtils.getName(downloadUrl);
					final long contentLength = getContentLength(downloadResponse);

					return new InputStreamDownloadChannel(downloadResponse
							.getEntity().getContent(), contentLength, filename);
				}
			} else {
				throw new DownloadLinkNotFoundException();
			}
		}
	}

	protected class MegaUploadAuthenticator implements Authenticator {
		private final Credential credential;

		public MegaUploadAuthenticator(Credential credential) {
			this.credential = credential;
		}

		@Override
		public void login() throws IOException {
			final MultipartEntity entity = new MultipartEntity();

			entity.addPart("login", new StringBody("1"));
			entity.addPart("username", new StringBody(credential.getUsername()));
			entity.addPart("password", new StringBody(credential.getPassword()));

			final HTMLPage page = postAsPage(
					"http://www.megaupload.com/?c=login", entity);
			String username = page.findInScript(LOGIN_USERNAME_PATTERN, 1);

			if (username == null)
				throw new AuthenticationInvalidCredentialException();
		}

		@Override
		public void logout() throws IOException {
			final MultipartEntity entity = new MultipartEntity();
			entity.addPart("logout", new StringBody("1"));

			postAsString("http://www.megaupload.com/?c=account", entity);
			// TODO check logout status
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
