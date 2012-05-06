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
package com.rogiel.httpchannel.service.twoshared;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import com.rogiel.httpchannel.captcha.exception.UnsolvableCaptchaServiceException;
import com.rogiel.httpchannel.service.AbstractHttpDownloader;
import com.rogiel.httpchannel.service.AbstractHttpService;
import com.rogiel.httpchannel.service.AbstractUploader;
import com.rogiel.httpchannel.service.CapabilityMatrix;
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
import com.rogiel.httpchannel.service.config.NullDownloaderConfiguration;
import com.rogiel.httpchannel.service.config.NullUploaderConfiguration;
import com.rogiel.httpchannel.service.exception.DownloadLimitExceededException;
import com.rogiel.httpchannel.service.exception.DownloadLinkNotFoundException;
import com.rogiel.httpchannel.service.exception.DownloadNotAuthorizedException;
import com.rogiel.httpchannel.service.exception.DownloadNotResumableException;
import com.rogiel.httpchannel.service.exception.NoCaptchaServiceException;
import com.rogiel.httpchannel.util.ExceptionUtils;
import com.rogiel.httpchannel.util.html.Page;

/**
 * This service handles uploads to TwoShared.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel</a>
 * @since 1.0
 */
public class TwoSharedService extends AbstractHttpService implements Service,
		UploadService<NullUploaderConfiguration>,
		DownloadService<NullDownloaderConfiguration> {
	/**
	 * This service ID
	 */
	public static final ServiceID SERVICE_ID = ServiceID.create("twoshared");

	private static final Pattern UPLOAD_URL_PATTERN = Pattern
			.compile("http://dc[0-9]*\\.2shared\\.com/main/upload2\\.jsp\\?sId=[A-z0-9]*");
	private static final Pattern UPLOAD_ID_PATTERN = Pattern
			.compile("sId=([A-z0-9]*)");

	private static final Pattern DOWNLOAD_URL_PATTERN = Pattern
			.compile("http://(www\\.)?2shared\\.com/document/[A-z0-9]*/.*");
	private static final Pattern DIRECT_DOWNLOAD_URL_PATTERN = Pattern
			.compile("http://dc[0-9]+\\.2shared\\.com/download/[A-z0-9]+/.+\\?tsid=[0-9]{8}-[0-9]{6}-[A-z0-9]{8}");

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
		// no filesize limit
		return -1;
	}

	@Override
	public String[] getSupportedExtensions() {
		// no extension restriction
		return null;
	}

	@Override
	public CapabilityMatrix<UploaderCapability> getUploadCapabilities() {
		return new CapabilityMatrix<UploaderCapability>(
				UploaderCapability.UNAUTHENTICATED_UPLOAD);
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
		return DOWNLOAD_URL_PATTERN.matcher(uri.toString()).matches();
	}

	@Override
	public CapabilityMatrix<DownloaderCapability> getDownloadCapabilities() {
		return new CapabilityMatrix<DownloaderCapability>(
				DownloaderCapability.UNAUTHENTICATED_DOWNLOAD,
				DownloaderCapability.UNAUTHENTICATED_RESUME);
	}

	private class UploaderImpl extends
			AbstractUploader<NullUploaderConfiguration> implements
			Uploader<NullUploaderConfiguration>,
			LinkedUploadChannelCloseCallback {
		private Future<Page> uploadFuture;
		private String uploadID;

		public UploaderImpl(String filename, long filesize,
				NullUploaderConfiguration configuration) {
			super(TwoSharedService.this, filename, filesize, configuration);
		}

		@Override
		public UploadChannel openChannel() throws IOException {
			logger.debug("Starting upload to TwoShared");
			final Page page = get("http://www.2shared.com/").asPage();

			// locate upload uri
			final String uri = page.form(UPLOAD_URL_PATTERN).asString();
			final String mainDC = page.inputByName("mainDC").asString();
			uploadID = page.search(UPLOAD_ID_PATTERN).asString(1);

			logger.debug("Upload URI: {}, DC: {}", uri, mainDC);

			// create a new channel
			final LinkedUploadChannel channel = createLinkedChannel(this);
			uploadFuture = multipartPost(uri).parameter("fff", channel)
					.parameter("mainDC", mainDC).asPageAsync();

			// wait for channel link
			return waitChannelLink(channel);
		}

		@Override
		public String finish() throws IOException {
			try {
				uploadFuture.get();
				final Page page = get(
						"http://www.2shared.com/uploadComplete.jsp?sId="
								+ uploadID).asPage();
				return page.textareaByID("downloadLink").asString();
			} catch (InterruptedException e) {
				return null;
			} catch (ExecutionException e) {
				ExceptionUtils.asIOException(e);
				return null;
			}
		}
	}

	private class DownloaderImpl extends
			AbstractHttpDownloader<NullDownloaderConfiguration> implements
			Downloader<NullDownloaderConfiguration> {
		/**
		 * @param uri
		 *            the download uri
		 * @param configuration
		 *            the downloader configuration
		 */
		protected DownloaderImpl(URI uri,
				NullDownloaderConfiguration configuration) {
			super(TwoSharedService.this, uri, configuration);

		}

		@Override
		public DownloadChannel openChannel(DownloadListener listener,
				long position) throws IOException,
				DownloadLinkNotFoundException, DownloadLimitExceededException,
				DownloadNotAuthorizedException, DownloadNotResumableException,
				UnsolvableCaptchaServiceException, NoCaptchaServiceException {
			final Page page = get(uri).asPage();
			final String downloadUri = page.script(
					DIRECT_DOWNLOAD_URL_PATTERN).asString();
			return download(get(downloadUri));
		}
	}
}
