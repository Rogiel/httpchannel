package com.rogiel.httpchannel.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Assert;

public class ChecksumUtils {
	public static void assertChecksum(String message, String algorithm,
			byte[] data, byte[] expected) throws NoSuchAlgorithmException {
		final MessageDigest md = MessageDigest.getInstance(algorithm);
		final byte[] actual = md.digest(data);
		Assert.assertArrayEquals(message, expected, actual);
	}
}
