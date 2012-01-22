#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
package ${package};

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.rogiel.httpchannel.service.AbstractHttpService;
import com.rogiel.httpchannel.service.AbstractUploader;
import com.rogiel.httpchannel.service.CapabilityMatrix;
import com.rogiel.httpchannel.service.Service;
import com.rogiel.httpchannel.service.ServiceID;
import com.rogiel.httpchannel.service.ServiceMode;
import com.rogiel.httpchannel.service.UploadChannel;
import com.rogiel.httpchannel.service.UploadService;
import com.rogiel.httpchannel.service.Uploader;
import com.rogiel.httpchannel.service.UploaderCapability;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel;
import com.rogiel.httpchannel.service.channel.LinkedUploadChannel.LinkedUploadChannelCloseCallback;
import com.rogiel.httpchannel.service.config.NullUploaderConfiguration;
import com.rogiel.httpchannel.util.htmlparser.HTMLPage;

/**
 * This service handles uploads to ${serviceName}.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel</a>
 * @since 1.0
 */
public class ${serviceName}Service extends AbstractHttpService implements
		Service, UploadService<NullUploaderConfiguration> {
	/**
	 * This service ID
	 */
	public static final ServiceID SERVICE_ID = ServiceID.create("${serviceID}");

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
	
	protected class UploaderImpl extends
			AbstractUploader<NullUploaderConfiguration> implements
			Uploader<NullUploaderConfiguration>,
			LinkedUploadChannelCloseCallback {
		private Future<HTMLPage> uploadFuture;

		public UploaderImpl(String filename, long filesize,
				NullUploaderConfiguration configuration) {
			super(MyServiceService.this, filename, filesize, configuration);
		}

		@Override
		public UploadChannel openChannel() throws IOException {
			logger.debug("Starting upload to ${serviceName}");
			final HTMLPage page = get("http://www.example.com/").asPage();
			
			// locate upload uri
			String uri = null;
			
			logger.debug("Upload URI: {}", uri);

			// create a new channel
			final LinkedUploadChannel channel = createLinkedChannel(this);
			uploadFuture = multipartPost(uri).parameter("[file-parameter]", channel).asPageAsync();
			
			// wait for channel link
			return waitChannelLink(channel);
		}

		@Override
		public String finish() throws IOException {
			try {
				final HTMLPage page = uploadFuture.get();
				// find link
				return null;
			} catch (InterruptedException e) {
				return null;
			} catch (ExecutionException e) {
				throw (IOException) e.getCause();
			}
		}
	}
}
