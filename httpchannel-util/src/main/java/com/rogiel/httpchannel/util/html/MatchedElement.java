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

import org.htmlparser.Tag;

/**
 * An {@link PageElement} that has an matched string attached to it
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class MatchedElement<T extends Tag> extends PageElement<T> {
	/**
	 * The regular expression {@link Matcher} that retains the matched strings
	 * to it
	 */
	private final Matcher matcher;

	/**
	 * @param tag
	 *            the tag
	 * @param matcher
	 *            the matcher
	 */
	public MatchedElement(T tag, Matcher matcher) {
		super(tag);
		this.matcher = matcher;
	}

	/**
	 * @param tag
	 *            the tag
	 * @param pattern
	 *            the pattern
	 * @param content
	 *            the content
	 */
	public MatchedElement(T tag, Pattern pattern, String content) {
		super(tag);
		this.matcher = pattern.matcher(content);
	}

	/**
	 * @param tag
	 *            the tag
	 * @param content
	 *            the content
	 */
	public MatchedElement(T tag, String content) {
		this(tag, Pattern.compile(Pattern.quote(content)), content);
		this.matcher.matches();
	}

	/**
	 * @return <code>true</code> if the element has an matched element
	 */
	public boolean matches() {
		matcher.reset();
		return matcher.matches();
	}

	/**
	 * @return <code>true</code> if the element has an matched element (the
	 *         entire value matches the pattern)
	 */
	public boolean matchesEntirelly() {
		return matcher.lookingAt();
	}

	/**
	 * @return <code>true</code> if the pattern has found something on the
	 *         element that matches it
	 */
	public boolean find() {
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

	@Override
	public String toString() {
		return "MatchedElement [tag=" + tag + ", pattern=" + getPattern() + "]";
	}
}
