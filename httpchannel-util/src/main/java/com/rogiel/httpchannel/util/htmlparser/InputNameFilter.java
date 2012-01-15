package com.rogiel.httpchannel.util.htmlparser;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.tags.InputTag;

public class InputNameFilter implements NodeFilter {
	private static final long serialVersionUID = 1L;
	private final String name;

	public InputNameFilter(String name) {
		this.name = name;
	}

	@Override
	public boolean accept(Node node) {
		if (!(node instanceof InputTag))
			return false;
		final InputTag input = (InputTag) node;
		if(input.getAttribute("name") == null)
			return false;
		if (!input.getAttribute("name").equals(name))
			return false;
		return true;
	}
}
