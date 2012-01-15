/*
 * This file is part of seedbox <github.com/seedbox>.
 *
 * seedbox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * seedbox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with seedbox.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.rogiel.httpchannel.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.rogiel.httpchannel.service.exception.AuthenticationInvalidCredentialException;
import com.rogiel.httpchannel.util.ChannelUtils;
import com.rogiel.httpchannel.util.ChecksumUtils;

public abstract class AbstractServiceTest {
	public static final Path TEST_UPLOAD_FILE = Paths
			.get("src/main/resources/upload-test-file.txt");
	public static final byte[] EXPECTED_FULL_CHECKSUM = new byte[] { 27, -93,
			-76, 6, 123, -31, -9, 1, -100, 103, 123, -108, -22, -3, 121, -54,
			-127, 27, 43, -8 };
	public static final byte[] EXPECTED_RESUME_CHECKSUM = new byte[] { 39, -29,
			-107, -76, -69, -122, -20, 78, -27, -60, 95, -23, 70, -127, -17,
			101, -39, -87, -2, -67 };

	private Service service;

	private URL downloadURL;

	private Credential validCredential;
	private Credential invalidCredential;

	@Before
	public void setUp() throws Exception {
		this.service = createService();
		downloadURL = createDownloadURL();
		validCredential = createValidCredential();
		invalidCredential = createInvalidCredential();
	}

	protected abstract Service createService();

	protected Credential createValidCredential() {
		return null;
	}

	protected Credential createInvalidCredential() {
		return null;
	}

	protected URL createDownloadURL() throws MalformedURLException {
		return null;
	}

	@Test
	public void testUnauthenticatedUpload() throws IOException {
		if (!(service instanceof UploadService))
			return;
		assertTrue(
				"This service does not have the capability UploadCapability.UNAUTHENTICATED_UPLOAD",
				((UploadService<?>) service).getUploadCapabilities().has(
						UploaderCapability.UNAUTHENTICATED_UPLOAD));

		final URL url = ChannelUtils.upload((UploadService<?>) service,
				TEST_UPLOAD_FILE);

		Assert.assertNotNull(url);
		System.out.println("Uploaded file to " + url);
	}

	@Test
	public void testAuthenticatedUpload() throws IOException {
		if (validCredential == null)
			return;
		if (!(service instanceof UploadService))
			return;
		if (!(service instanceof AuthenticationService))
			fail("The servide does not support authentication!");

		assertTrue(
				"This service does not have the capability UploadCapability.NON_PREMIUM_ACCOUNT_UPLOAD",
				((UploadService<?>) service).getUploadCapabilities().has(
						UploaderCapability.NON_PREMIUM_ACCOUNT_UPLOAD));

		((AuthenticationService<?>) service).getAuthenticator(validCredential)
				.login();

		final URL url = ChannelUtils.upload((UploadService<?>) service,
				TEST_UPLOAD_FILE);

		Assert.assertNotNull(url);
		System.out.println("Uploaded file to " + url);
	}

	@Test
	public void testUnauthenticatedDownload() throws IOException,
			NoSuchAlgorithmException {
		if (!(service instanceof DownloadService))
			return;
		assertTrue(
				"This service does not have the capability DownloaderCapability.UNAUTHENTICATED_DOWNLOAD",
				((DownloadService<?>) service).getDownloadCapabilities().has(
						DownloaderCapability.UNAUTHENTICATED_DOWNLOAD));

		final byte[] data = ChannelUtils
				.toByteArray(((DownloadService<?>) service).getDownloader(
						downloadURL).openChannel());
		ChecksumUtils.assertChecksum(
				"Downloaded data checksum did not matched", "SHA1", data,
				EXPECTED_FULL_CHECKSUM);
	}

	@Test
	public void testUnauthenticatedDownloadResume() throws IOException,
			NoSuchAlgorithmException {
		if (!(service instanceof DownloadService))
			return;
		if (!((DownloadService<?>) service).getDownloadCapabilities().has(
				DownloaderCapability.UNAUTHENTICATED_RESUME))
			return;

		assertTrue(
				"This service does not have the capability DownloaderCapability.UNAUTHENTICATED_DOWNLOAD",
				((DownloadService<?>) service).getDownloadCapabilities().has(
						DownloaderCapability.UNAUTHENTICATED_DOWNLOAD));

		final byte[] data = ChannelUtils
				.toByteArray(((DownloadService<?>) service).getDownloader(
						downloadURL).openChannel(50));
		ChecksumUtils.assertChecksum(
				"Downloaded data checksum did not matched", "SHA1", data,
				EXPECTED_RESUME_CHECKSUM);
	}

	@Test
	public void testAuthenticatedDownload() throws IOException,
			NoSuchAlgorithmException {
		if (validCredential == null)
			return;
		if (!(service instanceof DownloadService))
			return;
		if (!(service instanceof AuthenticationService))
			fail("The service does not support authentication!");

		assertTrue(
				"This service does not have the capability DownloaderCapability.NON_PREMIUM_ACCOUNT_DOWNLOAD",
				((DownloadService<?>) service).getDownloadCapabilities().has(
						DownloaderCapability.NON_PREMIUM_ACCOUNT_DOWNLOAD));

		((AuthenticationService<?>) service).getAuthenticator(validCredential)
				.login();

		final byte[] data = ChannelUtils
				.toByteArray(((DownloadService<?>) service).getDownloader(
						downloadURL).openChannel());
		ChecksumUtils.assertChecksum(
				"Downloaded data checksum did not matched", "SHA1", data,
				EXPECTED_FULL_CHECKSUM);
	}

	@Test
	public void testAuthenticatedDownloadResume() throws IOException,
			NoSuchAlgorithmException {
		if (validCredential == null)
			return;
		if (!(service instanceof DownloadService))
			return;
		if (!(service instanceof AuthenticationService))
			fail("The service does not support authentication!");
		if (!((DownloadService<?>) service).getDownloadCapabilities().has(
				DownloaderCapability.NON_PREMIUM_ACCOUNT_RESUME))
			return;

		assertTrue(
				"This service does not have the capability DownloaderCapability.NON_PREMIUM_ACCOUNT_DOWNLOAD",
				((DownloadService<?>) service).getDownloadCapabilities().has(
						DownloaderCapability.NON_PREMIUM_ACCOUNT_DOWNLOAD));

		((AuthenticationService<?>) service).getAuthenticator(validCredential)
				.login();

		final byte[] data = ChannelUtils
				.toByteArray(((DownloadService<?>) service).getDownloader(
						downloadURL).openChannel(50));
		ChecksumUtils.assertChecksum(
				"Downloaded data checksum did not matched", "SHA1", data,
				EXPECTED_RESUME_CHECKSUM);
	}

	@Test
	public void testAuthenticatorWithValidCredential() throws IOException {
		if (validCredential == null)
			return;
		if (!(service instanceof AuthenticationService))
			return;

		((AuthenticationService<?>) service).getAuthenticator(validCredential)
				.login();
	}

	@Test
	public void testAuthenticatorWithInvalidCredential() throws IOException {
		if (invalidCredential == null)
			return;
		if (!(service instanceof AuthenticationService))
			return;
		try {
			((AuthenticationService<?>) service).getAuthenticator(
					invalidCredential).login();
		} catch (AuthenticationInvalidCredentialException e) {
			return;
		}
		fail("This login attept should have failed!");
	}
}
