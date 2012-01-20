/**
 * 
 */
package org.httpchannel.service.fourshared;

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
public class FourSharedServiceTest {
	private final FourSharedService service = new FourSharedService();
	private final Properties properties = new Properties();

	@Before
	public void setUp() throws IOException {
		properties.load(Files.newInputStream(Paths
				.get("../src/test/resources/login.properties")));
	}

	@Test
	public void test() throws IOException {
		AuthenticationServices.authenticator(service,
				properties.getProperty("4shared.username"),
				properties.getProperty("4shared.password")).login();

		final Path path = Paths
				.get("../src/test/resources/upload-test-file.txt");
		final URI uri = ChannelUtils.upload(service, path);

		Assert.assertNotNull(uri);
		System.out.println(uri);
	}
}
