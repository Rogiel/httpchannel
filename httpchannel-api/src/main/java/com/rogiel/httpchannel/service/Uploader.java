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
import com.rogiel.httpchannel.service.Uploader.UploaderConfiguration;
import com.rogiel.httpchannel.service.exception.NoCaptchaServiceException;

/**
 * This interfaces provides uploading for an service.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @since 1.0
 */
public interface Uploader<C extends UploaderConfiguration> {
	/**
	 * Opens a new {@link UploadChannel} that will be immediately ready to
	 * receive data to be sent to the upload stream.
	 * <p>
	 * Whether an channel is returned or any exception is thrown it is <b><span
	 * style="color:red">NOT</span></b> possible to reuse the same instance for
	 * more than one upload!
	 * <p>
	 * Please remember to close the channel before calling
	 * {@link UploadChannel#getDownloadLink()} or aborting the upload. The
	 * {@link UploadChannel#close()} method will finish upload (may take some
	 * time) and release any of the resources (such as network connections and
	 * file handlers) that could continue open for the whole runtime or until
	 * they time out, which could never occur. Note that you should close the
	 * channel even when an exception is thrown.
	 * 
	 * @return the {@link UploadChannel} instance
	 * @throws IOException
	 *             if any IO error occur
	 * @throws UnsolvableCaptchaServiceException
	 *             if the service required captcha resolving but no
	 *             {@link CaptchaService} was available or the service did not
	 *             solve the challenge
	 * @throws NoCaptchaServiceException
	 *             if the service required an {@link CaptchaService}
	 *             implementation to be present, but none was available
	 */
	UploadChannel openChannel() throws IOException,
			UnsolvableCaptchaServiceException, NoCaptchaServiceException;

	/**
	 * Returns this {@link Uploader} configuration.
	 * <p>
	 * <b>IMPORTANT NOTE</b>: You should not modify any configuration within
	 * this configuration object once the upload has started. Doing so, could
	 * result in a error.
	 * 
	 * @return this {@link Uploader} configuration
	 */
	C getConfiguration();

	/**
	 * This interface must be implemented in order to allow upload
	 * configuration.
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 */
	public interface UploaderConfiguration {
	}

	/**
	 * Defines an {@link UploaderConfiguration} that can allow <b>at least</b>
	 * an description field
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 */
	public interface DescriptionableUploaderConfiguration extends
			UploaderConfiguration {
		public static final String DEFAULT_DESCRIPTION = "Uploaded by httpchannel";

		/**
		 * @return the upload description
		 */
		String description();

		/**
		 * @param description
		 *            the upload description
		 */
		DescriptionableUploaderConfiguration description(String description);
	}
}
