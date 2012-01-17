package com.rogiel.httpchannel.service.impl;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import com.rogiel.httpchannel.http.PostMultipartRequest;
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
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel.LinkedUploadChannelCloseCallback;
import com.rogiel.httpchannel.service.config.NullAuthenticatorConfiguration;
import com.rogiel.httpchannel.service.config.NullDownloaderConfiguration;
import com.rogiel.httpchannel.service.exception.AuthenticationInvalidCredentialException;
import com.rogiel.httpchannel.service.exception.DownloadLimitExceededException;
import com.rogiel.httpchannel.service.exception.DownloadLinkNotFoundException;
import com.rogiel.httpchannel.service.exception.DownloadNotAuthorizedException;
import com.rogiel.httpchannel.service.exception.DownloadNotResumableException;
import com.rogiel.httpchannel.service.impl.MultiUploadUploaderConfiguration.MultiUploadMirrorService;
import com.rogiel.httpchannel.util.PatternUtils;
import com.rogiel.httpchannel.util.htmlparser.HTMLPage;

/**
 * This service handles uploads to MultiUpload.com.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel</a>
 * @since 1.0
 */
public class MultiUploadService extends AbstractHttpService implements Service,
		UploadService<MultiUploadUploaderConfiguration>,
		DownloadService<NullDownloaderConfiguration>,
		AuthenticationService<NullAuthenticatorConfiguration> {
	/**
	 * This service ID
	 */
	public static final ServiceID SERVICE_ID = ServiceID.create("multiupload");

	// http://www52.multiupload.com/upload/?UPLOAD_IDENTIFIER=73132658610746
	private static final Pattern UPLOAD_URL_PATTERN = Pattern
			.compile("http://www([0-9]*)\\.multiupload\\.com/upload/\\?UPLOAD_IDENTIFIER=[0-9]*");
	private static final Pattern DOWNLOAD_ID_PATTERN = Pattern
			.compile("\"downloadid\":\"([0-9a-zA-Z]*)\"");
	private static final Pattern DOWNLOAD_LINK_PATTERN = Pattern
			.compile("http://(www\\.)?multiupload\\.com/([0-9a-zA-Z]*)");
	private static final Pattern DIRECT_DOWNLOAD_LINK_PATTERN = Pattern
			.compile("http://www[0-9]*\\.multiupload\\.com(:[0-9]*)?/files/([0-9a-zA-Z]*)/(.*)");

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
	public Uploader<MultiUploadUploaderConfiguration> getUploader(
			String filename, long filesize,
			MultiUploadUploaderConfiguration configuration) {
		if (configuration == null)
			configuration = new MultiUploadUploaderConfiguration();
		return new UploaderImpl(filename, filesize, configuration);
	}

	@Override
	public Uploader<MultiUploadUploaderConfiguration> getUploader(
			String filename, long filesize) {
		return getUploader(filename, filesize, newUploaderConfiguration());
	}

	@Override
	public MultiUploadUploaderConfiguration newUploaderConfiguration() {
		return new MultiUploadUploaderConfiguration();
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
	public Downloader<NullDownloaderConfiguration> getDownloader(URL url,
			NullDownloaderConfiguration configuration) {
		return new DownloaderImpl(url, configuration);
	}

	@Override
	public Downloader<NullDownloaderConfiguration> getDownloader(URL url) {
		return getDownloader(url, newDownloaderConfiguration());
	}

	@Override
	public NullDownloaderConfiguration newDownloaderConfiguration() {
		return NullDownloaderConfiguration.SHARED_INSTANCE;
	}

	@Override
	public boolean matchURL(URL url) {
		return DOWNLOAD_LINK_PATTERN.matcher(url.toString()).matches();
	}

	@Override
	public CapabilityMatrix<DownloaderCapability> getDownloadCapabilities() {
		return new CapabilityMatrix<>(
				DownloaderCapability.UNAUTHENTICATED_DOWNLOAD,
				DownloaderCapability.UNAUTHENTICATED_RESUME,
				DownloaderCapability.NON_PREMIUM_ACCOUNT_DOWNLOAD,
				DownloaderCapability.NON_PREMIUM_ACCOUNT_RESUME);
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
			AbstractUploader<MultiUploadUploaderConfiguration> implements
			Uploader<MultiUploadUploaderConfiguration>,
			LinkedUploadChannelCloseCallback {
		private Future<String> uploadFuture;

		public UploaderImpl(String filename, long filesize,
				MultiUploadUploaderConfiguration configuration) {
			super(filename, filesize, configuration);
		}

		@Override
		public UploadChannel openChannel() throws IOException {
			logger.debug("Starting upload to multiupload.com");
			final String url = get("http://www.multiupload.com/").asPage()
					.findFormAction(UPLOAD_URL_PATTERN);
			logger.debug("Upload URL is {}", url);
			final LinkedUploadChannel channel = createLinkedChannel(this);

			PostMultipartRequest request = multipartPost(url).parameter(
					"description_0", configuration.description()).parameter(
					"file_0", channel);
			for (final MultiUploadMirrorService mirror : configuration
					.uploadServices()) {
				logger.debug("Adding {} as mirror", mirror.name());
				request.parameter("service_" + mirror.id, 1);
			}

			uploadFuture = request.asStringAsync();
			return waitChannelLink(channel, uploadFuture);
		}

		@Override
		public String finish() throws IOException {
			try {
				final String linkId = PatternUtils.find(DOWNLOAD_ID_PATTERN,
						uploadFuture.get(), 1);
				logger.debug("Upload to multiupload.com finished");
				if (linkId == null)
					return null;
				return new StringBuilder("http://www.multiupload.com/").append(
						linkId).toString();
			} catch (InterruptedException e) {
				return null;
			} catch (ExecutionException e) {
				throw (IOException) e.getCause();
			}
		}
	}

	protected class DownloaderImpl extends
			AbstractHttpDownloader<NullDownloaderConfiguration> implements
			Downloader<NullDownloaderConfiguration> {
		protected DownloaderImpl(URL url,
				NullDownloaderConfiguration configuration) {
			super(url, configuration);
		}

		@Override
		public DownloadChannel openChannel(DownloadListener listener,
				long position) throws IOException,
				DownloadLinkNotFoundException, DownloadLimitExceededException,
				DownloadNotAuthorizedException, DownloadNotResumableException {
			final HTMLPage page = get(url).asPage();
			final String link = page.findLink(DIRECT_DOWNLOAD_LINK_PATTERN);
			logger.debug("Direct download link is {}", link);
			if (link == null)
				throw new DownloadLinkNotFoundException();
			return download(get(link).position(position));
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
			final HTMLPage page = post("http://www.multiupload.com/login")
					.parameter("username", credential.getUsername())
					.parameter("password", credential.getPassword()).asPage();

			if (!page.containsIgnoreCase(credential.getUsername()))
				throw new AuthenticationInvalidCredentialException();
		}

		@Override
		public void logout() throws IOException {
			post("http://www.multiupload.com/login").parameter("do", "logout")
					.request();
			// TODO check logout status
		}
	}
}
