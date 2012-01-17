/**
 * 
 */
package com.rogiel.httpchannel.filesonic.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class FSResponse {
	@XmlElement(name = "status")
	private String status;

	public String getStatus() {
		return status;
	}
}
