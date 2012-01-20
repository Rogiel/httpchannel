/**
 * 
 */
package org.httpchannel.service.fourshared;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import junit.framework.Assert;

import org.junit.Test;

import com.rogiel.httpchannel.service.helper.AuthenticationServices;
import com.rogiel.httpchannel.util.ChannelUtils;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class FourSharedServiceTest {
	private final FourSharedService service = new FourSharedService();

	@Test
	public void test() throws IOException {
		AuthenticationServices.authenticator(service, "", "").login();
		
		final Path path = Paths
				.get("../src/test/resources/upload-test-file.txt");
		final URI uri = ChannelUtils.upload(service, path);

		Assert.assertNotNull(uri);
		System.out.println(uri);
	}
}
