package com.rogiel.httpchannel.util.htmlparser;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.tags.InputTag;

public class InputIDFilter implements NodeFilter {
	private static final long serialVersionUID = 1L;
	private final String id;

	public InputIDFilter(String id) {
		this.id = id;
	}

	@Override
	public boolean accept(Node node) {
		if (!(node instanceof InputTag))
			return false;
		final InputTag input = (InputTag) node;
		if (input.getAttribute("id") == null)
			return false;
		if (!input.getAttribute("id").equals(id))
			return false;
		return true;
	}
}
