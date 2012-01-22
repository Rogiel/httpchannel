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
package com.rogiel.httpchannel.util.htmlparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.FormTag;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.InputTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.ScriptTag;
import org.htmlparser.tags.TextareaTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class HTMLPage {
	private final NodeList nodes;

	private HTMLPage(Parser parser) throws ParserException {
		this.nodes = parser.parse(null);
	}

	private <T extends Node> List<T> filter(final Class<T> nodeType,
			NodeFilter... filters) {
		final NodeFilter filter;
		if (filters.length == 1)
			filter = filters[0];
		else
			filter = new AndFilter(filters);
		try {
			return list(nodes.extractAllNodesThatMatch(filter, true));
		} catch (ParserException e) {
			return Collections.emptyList();
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends Node> List<T> list(final NodeList list)
			throws ParserException {
		final List<T> filtered = new ArrayList<>();
		final NodeIterator iterator = list.elements();
		while (iterator.hasMoreNodes()) {
			filtered.add((T) iterator.nextNode());
		}
		return filtered;
	}

	public boolean contains(final Pattern pattern) {
		return !filter(Node.class, new ContainsFilter(pattern)).isEmpty();
	}

	public boolean contains(final String text) {
		return contains(Pattern.compile(Pattern.quote(text)));
	}

	public boolean containsIgnoreCase(final String text) {
		return !filter(
				Node.class,
				new ContainsInLowerCaseFilter(Pattern.compile(Pattern
						.quote(text.toLowerCase())))).isEmpty();
	}

	public String find(final Pattern pattern, int n) {
		for (final Node tag : filter(Tag.class, new ContainsFilter(pattern))) {
			final Matcher matcher = pattern.matcher(tag.getText());
			if (matcher.find())
				return matcher.group(n);
		}
		return null;
	}

	public int findAsInt(final Pattern pattern, int n) {
		String found = find(pattern, n);
		if (found == null)
			return 0;
		return Integer.parseInt(findScript(pattern, n));
	}

	/**
	 * Tries to find a link that has an URI following the given pattern
	 * 
	 * @param pattern
	 *            the pattern
	 * @return the link content, if found. <code>null</code> otherwise
	 */
	public String findLink(final Pattern pattern) {
		for (final LinkTag tag : filter(LinkTag.class, new LinkPatternFilter(
				pattern))) {
			return tag.getLink();
		}
		return null;
	}

	/**
	 * Tries to find a frame that has an URI following the given pattern
	 * 
	 * @param pattern
	 *            the pattern
	 * @return the iframe uri, if found. <code>null</code> otherwise
	 */
	public String findFrame(final Pattern pattern) {
		for (final TagNode tag : filter(TagNode.class, new FramePatternFilter(
				pattern))) {
			return tag.getAttribute("src");
		}
		return null;
	}

	/**
	 * Tries to find a image that has an URI following the given pattern
	 * 
	 * @param pattern
	 *            the pattern
	 * @return the iframe uri, if found. <code>null</code> otherwise
	 */
	public String findImage(final Pattern pattern) {
		for (final ImageTag tag : filter(ImageTag.class,
				new ImagePatternFilter(pattern))) {
			return tag.getImageURL();
		}
		return null;
	}

	/**
	 * Tries to find a form which has an location that respects the given
	 * pattern
	 * 
	 * @param pattern
	 *            the pattern
	 * @return the URI found, if any. <code>null</code> otherwise
	 */
	public String findFormAction(final Pattern pattern) {
		for (final FormTag tag : filter(FormTag.class,
				new FormActionPatternFilter(pattern))) {
			return tag.getFormLocation();
		}
		return null;
	}

	private String inputValue(List<InputTag> tags) {
		for (final InputTag tag : tags) {
			return tag.getAttribute("value");
		}
		return null;
	}

	public String getInputValue(final String inputName) {
		return inputValue(filter(InputTag.class, new InputNameFilter(inputName)));
	}

	public int getInputValueAsInt(final String inputName) {
		return Integer.parseInt(getInputValue(inputName));
	}

	public String getInputValueById(final String id) {
		return inputValue(filter(InputTag.class, new InputIDFilter(id)));
	}

	public int getInputValueByIdInt(final String id) {
		return Integer.parseInt(inputValue(filter(InputTag.class,
				new InputIDFilter(id))));
	}

	public String getInputValue(final Pattern pattern) {
		return inputValue(filter(InputTag.class, new InputValuePatternFilter(
				pattern)));
	}

	public String getTextareaValueById(String id) {
		return ((TextareaTag) getTagByID(id)).getStringText();
	}

	public Tag getTagByID(final String id) {
		for (final Tag tag : filter(Tag.class, new IDFilter(id))) {
			return tag;
		}
		return null;
	}

	public Tag getTagByName(final String name) {
		for (final Tag tag : filter(Tag.class, new NameFilter(name))) {
			return tag;
		}
		return null;
	}

	public String findScript(final Pattern pattern, int n) {
		for (final ScriptTag tag : filter(ScriptTag.class,
				new ScriptContainsFilter(pattern))) {
			final Matcher matcher = pattern.matcher(tag.getScriptCode());
			if (matcher.find())
				return matcher.group(n);
		}
		return null;
	}

	public String findScriptSrc(final Pattern pattern) {
		for (final ScriptTag tag : filter(ScriptTag.class, new ScriptSrcFilter(
				pattern))) {
			final Matcher matcher = pattern.matcher(tag.getAttribute("src"));
			if (matcher.matches())
				return matcher.group();
		}
		return null;
	}

	public int findScriptAsInt(final Pattern pattern, int n) {
		String found = findScript(pattern, n);
		if (found == null)
			return 0;
		return Integer.parseInt(found);
	}

	public String toString() {
		// try {
		// return parser.parse(null).toHtml(false);
		// } catch (ParserException e1) {
		// return null;
		// }
		return nodes.toHtml(false);
	}

	public static HTMLPage parse(String html) {
		try {
			return new HTMLPage(Parser.createParser(html, null));
		} catch (ParserException e) {
			return null;
		}
	}
}
