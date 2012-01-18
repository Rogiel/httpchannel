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
