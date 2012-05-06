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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.tags.FormTag;
import org.htmlparser.tags.FrameTag;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.InputTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.ScriptTag;
import org.htmlparser.tags.TextareaTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.rogiel.httpchannel.util.html.PageElement.TagMatcher;
import com.rogiel.httpchannel.util.html.filter.TypeTagFilter;
import com.rogiel.httpchannel.util.html.matcher.IDTagMatcher;
import com.rogiel.httpchannel.util.html.matcher.NameTagMatcher;

/**
 * This class handles all HTML parsing and searching. With this class is easy to
 * search for links matching an {@link Pattern}, for images, frames, forms,
 * inputs and maany more HTML widgets.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class Page {
	/**
	 * The list of nodes on the HTML DOM model
	 */
	private final NodeList nodes;

	/**
	 * This interface provides a mean to transform an list of objects into
	 * another type
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 * 
	 * @param <I>
	 *            the input object type
	 * @param <O>
	 *            the output object type
	 */
	private interface ListProcessor<I extends Tag, O> {
		O process(I tag);
	}

	/**
	 * An default {@link ListProcessor} that converts all tags to an
	 * {@link PageElement}
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 * 
	 * @param <I>
	 *            the input type
	 */
	private class DefaultListProcessor<I extends Tag> implements
			ListProcessor<I, PageElement<I>> {
		@Override
		public PageElement<I> process(I tag) {
			return new PageElement<I>(tag);
		}
	}

	/**
	 * Creates a new page instance
	 * 
	 * @param parser
	 *            the HTML parser
	 * @throws ParserException
	 *             an parsing exception
	 */
	public Page(Parser parser) throws ParserException {
		this.nodes = parser.parse(null);
	}

	/*
	 * ************************************************************************
	 * ***** INTERNAL
	 * ************************************************************************
	 */
	/**
	 * Filters all the tags within this page to those matching the filter
	 * 
	 * @param processor
	 *            the list processor
	 * @param filters
	 *            the filters to be applied
	 * @return an list of matching tags
	 */
	private <T extends Tag, O> List<O> filter(ListProcessor<T, O> processor,
			NodeFilter... filters) {
		final NodeFilter filter;
		if (filters.length == 1)
			filter = filters[0];
		else
			filter = new AndFilter(filters);
		try {
			return list(nodes.extractAllNodesThatMatch(filter, true), processor);
		} catch (ParserException e) {
			return Collections.emptyList();
		}
	}

	/**
	 * Creates a list of converted objects
	 * 
	 * @param list
	 *            the input list
	 * @param processor
	 *            the processor that converts the object types
	 * @return the processed and converted list
	 * @throws ParserException
	 *             if any exception occur
	 */
	@SuppressWarnings("unchecked")
	private <T extends Tag, O> List<O> list(final NodeList list,
			ListProcessor<T, O> processor) throws ParserException {
		final List<O> filtered = new ArrayList<>();
		final NodeIterator iterator = list.elements();
		while (iterator.hasMoreNodes()) {
			filtered.add(processor.process((T) iterator.nextNode()));
		}
		return filtered;
	}

	/**
	 * Tries to search for a tag value that matches exactly (the entire string)
	 * with the pattern.
	 * 
	 * @param list
	 *            the list of elements
	 * @param pattern
	 *            the pattern
	 * @param tagMatcher
	 *            the tag matcher (which will be matched against the pattern)
	 * @param realMatcher
	 *            the real matcher (which will be returned on the
	 *            {@link MatchedElement})
	 * @return an list of {@link MatchedElement}
	 */
	private <T extends Tag, E extends PageElement<T>> List<MatchedElement<T>> match(
			List<E> list, Pattern pattern, TagMatcher<T> tagMatcher,
			TagMatcher<T> realMatcher) {
		final List<MatchedElement<T>> matchList = new ArrayList<>();
		for (final E tag : list) {
			final MatchedElement<T> matched = tag.match(pattern, tagMatcher);
			if (matched == null)
				continue;
			if (matched.matches()) {
				if (tagMatcher == realMatcher) {
					matchList.add(matched);
				} else {
					matchList.add(tag.match(realMatcher));
				}
			}
		}
		return matchList;
	}

	/**
	 * Tries to search for a tag value that matches exactly (the entire string)
	 * with the pattern.
	 * 
	 * @param list
	 *            the list of elements
	 * @param pattern
	 *            the pattern
	 * @param tagMatcher
	 *            the tag matcher (which will be matched against the pattern and
	 *            used on {@link MatchedElement})
	 * @return an list of {@link MatchedElement}
	 */
	private <T extends Tag, E extends PageElement<T>> List<MatchedElement<T>> match(
			List<E> list, Pattern pattern, TagMatcher<T> tagMatcher) {
		return match(list, pattern, tagMatcher, tagMatcher);
	}

	/**
	 * Tries to search for a tag value that contains the content within the
	 * pattern.
	 * 
	 * @param list
	 *            the list of elements
	 * @param pattern
	 *            the pattern
	 * @param tagMatcher
	 *            the tag matcher (which will be matched against the pattern and
	 *            used on {@link MatchedElement})
	 * @return an list of {@link MatchedElement}
	 */

	private <T extends Tag, E extends PageElement<T>> List<MatchedElement<T>> find(
			List<E> list, Pattern pattern, TagMatcher<T> tagMatcher) {
		final List<MatchedElement<T>> matchList = new ArrayList<>();
		for (final E tag : list) {
			final MatchedElement<T> matched = tag.match(pattern, tagMatcher);
			if (matched.find())
				matchList.add(matched);
		}
		return matchList;
	}

	/**
	 * Returns a single element from the list
	 * 
	 * @param list
	 *            the list
	 * @return the first element at the list
	 */
	private <O> O single(List<O> list) {
		if (list.size() == 0)
			return null;
		return list.get(0);
	}

	/**
	 * Parses the HTML page to a plain string. This is similar to the
	 * "SEO preview" systems
	 * 
	 * @return
	 */
	public String asPlainString() {
		String string = nodes.asString().replaceAll("&nbsp;", "");
		final String[] lines = string.split("\n");

		final StringBuilder builder = new StringBuilder();
		for (final String line : lines) {
			String procLine = line.replaceAll("\t", " ").trim();
			if (procLine.length() == 0)
				continue;
			builder.append(line.replaceAll("\t", " ").trim()).append(" ");
		}

		return builder.toString();
	}

	/*
	 * ************************************************************************
	 * ***** TEXT SEARCH
	 * ************************************************************************
	 */
	/**
	 * Searches for the given pattern at the entire page
	 * 
	 * @param pattern
	 *            the pattern
	 * @return the search results
	 */
	public SearchResults search(Pattern pattern) {
		return new SearchResults(pattern, asPlainString());
	}

	/**
	 * Searches for the given text at the entire page
	 * 
	 * @param text
	 *            the text
	 * @return the search results
	 */
	public SearchResults searchFirst(String text) {
		return search(Pattern.compile(Pattern.quote(text)));
	}

	/*
	 * ************************************************************************
	 * ***** LINKS
	 * ************************************************************************
	 */
	/**
	 * An {@link TagMatcher} that returns the link href
	 */
	private static final TagMatcher<LinkTag> LINK_TAG_MATCHER = new TagMatcher<LinkTag>() {
		@Override
		public String content(LinkTag tag) {
			return tag.getLink();
		}
	};

	/**
	 * @return a list of all links contained at the page
	 */
	public List<PageElement<LinkTag>> links() {
		return filter(new DefaultListProcessor<LinkTag>(), new TypeTagFilter(
				LinkTag.class));
	}

	/**
	 * Return all links whose URL matches the given pattern
	 * 
	 * @param pattern
	 *            the pattern
	 * @return the list of links matching the pattern
	 */
	public List<MatchedElement<LinkTag>> links(Pattern pattern) {
		return match(links(), pattern, LINK_TAG_MATCHER);
	}

	/**
	 * Return the first link whose URL matches the given pattern
	 * 
	 * @param pattern
	 *            the pattern
	 * @return the first link matching the pattern
	 */
	public MatchedElement<LinkTag> link(Pattern pattern) {
		return single(links(pattern));
	}

	/**
	 * @param pattern
	 *            the pattern
	 * @return the links whose IDs matches the pattern
	 */
	public List<MatchedElement<LinkTag>> linksByID(Pattern pattern) {
		return match(links(), pattern, new IDTagMatcher<LinkTag>(),
				LINK_TAG_MATCHER);
	}

	/**
	 * @param id
	 *            the link ID
	 * @return the link with the given ID
	 */
	public MatchedElement<LinkTag> linkByID(String id) {
		return single(linksByID(Pattern.compile(Pattern.quote(id))));
	}

	/**
	 * @param pattern
	 *            the name pattern
	 * @return the links whose name matches the pattern
	 */
	public List<MatchedElement<LinkTag>> linksByName(Pattern pattern) {
		return match(links(), pattern, new NameTagMatcher<LinkTag>(),
				LINK_TAG_MATCHER);
	}

	/**
	 * @param name
	 *            the name
	 * @return the link with the given name
	 */
	public MatchedElement<LinkTag> linkByName(String name) {
		return single(linksByName(Pattern.compile(Pattern.quote(name))));
	}

	/*
	 * ************************************************************************
	 * ***** IMAGES
	 * ************************************************************************
	 */
	/**
	 * An {@link TagMatcher} that returns the image source url
	 */
	private static final TagMatcher<ImageTag> IMAGE_TAG_MATCHER = new TagMatcher<ImageTag>() {
		@Override
		public String content(ImageTag tag) {
			return tag.getImageURL();
		}
	};

	/**
	 * @return the list of all images at the page
	 */
	public List<PageElement<ImageTag>> images() {
		return filter(new DefaultListProcessor<ImageTag>(), new TypeTagFilter(
				ImageTag.class));
	}

	/**
	 * @param pattern
	 *            the image url pattern
	 * @return the list of images matching the url pattern
	 */
	public List<MatchedElement<ImageTag>> images(Pattern pattern) {
		return match(images(), pattern, IMAGE_TAG_MATCHER);
	}

	/**
	 * @param pattern
	 *            the image url pattern
	 * @return the first image whose url matches the pattern
	 */
	public MatchedElement<ImageTag> image(Pattern pattern) {
		return single(images(pattern));
	}

	/**
	 * @param pattern
	 *            the pattern id
	 * @return the list of images that match the given id
	 */
	public List<MatchedElement<ImageTag>> imagesByID(Pattern pattern) {
		return match(images(), pattern, new IDTagMatcher<ImageTag>(),
				IMAGE_TAG_MATCHER);
	}

	/**
	 * @param id
	 *            the image ID
	 * @return the image that matches with the given id
	 */
	public MatchedElement<ImageTag> imageByID(String id) {
		return single(imagesByID(Pattern.compile(Pattern.quote(id))));
	}

	/**
	 * @param pattern
	 *            the image name pattern
	 * @return the list of images whose names match the pattern
	 */
	public List<MatchedElement<ImageTag>> imagesByName(Pattern pattern) {
		return match(images(), pattern, new NameTagMatcher<ImageTag>(),
				IMAGE_TAG_MATCHER);
	}

	/**
	 * @param name
	 *            the image name
	 * @return the image whose name matches the given
	 */
	public MatchedElement<ImageTag> imageByName(String name) {
		return single(imagesByName(Pattern.compile(Pattern.quote(name))));
	}

	/*
	 * ************************************************************************
	 * ***** FORM
	 * ************************************************************************
	 */
	/**
	 * An {@link TagMatcher} that returns the form action (or submit) url
	 */
	private static final TagMatcher<FormTag> FORM_TAG_MATCHER = new TagMatcher<FormTag>() {
		@Override
		public String content(FormTag tag) {
			return tag.getFormLocation();
		}
	};

	/**
	 * @return the list of all forms on the page
	 */
	public List<PageElement<FormTag>> forms() {
		return filter(new DefaultListProcessor<FormTag>(), new TypeTagFilter(
				FormTag.class));
	}

	/**
	 * @param pattern
	 *            the action url pattern
	 * @return the forms whose urls matches the pattern
	 */
	public List<MatchedElement<FormTag>> forms(Pattern pattern) {
		return match(forms(), pattern, FORM_TAG_MATCHER);
	}

	/**
	 * @param pattern
	 *            the action url pattern
	 * @return the first form whose action url matches the pattern
	 */
	public MatchedElement<FormTag> form(Pattern pattern) {
		return single(forms(pattern));
	}

	/**
	 * @param pattern
	 *            the form id pattern
	 * @return the forms whose ids matches the pattern
	 */
	public List<MatchedElement<FormTag>> formsByID(Pattern pattern) {
		return match(forms(), pattern, new IDTagMatcher<FormTag>(),
				FORM_TAG_MATCHER);
	}

	/**
	 * @param id
	 *            the form id
	 * @return the form whose id matches the given
	 */
	public MatchedElement<FormTag> formByID(String id) {
		return single(formsByID(Pattern.compile(Pattern.quote(id))));
	}

	/**
	 * @param pattern
	 *            the form name pattern
	 * @return the forms whose names matches the pattern
	 */
	public List<MatchedElement<FormTag>> formsByName(Pattern pattern) {
		return match(forms(), pattern, new NameTagMatcher<FormTag>(),
				FORM_TAG_MATCHER);
	}

	/**
	 * @param name
	 *            the form name
	 * @return the form whose name matches the given
	 */
	public MatchedElement<FormTag> formByName(String name) {
		return single(formsByName(Pattern.compile(Pattern.quote(name))));
	}

	/*
	 * ************************************************************************
	 * ***** INPUT
	 * ************************************************************************
	 */
	/**
	 * An {@link TagMatcher} that returns the input value
	 */
	private static final TagMatcher<InputTag> INPUT_TAG_MATCHER = new TagMatcher<InputTag>() {
		@Override
		public String content(InputTag tag) {
			return tag.getAttribute("value");
		}
	};

	/**
	 * @return the list of all inputs on the page
	 */
	public List<PageElement<InputTag>> inputs() {
		return filter(new DefaultListProcessor<InputTag>(), new TypeTagFilter(
				InputTag.class));
	}

	/**
	 * @param pattern
	 *            the input value pattern
	 * @return the inputs whose values matches the pattern
	 */
	public List<MatchedElement<InputTag>> inputs(Pattern pattern) {
		return find(inputs(), pattern, INPUT_TAG_MATCHER);
	}

	/**
	 * @param pattern
	 *            the action url pattern
	 * @return the first input whose value matches the pattern
	 */
	public MatchedElement<InputTag> input(Pattern pattern) {
		return single(inputs(pattern));
	}

	/**
	 * @param pattern
	 *            the input id pattern
	 * @return the inputs whose ids matches the pattern
	 */
	public List<MatchedElement<InputTag>> inputsByID(Pattern pattern) {
		return match(inputs(), pattern, new IDTagMatcher<InputTag>(),
				INPUT_TAG_MATCHER);
	}

	/**
	 * @param name
	 *            the input id
	 * @return the input whose id matches the given
	 */
	public MatchedElement<InputTag> inputByID(String id) {
		return single(inputsByID(Pattern.compile(Pattern.quote(id))));
	}

	/**
	 * @param pattern
	 *            the input name pattern
	 * @return the inputs whose name matches the pattern
	 */
	public List<MatchedElement<InputTag>> inputsByName(Pattern pattern) {
		return match(inputs(), pattern, new NameTagMatcher<InputTag>(),
				INPUT_TAG_MATCHER);
	}

	/**
	 * @param name
	 *            the input name
	 * @return the input whose name matches the given
	 */
	public MatchedElement<InputTag> inputByName(String name) {
		return single(inputsByName(Pattern.compile(Pattern.quote(name))));
	}

	/*
	 * ************************************************************************
	 * ***** TEXTAREA
	 * ************************************************************************
	 */
	/**
	 * An {@link TagMatcher} that returns the textarea value
	 */
	private static final TagMatcher<TextareaTag> TEXTAREA_TAG_MATCHER = new TagMatcher<TextareaTag>() {
		@Override
		public String content(TextareaTag tag) {
			return tag.getStringText();
		}
	};

	/**
	 * @return the list of all textareas on the page
	 */
	public List<PageElement<TextareaTag>> textareas() {
		return filter(new DefaultListProcessor<TextareaTag>(),
				new TypeTagFilter(TextareaTag.class));
	}

	/**
	 * @param pattern
	 *            the textarea value pattern
	 * @return the textareas whose values matches the pattern
	 */
	public List<MatchedElement<TextareaTag>> textareas(Pattern pattern) {
		return match(textareas(), pattern, TEXTAREA_TAG_MATCHER);
	}

	/**
	 * @param pattern
	 *            the textarea value pattern
	 * @return the first textarea whose value matches the pattern
	 */
	public MatchedElement<TextareaTag> textarea(Pattern pattern) {
		return single(textareas(pattern));
	}

	/**
	 * @param pattern
	 *            the textarea id pattern
	 * @return the textareas whose ids matches the pattern
	 */
	public List<MatchedElement<TextareaTag>> textareasByID(Pattern pattern) {
		return match(textareas(), pattern, new IDTagMatcher<TextareaTag>(),
				TEXTAREA_TAG_MATCHER);
	}

	/**
	 * @param name
	 *            the textarea id
	 * @return the textarea whose id matches the given
	 */
	public MatchedElement<TextareaTag> textareaByID(String id) {
		return single(textareasByID(Pattern.compile(Pattern.quote(id))));
	}

	/**
	 * @param pattern
	 *            the textarea name pattern
	 * @return the textareas whose name matches the pattern
	 */
	public List<MatchedElement<TextareaTag>> textareasByName(Pattern pattern) {
		return match(textareas(), pattern, new NameTagMatcher<TextareaTag>(),
				TEXTAREA_TAG_MATCHER);
	}

	/**
	 * @param name
	 *            the textarea name
	 * @return the textarea whose name matches the given
	 */
	public MatchedElement<TextareaTag> textareaByName(String name) {
		return single(textareasByName(Pattern.compile(Pattern.quote(name))));
	}

	/*
	 * ************************************************************************
	 * ***** JAVASCRIPT
	 * ************************************************************************
	 */
	/**
	 * An {@link TagMatcher} that returns the script code
	 */
	public List<PageElement<ScriptTag>> scripts() {
		return filter(new DefaultListProcessor<ScriptTag>(), new TypeTagFilter(
				ScriptTag.class));
	}

	/**
	 * @return the list of all scripts on the page
	 */
	public List<MatchedElement<ScriptTag>> scripts(Pattern pattern) {
		return find(scripts(), pattern, new TagMatcher<ScriptTag>() {
			@Override
			public String content(ScriptTag tag) {
				return tag.getScriptCode();
			}
		});
	}

	/**
	 * @param pattern
	 *            the script code pattern
	 * @return the first script whose code matches the pattern
	 */
	public MatchedElement<ScriptTag> script(Pattern pattern) {
		return single(scripts(pattern));
	}

	/**
	 * @param pattern
	 *            the script url pattern
	 * @return the scripts whose urls matches the pattern
	 */
	public MatchedElement<ScriptTag> scriptBySource(Pattern pattern) {
		return single(match(scripts(), pattern, new TagMatcher<ScriptTag>() {
			@Override
			public String content(ScriptTag tag) {
				return tag.getAttribute("src");
			}
		}));
	}

	/*
	 * ************************************************************************
	 * ***** FRAME
	 * ************************************************************************
	 */
	/**
	 * An {@link TagMatcher} that returns the frame url
	 */
	private static final TagMatcher<FrameTag> FRAME_TAG_MATCHER = new TagMatcher<FrameTag>() {
		@Override
		public String content(FrameTag tag) {
			return tag.getFrameLocation();
		}
	};

	/**
	 * @return the list of all frames on the page
	 */
	public List<PageElement<FrameTag>> frames() {
		return filter(new DefaultListProcessor<FrameTag>(), new TypeTagFilter(
				FrameTag.class));
	}

	/**
	 * @param pattern
	 *            the frame url pattern
	 * @return the frames whose urls matches the pattern
	 */
	public List<MatchedElement<FrameTag>> frames(Pattern pattern) {
		return match(frames(), pattern, FRAME_TAG_MATCHER);
	}

	/**
	 * @param pattern
	 *            the frame url pattern
	 * @return the first frame whose url matches the pattern
	 */
	public MatchedElement<FrameTag> frame(Pattern pattern) {
		return single(frames(pattern));
	}

	/**
	 * @param pattern
	 *            the frame id pattern
	 * @return the frames whose id matches the pattern
	 */
	public List<MatchedElement<FrameTag>> framesByID(Pattern pattern) {
		return match(frames(), pattern, new IDTagMatcher<FrameTag>(),
				FRAME_TAG_MATCHER);
	}

	/**
	 * @param name
	 *            the frame id
	 * @return the frame whose id matches the given
	 */
	public MatchedElement<FrameTag> frameByID(String id) {
		return single(framesByID(Pattern.compile(Pattern.quote(id))));
	}

	/**
	 * @param pattern
	 *            the frame name pattern
	 * @return the frames whose name matches the pattern
	 */
	public List<MatchedElement<FrameTag>> framesByName(Pattern pattern) {
		return match(frames(), pattern, new NameTagMatcher<FrameTag>(),
				FRAME_TAG_MATCHER);
	}

	/**
	 * @param name
	 *            the frame name
	 * @return the frame whose name matches the given
	 */
	public MatchedElement<FrameTag> frameByName(String name) {
		return single(framesByName(Pattern.compile(Pattern.quote(name))));
	}

	/*
	 * ************************************************************************
	 * ***** INITIALIZERS
	 * ************************************************************************
	 */
	/**
	 * Creates a new page parsing the HTML input
	 * 
	 * @param html
	 *            the html code
	 * @return the newly created {@link Page} object
	 */
	public static Page parse(String html) {
		try {
			return new Page(Parser.createParser(html, null));
		} catch (ParserException e) {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return nodes.toHtml(false);
	}
}
