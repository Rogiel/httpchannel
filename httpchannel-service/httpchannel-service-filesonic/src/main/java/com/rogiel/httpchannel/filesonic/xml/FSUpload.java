/**
 * 
 */
package com.rogiel.httpchannel.filesonic.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "FSApi_Upload")
public class FSUpload extends FSAPI {
	@XmlElements(value = { @XmlElement(name = "getUploadUrl", type = FSGetUploadURL.class) })
	private FSResponse response;

	public FSResponse getResponse() {
		return response;
	}
}
