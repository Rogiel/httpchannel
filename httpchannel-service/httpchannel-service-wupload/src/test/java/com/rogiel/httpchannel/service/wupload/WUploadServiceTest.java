/**
 * 
 */
package com.rogiel.httpchannel.service.wupload;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.rogiel.httpchannel.service.helper.AuthenticationServices;
import com.rogiel.httpchannel.util.ChannelUtils;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class WUploadServiceTest {
	private final WUploadService service = new WUploadService();
	private final Properties properties = new Properties();

	@Before
	public void setUp() throws IOException {
		properties.load(Files.newInputStream(Paths
				.get("../src/test/resources/login.properties")));
	}

	@Test
	public void testUpload() throws IOException {
		AuthenticationServices.authenticator(service,
				properties.getProperty("wupload.username"),
				properties.getProperty("wupload.password")).login();

		final Path path = Paths
				.get("../src/test/resources/upload-test-file.txt");
		final URI uri = ChannelUtils.upload(service, path);

		Assert.assertNotNull(uri);
		System.out.println(uri);
	}
}
