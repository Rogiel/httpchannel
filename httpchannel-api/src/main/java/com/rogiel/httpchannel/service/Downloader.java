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
package com.rogiel.httpchannel.service;

import java.io.IOException;

import com.rogiel.httpchannel.captcha.CaptchaService;
import com.rogiel.httpchannel.captcha.exception.UnsolvableCaptchaServiceException;
import com.rogiel.httpchannel.service.Downloader.DownloaderConfiguration;
import com.rogiel.httpchannel.service.exception.DownloadLimitExceededException;
import com.rogiel.httpchannel.service.exception.DownloadLinkNotFoundException;
import com.rogiel.httpchannel.service.exception.DownloadNotAuthorizedException;
import com.rogiel.httpchannel.service.exception.DownloadNotResumableException;
import com.rogiel.httpchannel.service.exception.NoCaptchaServiceException;

/**
 * This interfaces provides downloading for an service.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @since 1.0
 */
public interface Downloader<C extends DownloaderConfiguration> {
	/**
	 * Opens a new {@link DownloadChannel} that will be immediately ready to
	 * read data from the download stream.
	 * <p>
	 * Whether an channel is returned or any exception is thrown it is possible
	 * to reuse the same instance for more than one download, this, however, is
	 * very unlikely to be done. The most common usage usage scenario for this
	 * is when an {@link DownloadNotResumableException} is thrown and you wish
	 * to restart the download from start giving <tt>position</tt> equal to
	 * zero, in such scenario, reutilizing the same {@link Downloader} instance
	 * is safe.
	 * <p>
	 * Please remember to close the channel by calling
	 * {@link DownloadChannel#close()}, otherwise some of the resources (such as
	 * network connections and file handlers) might still be open for the whole
	 * runtime or until they time out, which could never occur.
	 * 
	 * @param listener
	 *            the listener to keep a track on the download progress
	 * @param position
	 *            the download start position. If seek is supported by service.
	 *            If zero, download will start from the beginning.
	 * 
	 * @return the {@link DownloadChannel} instance
	 * @throws IOException
	 *             if any IO error occur
	 * @throws DownloadLinkNotFoundException
	 *             if the direct download link cannot be found (the file could
	 *             have been deleted)
	 * @throws DownloadLimitExceededException
	 *             if the download limit has been exceed, most times thrown when
	 *             downloading as a non-premium user
	 * @throws DownloadNotAuthorizedException
	 *             if the user (or guest) account does not have necessary rights
	 *             to download the file
	 * @throws DownloadNotResumableException
	 *             if the download cannot be started at <tt>position</tt>. Will
	 *             only be thrown if <tt>position > 0</tt>.
	 * @throws UnsolvableCaptchaServiceException
	 *             if the service required captcha resolving but no
	 *             {@link CaptchaService} was available or the service did not
	 *             solve the challenge
	 * @throws NoCaptchaServiceException
	 *             if the service required an {@link CaptchaService}
	 *             implementation to be present, but none was available
	 */
	DownloadChannel openChannel(DownloadListener listener, long position)
			throws IOException, DownloadLinkNotFoundException,
			DownloadLimitExceededException, DownloadNotAuthorizedException,
			DownloadNotResumableException, UnsolvableCaptchaServiceException,
			NoCaptchaServiceException;

	/**
	 * Opens a new {@link DownloadChannel} with no listener. For more details,
	 * see {@link #openChannel(DownloadListener, long)}
	 * 
	 * @param position
	 *            the download start position. If seek is supported by service.
	 *            If zero, download will start from the beginning.
	 * @return the {@link DownloadChannel} instance
	 * @throws IOException
	 *             if any IO error occur
	 * @throws DownloadLinkNotFoundException
	 *             if the direct download link cannot be found (the file could
	 *             have been deleted)
	 * @throws DownloadLimitExceededException
	 *             if the download limit has been exceed, most times thrown when
	 *             downloading as a non-premium user
	 * @throws DownloadNotAuthorizedException
	 *             if the user (or guest) account does not have necessary rights
	 *             to download the file
	 * @throws DownloadNotResumableException
	 *             if the download cannot be started at <tt>position</tt>. Will
	 *             only be thrown if <tt>position > 0</tt>.
	 * @throws UnsolvableCaptchaServiceException
	 *             if the service required captcha resolving but no
	 *             {@link CaptchaService} was available or the service did not
	 *             solve the challenge
	 * @throws NoCaptchaServiceException
	 *             if the service required an {@link CaptchaService}
	 *             implementation to be present, but none was available
	 * 
	 */
	DownloadChannel openChannel(long position) throws IOException,
			DownloadLinkNotFoundException, DownloadLimitExceededException,
			DownloadNotAuthorizedException, DownloadNotResumableException,
			UnsolvableCaptchaServiceException, NoCaptchaServiceException;

	/**
	 * Opens a new {@link DownloadChannel} positioned at start. For more
	 * details, see {@link #openChannel(DownloadListener, long)}
	 * <p>
	 * Note that {@link DownloadNotResumableException} is never thrown because
	 * this channel always starts at <code>0</code> offset.
	 * 
	 * @param listener
	 *            the listener to keep a track on the download progress
	 * @return the {@link DownloadChannel} instance
	 * @throws IOException
	 *             if any IO error occur
	 * @throws DownloadLinkNotFoundException
	 *             if the direct download link cannot be found (the file could
	 *             have been deleted)
	 * @throws DownloadLimitExceededException
	 *             if the download limit has been exceed, most times thrown when
	 *             downloading as a non-premium user
	 * @throws DownloadNotAuthorizedException
	 *             if the user (or guest) account does not have necessary rights
	 *             to download the file
	 * @throws UnsolvableCaptchaServiceException
	 *             if the service required captcha resolving but no
	 *             {@link CaptchaService} was available or the service did not
	 *             solve the challenge
	 * @throws NoCaptchaServiceException
	 *             if the service required an {@link CaptchaService}
	 *             implementation to be present, but none was available
	 */
	DownloadChannel openChannel(DownloadListener listener) throws IOException,
			DownloadLinkNotFoundException, DownloadLimitExceededException,
			DownloadNotAuthorizedException, UnsolvableCaptchaServiceException,
			NoCaptchaServiceException;

	/**
	 * Opens a new {@link DownloadChannel} with no listener and positioned at
	 * start. For more details, see {@link #openChannel(DownloadListener, long)}
	 * <p>
	 * Note that {@link DownloadNotResumableException} is never thrown because
	 * this channel always starts at <code>0</code> offset.
	 * 
	 * @return the {@link DownloadChannel} instance
	 * @throws IOException
	 *             if any IO error occur
	 * @throws DownloadLinkNotFoundException
	 *             if the direct download link cannot be found (the file could
	 *             have been deleted)
	 * @throws DownloadLimitExceededException
	 *             if the download limit has been exceed, most times thrown when
	 *             downloading as a non-premium user
	 * @throws DownloadNotAuthorizedException
	 *             if the user (or guest) account does not have necessary rights
	 *             to download the file
	 * @throws UnsolvableCaptchaServiceException
	 *             if the service required captcha resolving but no
	 *             {@link CaptchaService} was available or the service did not
	 *             solve the challenge
	 * @throws NoCaptchaServiceException
	 *             if the service required an {@link CaptchaService}
	 *             implementation to be present, but none was available
	 */
	DownloadChannel openChannel() throws IOException,
			DownloadLinkNotFoundException, DownloadLimitExceededException,
			DownloadNotAuthorizedException, UnsolvableCaptchaServiceException,
			NoCaptchaServiceException;

	/**
	 * Returns this {@link Downloader} configuration.
	 * <p>
	 * <b>IMPORTANT NOTE</b>: You should not modify any configuration within
	 * this configuration object once the download has started. Doing so, could
	 * result in a error.
	 * 
	 * @return this {@link Downloader} configuration
	 */
	C getConfiguration();

	/**
	 * This interface must be implemented in order to allow download
	 * configuration.
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 */
	public interface DownloaderConfiguration {
		/**
		 * Checks whether the configuration object can be casted to
		 * <code>type</code>
		 * 
		 * @param type
		 *            the casting type
		 * @return <code>true</code> if this object can be casted to
		 *         <code>type</code>
		 */
		boolean is(Class<? extends DownloaderConfiguration> type);

		/**
		 * Casts this object to <code>type</code>. If cannot be casted,
		 * <code>null</code> is returned.
		 * 
		 * @param type
		 *            the casting type
		 * @return the casted configuration
		 */
		<T extends DownloaderConfiguration> T as(Class<T> type);
	}
}
