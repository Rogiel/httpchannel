package com.rogiel.httpchannel.util.htmlparser;

import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.tags.InputTag;

public class InputValuePatternFilter implements NodeFilter {
	private static final long serialVersionUID = 1L;
	private final Pattern pattern;

	public InputValuePatternFilter(Pattern pattern) {
		this.pattern = pattern;
	}

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
}
