package com.rogiel.httpchannel.wirecopy;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;

import com.rogiel.httpchannel.copy.ChannelCopy;
import com.rogiel.httpchannel.service.impl.MegaUploadService;
import com.rogiel.httpchannel.service.impl.MultiUploadService;

public class ChannelCopyTest {
	@Test
	public void testWireCopy() throws IOException {
		final ChannelCopy copy = new ChannelCopy(Paths.get("pom.xml"));
		copy.addOutput(new MegaUploadService());
		copy.addOutput(new MultiUploadService());
		System.out.println(copy.call());
	}
}
