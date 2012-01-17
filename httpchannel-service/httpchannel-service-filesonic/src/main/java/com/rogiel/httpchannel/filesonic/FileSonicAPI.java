/**
 * 
 */
package com.rogiel.httpchannel.filesonic;

import java.io.IOException;
import java.net.URI;

import javax.xml.bind.JAXB;

import com.rogiel.httpchannel.filesonic.xml.FSAPI;
import com.rogiel.httpchannel.filesonic.xml.FSGetUploadURL;
import com.rogiel.httpchannel.filesonic.xml.FSUpload;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class FileSonicAPI {
	private static final String BASE_URI = "http://api.filesonic.com/";

	private String email;
	private String password;

	public Object getInfo(int id) {
		return id;
	}

	public URI getUploadURI() throws IOException {
		return URI.create((((FSGetUploadURL) execute(FSUpload.class,
				"upload?method=getUploadUrl").getResponse()).getResponse()
				.getUploadURI()));
	}

	public long getMaxFilesize() throws IOException {
		return ((FSGetUploadURL) execute(FSUpload.class,
				"upload?method=getUploadUrl").getResponse()).getResponse()
				.getMaxFilesize();
	}

	public void login(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public void logout() {
		this.email = null;
		this.password = null;
	}

	private <T extends FSAPI> T execute(Class<T> type, String requestURI)
			throws IOException {
		final URI uri = URI.create(BASE_URI + requestURI + "&u=" + email
				+ "&p=" + password + "&format=xml");
		return JAXB.unmarshal(uri.toURL().openStream(), type);
	}
}
