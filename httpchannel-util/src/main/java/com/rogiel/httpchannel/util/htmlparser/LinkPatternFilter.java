package com.rogiel.httpchannel.util.htmlparser;

import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.tags.LinkTag;

public class LinkPatternFilter implements NodeFilter {
	private static final long serialVersionUID = 1L;
	private final Pattern pattern;

	public LinkPatternFilter(Pattern pattern) {
		this.pattern = pattern;
	}

	@Override
	public boolean accept(Node node) {
		if (!(node instanceof LinkTag))
			return false;
		final LinkTag link = (LinkTag) node;
		return pattern.matcher(link.getLink()).matches();
	}
}
