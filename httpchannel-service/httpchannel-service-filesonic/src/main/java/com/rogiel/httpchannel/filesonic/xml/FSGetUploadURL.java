/**
 * 
 */
package com.rogiel.httpchannel.filesonic.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
@XmlRootElement(name = "getUploadUrl")
@XmlAccessorType(XmlAccessType.NONE)
public class FSGetUploadURL extends FSResponse {
	@XmlElement(name = "response")
	private FSGetUploadURLResponse response;

	@XmlAccessorType(XmlAccessType.NONE)
	public static class FSGetUploadURLResponse {
		@XmlElement(name = "uri")
		private String uploadURI;
		@XmlElement(name = "max-filesize")
		private long maxFilesize;

		public String getUploadURI() {
			return uploadURI;
		}

		public long getMaxFilesize() {
			return maxFilesize;
		}
	}

	public FSGetUploadURLResponse getResponse() {
		return response;
	}
}
