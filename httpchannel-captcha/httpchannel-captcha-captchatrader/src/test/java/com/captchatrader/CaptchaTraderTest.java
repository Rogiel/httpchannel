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
package com.captchatrader;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import org.junit.Test;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class CaptchaTraderTest {
	/**
	 * Test method for
	 * {@link com.captchatrader.old.CaptchaTrader#respond(boolean)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRespond() throws Exception {
		final Properties p = new Properties();
		p.load(Files.newInputStream(
				Paths.get("../../httpchannel-captcha/src/test/resources/captchatrader.properties"),
				StandardOpenOption.READ));

		final CaptchaTrader api = new CaptchaTrader(
				"2acc44805ec208cc4d6b00c75a414996", p.getProperty("username"),
				p.getProperty("password"));
		final ResolvedCaptcha resolved = api
				.submit(new URI(
						"http://www.google.com/recaptcha/api/image?c=03AHJ_VusNSxAzZgs9OEvH79rOWOFDYXE2ElE5qkCr9kFU-ZU7gqy72tqEL3j_qCLYwdXgh4jaxU1iECISuUwt0zHbelni-lq8c7RVGSjUtJiMyHwlTTsG5CxWKIEus--yy3GPvwaW9l4N7hFnT57lLq272EOxcFDGYA"));
		System.out.println(resolved);
		resolved.valid();
	}

}
