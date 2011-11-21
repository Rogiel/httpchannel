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
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.htmlparser.Tag;

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
import com.rogiel.httpchannel.service.ServiceID;
import com.rogiel.httpchannel.service.UploadChannel;
import com.rogiel.httpchannel.service.UploadService;
import com.rogiel.httpchannel.service.Uploader;
import com.rogiel.httpchannel.service.UploaderCapability;
import com.rogiel.httpchannel.service.channel.InputStreamDownloadChannel;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel.LinkedUploadChannelCloseCallback;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannelContentBody;
import com.rogiel.httpchannel.service.config.ServiceConfiguration;
import com.rogiel.httpchannel.service.config.ServiceConfigurationHelper;
import com.rogiel.httpchannel.service.exception.AuthenticationInvalidCredentialException;
import com.rogiel.httpchannel.service.impl.HotFileService.HotFileServiceConfiguration;
import com.rogiel.httpchannel.util.ThreadUtils;
import com.rogiel.httpchannel.util.htmlparser.HTMLPage;

/**
 * This service handles login, upload and download to HotFile.com.
 * 
 * @author Rogiel
 * @since 1.0
 */
public class HotFileService extends
		AbstractHttpService<HotFileServiceConfiguration> implements Service,
		UploadService, DownloadService, AuthenticationService {
	/**
	 * This service ID
	 */
	public static final ServiceID SERVICE_ID = ServiceID.create("hotfile");
	
	private static final Pattern UPLOAD_URL_PATTERN = Pattern
			.compile("http://u[0-9]*\\.hotfile\\.com/upload\\.cgi\\?[0-9]*");

	private static final Pattern DOWNLOAD_DIRECT_LINK_PATTERN = Pattern
			.compile("http://hotfile\\.com/get/([0-9]*)/([A-Za-z0-9]*)/([A-Za-z0-9]*)/(.*)");
	// private static final Pattern DOWNLOAD_TIMER = Pattern
	// .compile("timerend=d\\.getTime\\(\\)\\+([0-9]*);");
	// private static final Pattern DOWNLOAD_FILESIZE = Pattern
	// .compile("[0-9]*(\\.[0-9]*)? (K|M|G)B");

	private static final Pattern DOWNLOAD_URL_PATTERN = Pattern
			.compile("http://hotfile\\.com/dl/([0-9]*)/([A-Za-z0-9]*)/(.*)");

	public HotFileService() {
		super(ServiceConfigurationHelper
				.defaultConfiguration(HotFileServiceConfiguration.class));
	}

	@Override
	public ServiceID getID() {
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
		return DOWNLOAD_URL_PATTERN.matcher(url.toString()).matches();
	}

	@Override
	public CapabilityMatrix<DownloaderCapability> getDownloadCapabilities() {
		return new CapabilityMatrix<DownloaderCapability>(
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

		private Future<HTMLPage> uploadFuture;

		public HotFileUploader(String filename, long filesize) {
			super();
			this.filename = filename;
			this.filesize = filesize;
		}

		@Override
		public UploadChannel upload() throws IOException {
			final HTMLPage page = getAsPage("http://www.hotfile.com/");
			final String action = page.getFormAction(UPLOAD_URL_PATTERN);

			final LinkedUploadChannel channel = new LinkedUploadChannel(this,
					filesize, filename);
			final MultipartEntity entity = new MultipartEntity();

			entity.addPart("uploads[]", new LinkedUploadChannelContentBody(
					channel));

			uploadFuture = postAsPageAsync(action, entity);
			while (!channel.isLinked() && !uploadFuture.isDone()) {
				ThreadUtils.sleep(100);
			}
			return channel;
		}

		@Override
		public String finish() throws IOException {
			try {
				return uploadFuture.get().getInputValue(DOWNLOAD_URL_PATTERN);
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
		public DownloadChannel download(DownloadListener listener, long position)
				throws IOException {
			final HTMLPage page = getAsPage(url.toString());

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
					.getLink(DOWNLOAD_DIRECT_LINK_PATTERN);
			// final String tmHash = PatternUtils.find(DOWNLOAD_TMHASH_PATTERN,
			// content);F
			if (downloadUrl != null && downloadUrl.length() > 0) {
				final HttpResponse downloadResponse = get(downloadUrl);

				final String filename = FilenameUtils.getName(downloadUrl);
				long contentLength = getContentLength(downloadResponse);

				return new InputStreamDownloadChannel(downloadResponse
						.getEntity().getContent(), contentLength, filename);
				// } else if (tmHash != null) {
				// String dlUrl = PatternUtils.find(FREE_DOWNLOAD_URL_PATTERN,
				// content);
				//
				// String action = PatternUtils.find(DOWNLOAD_ACTION_PATTERN,
				// content, 1);
				// int tm = PatternUtils.findInt(DOWNLOAD_TM_PATTERN, content,
				// 1);
				// int wait = PatternUtils.findInt(DOWNLOAD_WAIT_PATTERN,
				// content,
				// 1);
				// String waitHash =
				// PatternUtils.find(DOWNLOAD_WAITHASH_PATTERN,
				// content, 1);
				// String upId = PatternUtils.find(DOWNLOAD_UPIDHASH_PATTERN,
				// content, 1);
				//
				// System.out.println("Wait time: "+wait);
				//
				// if (wait > 0)
				// timer(listener, wait * 1000);
				//
				// final HttpPost downloadPost = new
				// HttpPost("http://www.hotfile.com"+dlUrl);
				// final List<NameValuePair> pairs = new
				// ArrayList<NameValuePair>();
				// pairs.add(new BasicNameValuePair("action", action));
				// pairs.add(new BasicNameValuePair("tm",
				// Integer.toString(tm)));
				// pairs.add(new BasicNameValuePair("tmhash", tmHash));
				// pairs.add(new BasicNameValuePair("wait",
				// Integer.toString(wait)));
				// pairs.add(new BasicNameValuePair("waithash", waitHash));
				// pairs.add(new BasicNameValuePair("upidhash", upId));
				//
				// downloadPost.setEntity(new UrlEncodedFormEntity(pairs));
				//
				// final HttpResponse downloadResponse = client
				// .execute(downloadPost);
				// System.out.println(IOUtils.toString(downloadResponse.getEntity().getContent()));
				//
				// return new InputStreamDownloadChannel(downloadResponse
				// .getEntity().getContent(), 0, "haha");
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
		public void login() throws ClientProtocolException, IOException {
			final MultipartEntity entity = new MultipartEntity();

			entity.addPart("returnto", new StringBody("/index.php"));
			entity.addPart("user", new StringBody(credential.getUsername()));
			entity.addPart("pass", new StringBody(credential.getPassword()));

			HTMLPage page = postAsPage("http://www.hotfile.com/login.php",
					entity);
			final Tag accountTag = page.getTagByID("account");
			if (accountTag == null)
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

	public static interface HotFileServiceConfiguration extends
			ServiceConfiguration {
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + getMajorVersion() + "."
				+ getMinorVersion();
	}
}
