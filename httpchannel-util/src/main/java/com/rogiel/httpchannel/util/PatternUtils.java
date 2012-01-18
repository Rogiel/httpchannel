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
package com.rogiel.httpchannel.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtils {
	public static String find(Pattern pattern, String text) {
		return find(pattern, text, 0);
	}

	public static int findInt(Pattern pattern, String text, int n) {
		String found = find(pattern, text, n);
		return (found != null ? Integer.parseInt(found) : 0);
	}

	public static String find(Pattern pattern, String text, int n) {
		final Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			return matcher.group(n);
		}
		return null;
	}

	public static String find(Pattern pattern, String text, int index, int n) {
		final Matcher matcher = pattern.matcher(text);
		int found = 0;
		while (matcher.find() && (++found) < index) {
		}
		return (found == 0 ? null : matcher.group(n));
	}

	public static String match(Pattern pattern, String text) {
		return match(pattern, text, 0);
	}

	public static String match(Pattern pattern, String text, int n) {
		final Matcher matcher = pattern.matcher(text);
		if (matcher.matches()) {
			return matcher.group(n);
		}
		return null;
	}
}
