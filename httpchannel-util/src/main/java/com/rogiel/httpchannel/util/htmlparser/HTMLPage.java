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
package com.rogiel.httpchannel.util.htmlparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.tags.FormTag;
import org.htmlparser.tags.InputTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.ScriptTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class HTMLPage {
	private final Parser parser;

	private HTMLPage(Parser parser) {
		this.parser = parser;
	}

	public String getLink(final Pattern pattern) {
		NodeList nodes;
		try {
			nodes = parser.extractAllNodesThatMatch(new NodeFilter() {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean accept(Node node) {
					if (!(node instanceof LinkTag))
						return false;
					final LinkTag link = (LinkTag) node;
					return pattern.matcher(link.getLink()).matches();
				}
			});
		} catch (ParserException e) {
			return null;
		}
		if (nodes.size() >= 1)
			return ((LinkTag) nodes.elements().nextNode()).getLink();
		return null;
	}

	public String getFormAction(final Pattern pattern) {
		NodeList nodes;
		try {
			nodes = parser.extractAllNodesThatMatch(new NodeFilter() {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean accept(Node node) {
					if (!(node instanceof FormTag))
						return false;
					final FormTag form = (FormTag) node;
					return pattern.matcher(form.getFormLocation()).matches();
				}
			});
		} catch (ParserException e) {
			return null;
		}
		if (nodes.size() >= 1)
			return ((FormTag) nodes.elements().nextNode()).getFormLocation();
		return null;
	}

	public String getInputValue(final String inputName) {
		NodeList nodes;
		try {
			nodes = parser.extractAllNodesThatMatch(new NodeFilter() {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean accept(Node node) {
					if (!(node instanceof InputTag))
						return false;
					final InputTag input = (InputTag) node;
					if (!input.getAttribute("name").equals(inputName))
						return false;
					return true;
				}
			});
		} catch (ParserException e) {
			return null;
		}
		if (nodes.size() >= 1)
			return ((InputTag) nodes.elements().nextNode())
					.getAttribute("value");
		return null;
	}

	public int getIntegerInputValue(final String inputName) {
		return Integer.parseInt(getInputValue(inputName));
	}

	public String getInputValue(final Pattern pattern) {
		NodeList nodes;
		try {
			nodes = parser.extractAllNodesThatMatch(new NodeFilter() {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean accept(Node node) {
					if (!(node instanceof InputTag))
						return false;
					final InputTag input = (InputTag) node;
					if (input.getAttribute("value") == null)
						return false;
					if (!pattern.matcher(input.getAttribute("value")).matches())
						return false;
					return true;
				}
			});
		} catch (ParserException e) {
			return null;
		}
		if (nodes.size() >= 1)
			return ((InputTag) nodes.elements().nextNode())
					.getAttribute("value");
		return null;
	}

	public Tag getTagByID(final String id) {
		NodeList nodes;
		try {
			nodes = parser.extractAllNodesThatMatch(new NodeFilter() {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean accept(Node node) {
					if (!(node instanceof Tag))
						return false;
					if (((Tag) node).getAttribute("id") == null)
						return false;
					return ((Tag) node).getAttribute("id").equals(id);
				}
			});
		} catch (ParserException e) {
			return null;
		}
		if (nodes.size() >= 1)
			return ((Tag) nodes.elements().nextNode());
		return null;
	}

	public Tag getTagByName(final String name) {
		NodeList nodes;
		try {
			nodes = parser.extractAllNodesThatMatch(new NodeFilter() {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean accept(Node node) {
					if (!(node instanceof Tag))
						return false;
					return ((Tag) node).getAttribute("name").equals(name);
				}
			});
		} catch (ParserException e) {
			return null;
		}
		if (nodes.size() >= 1)
			return ((Tag) nodes.elements().nextNode());
		return null;
	}

	public boolean contains(final String text) {
		try {
			for (NodeIterator e = parser.elements(); e.hasMoreNodes();) {
				if (e.nextNode().toPlainTextString().contains(text))
					return true;
			}
		} catch (ParserException e) {
			return false;
		}
		return false;
	}

	public String findInScript(final Pattern pattern, int n) {
		NodeList nodes;
		try {
			nodes = parser.extractAllNodesThatMatch(new NodeFilter() {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean accept(Node node) {
					if (!(node instanceof ScriptTag))
						return false;
					final ScriptTag script = (ScriptTag) node;
					return pattern.matcher(script.getScriptCode()).find();
				}
			});
		} catch (ParserException e) {
			return null;
		}
		if (nodes.size() >= 1) {
			final ScriptTag script = (ScriptTag) nodes.elements().nextNode();
			final Matcher matcher = pattern.matcher(script.getScriptCode());
			if (matcher.find())
				return matcher.group(n);
		}
		return null;
	}

	public int findIntegerInScript(final Pattern pattern, int n) {
		String found = findInScript(pattern, n);
		if(found == null)
			return 0;
		return Integer.parseInt(findInScript(pattern, n));
	}

	public String toString() {
		final StringBuilder builder = new StringBuilder();
		try {
			for (NodeIterator i = parser.elements(); i.hasMoreNodes();) {
				builder.append(i.nextNode().toHtml(true));
			}
		} catch (ParserException e) {
			return null;
		}
		return builder.toString();
	}

	public static HTMLPage parse(String html) {
		return new HTMLPage(Parser.createParser(html, null));
	}
}
