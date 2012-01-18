package com.rogiel.httpchannel.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import com.rogiel.httpchannel.service.Downloader.DownloaderConfiguration;
import com.rogiel.httpchannel.service.channel.InputStreamDownloadChannel;
import com.rogiel.httpchannel.service.exception.DownloadLimitExceededException;
import com.rogiel.httpchannel.service.exception.DownloadLinkNotFoundException;
import com.rogiel.httpchannel.service.exception.DownloadNotAuthorizedException;
import com.rogiel.httpchannel.service.exception.DownloadNotResumableException;

/**
 * An abstract {@link Downloader} that implements most of the general-purpose
 * methods
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 * @param <C>
 *            the {@link Downloader} configuration object type
 */
public abstract class AbstractDownloader<C extends DownloaderConfiguration>
		implements Downloader<C> {
	protected final DownloadService<?> service;
	/**
	 * The download URI
	 */
	protected final URI uri;

	/**
	 * The {@link Downloader} configuration
	 */
	protected final C configuration;

	/**
	 * Creates a new instance
	 * 
	 * @param service
	 *            the download service
	 * @param uri
	 *            the download uri
	 * @param configuration
	 *            the configuration object
	 */
	protected AbstractDownloader(DownloadService<?> service, URI uri,
			C configuration) {
		this.service = service;
		this.uri = uri;
		this.configuration = configuration;
	}

	@Override
	public DownloadChannel openChannel(long position) throws IOException,
			DownloadLinkNotFoundException, DownloadLimitExceededException,
			DownloadNotAuthorizedException, DownloadNotResumableException {
		return openChannel(null, position);
	}

	@Override
	public DownloadChannel openChannel(DownloadListener listener)
			throws IOException, DownloadLinkNotFoundException,
			DownloadLimitExceededException, DownloadNotAuthorizedException,
			DownloadNotResumableException {
		return openChannel(listener, 0);
	}

	@Override
	public DownloadChannel openChannel() throws IOException,
			DownloadLinkNotFoundException, DownloadLimitExceededException,
			DownloadNotAuthorizedException, DownloadNotResumableException {
		return openChannel(null, 0);
	}

	protected InputStreamDownloadChannel createInputStreamChannel(
			InputStream in, long length, String filename) {
		return new InputStreamDownloadChannel(service, this, in, length,
				filename);
	}

	@Override
	public C getConfiguration() {
		return configuration;
	}
}
