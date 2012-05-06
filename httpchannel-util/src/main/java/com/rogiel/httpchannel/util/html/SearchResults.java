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
package com.rogiel.httpchannel.util.html;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an search done against an page string
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class SearchResults {
	/**
	 * The matcher
	 */
	private final Matcher matcher;

	/**
	 * Creates a new instance
	 * 
	 * @param matcher
	 *            the matcher
	 */
	public SearchResults(Matcher matcher) {
		this.matcher = matcher;
	}

	/**
	 * Creates a new instance
	 * 
	 * @param pattern
	 *            the pattern
	 * @param content
	 *            the content
	 */
	public SearchResults(Pattern pattern, String content) {
		this.matcher = pattern.matcher(content);
		this.matcher.find();
	}

	/**
	 * @return <code>true</code> if the matcher has found any results
	 */
	public boolean hasResults() {
		matcher.reset();
		return matcher.find();
	}

	/**
	 * @param n
	 *            the group number
	 * @return <code>true</code> if the group exists
	 */
	public boolean hasGroup(int n) {
		return n <= matcher.groupCount();
	}

	/**
	 * @return the entire matched value as a string
	 */
	public String asString() {
		return asString(0);
	}

	/**
	 * @return the group value as a string
	 */
	public String asString(int n) {
		return matcher.group(n);
	}

	/**
	 * @return the entire matched value as a integer
	 */
	public int asInteger() {
		return asInteger(0);
	}

	/**
	 * @return the group value as a integer
	 */
	public int asInteger(int n) {
		return Integer.parseInt(asString(n));
	}

	/**
	 * @return the entire matched value as a long
	 */
	public long asLong() {
		return asLong(0);
	}

	/**
	 * @return the group value as a long
	 */
	public long asLong(int n) {
		return Long.parseLong(asString(n));
	}

	/**
	 * @return the entire matched value as a double
	 */
	public double asDouble() {
		return asDouble(0);
	}

	/**
	 * @return the group value as a double
	 */
	public double asDouble(int n) {
		return Double.parseDouble(asString(n));
	}

	/**
	 * @return the pattern matched against the element
	 */
	public Pattern getPattern() {
		return matcher.pattern();
	}
}
