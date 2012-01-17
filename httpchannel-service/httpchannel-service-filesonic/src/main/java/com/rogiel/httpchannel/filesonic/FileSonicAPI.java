/**
 * 
 */
package com.rogiel.httpchannel.filesonic;

import java.io.IOException;
import java.net.URL;

import javax.xml.bind.JAXB;

import com.rogiel.httpchannel.filesonic.xml.FSAPI;
import com.rogiel.httpchannel.filesonic.xml.FSGetUploadURL;
import com.rogiel.httpchannel.filesonic.xml.FSUpload;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class FileSonicAPI {
	private static final String BASE_URL = "http://api.filesonic.com/";

	private String email;
	private String password;

	public Object getInfo(int id) {
		return id;
	}

	public URL getUploadURL() throws IOException {
		return new URL(((FSGetUploadURL) execute(FSUpload.class,
				"upload?method=getUploadUrl").getResponse()).getResponse()
				.getUploadURL());
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

	private <T extends FSAPI> T execute(Class<T> type, String requestURL)
			throws IOException {
		final URL url = new URL(BASE_URL + requestURL + "&u=" + email + "&p="
				+ password + "&format=xml");
		return JAXB.unmarshal(url.openStream(), type);
	}
}
