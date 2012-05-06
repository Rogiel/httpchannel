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

import java.util.regex.Pattern;

import org.htmlparser.Tag;

/**
 * An element that represents an tag on the page
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class PageElement<T extends Tag> {
	/**
	 * The tag represented by this element
	 */
	protected final T tag;

	/**
	 * Creates a new instance
	 * 
	 * @param tag
	 *            the tag
	 */
	public PageElement(T tag) {
		this.tag = tag;
	}

	/**
	 * Tries to match the element with a given pattern
	 * 
	 * @param pattern
	 *            the pattern
	 * @return the matched element
	 */
	public MatchedElement<T> match(Pattern pattern) {
		return match(pattern, null);
	}

	/**
	 * Tries to match the element with a given pattern using an alternative
	 * {@link TagMatcher}
	 * 
	 * @param pattern
	 *            the pattern
	 * @param tagMatcher
	 *            the tag matcher
	 * @return the matched element
	 */
	public MatchedElement<T> match(Pattern pattern, TagMatcher<T> tagMatcher) {
		if (tagMatcher == null) {
			tagMatcher = new TagMatcher<T>() {
				@Override
				public String content(T tag) {
					return tag.toHtml();
				}
			};
		}
		final String content = tagMatcher.content(tag);
		if (content == null)
			return null;
		return new MatchedElement<T>(tag, pattern, tagMatcher.content(tag));
	}

	/**
	 * Tries to match the element with itself (return a {@link MatchedElement}
	 * that always matched it self)
	 * 
	 * @param tagMatcher
	 *            the tag matcher
	 * @return always an {@link MatchedElement} whose group 0 matches it self
	 */
	public MatchedElement<T> match(TagMatcher<T> tagMatcher) {
		if (tagMatcher == null) {
			tagMatcher = new TagMatcher<T>() {
				@Override
				public String content(T tag) {
					return tag.toHtml();
				}
			};
		}
		final String content = tagMatcher.content(tag);
		if (content == null)
			return null;
		return new MatchedElement<T>(tag, tagMatcher.content(tag));
	}

	/**
	 * An tag matcher is an helper class that can return an value that the
	 * matcher should use to test the pattern against it.
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 * 
	 * @param <T>
	 *            the tag type
	 */
	public interface TagMatcher<T extends Tag> {
		String content(T tag);
	}

	/**
	 * @return the tag object
	 */
	public T tag() {
		return tag;
	}

	@Override
	public String toString() {
		return "PageElement [tag=" + tag + "]";
	}
}
