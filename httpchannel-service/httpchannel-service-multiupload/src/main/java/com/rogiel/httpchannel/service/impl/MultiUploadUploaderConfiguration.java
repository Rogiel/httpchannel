package com.rogiel.httpchannel.service.impl;

import java.util.EnumSet;

import com.rogiel.httpchannel.service.Uploader.DescriptionableUploaderConfiguration;
import com.rogiel.httpchannel.service.Uploader.UploaderConfiguration;
import com.rogiel.httpchannel.service.impl.MultiUploadService.MultiUploadUploader;

/**
 * Describes an configuration for an {@link MultiUploadUploader}
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class MultiUploadUploaderConfiguration implements UploaderConfiguration,
		DescriptionableUploaderConfiguration {
	/**
	 * The upload description
	 */
	private String description = DescriptionableUploaderConfiguration.DEFAULT_DESCRIPTION;
	/**
	 * The services in which Multiupload should mirror the uploaded file
	 */
	private EnumSet<MultiUploadMirrorService> uploadServices = EnumSet
			.allOf(MultiUploadMirrorService.class);

	/**
	 * An enumeration containing all supported services for Multiupload
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 */
	public enum MultiUploadMirrorService {
		MEGAUPLOAD(1), UPLOADKING(16), DEPOSIT_FILES(7), HOTFILE(9), UPLOAD_HERE(
				17), ZSHARE(6), FILE_SONIC(15), FILE_SERVE(18), WUPLOAD(19);

		/**
		 * The internal multiupload id
		 */
		public final int id;

		private MultiUploadMirrorService(int id) {
			this.id = id;
		}
	}

	@Override
	public String description() {
		return description;
	}

	@Override
	public MultiUploadUploaderConfiguration description(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Adds this service as an desired mirror
	 * 
	 * @param service
	 *            the service
	 */
	public MultiUploadUploaderConfiguration uploadService(
			MultiUploadMirrorService... services) {
		for (final MultiUploadMirrorService service : services) {
			uploadServices.add(service);
		}
		return this;
	}

	/**
	 * Checks if the service is on the desired mirror list
	 * 
	 * @param service
	 *            the service
	 * @return <code>true</code> if the service is on the list
	 */
	public boolean containsUploadService(MultiUploadMirrorService service) {
		return uploadServices.contains(service);
	}

	/**
	 * Removes this service from the mirror list
	 * 
	 * @param service
	 *            the service
	 */
	public MultiUploadUploaderConfiguration removeUploadService(
			MultiUploadMirrorService service) {
		uploadServices.remove(service);
		return this;
	}

	/**
	 * Removes all services from the mirror list
	 * 
	 * @return
	 */
	public MultiUploadUploaderConfiguration clearUploadServices() {
		uploadServices.clear();
		return this;
	}

	/**
	 * @return the list of services of which MultiUpload should try to make
	 *         mirrors
	 */
	public EnumSet<MultiUploadMirrorService> uploadServices() {
		return uploadServices;
	}
}
