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
