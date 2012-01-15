package com.rogiel.httpchannel.util.htmlparser;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;

public class NameFilter implements NodeFilter {
	private static final long serialVersionUID = 1L;
	private final String name;

	public NameFilter(String name) {
		this.name = name;
	}

	@Override
	public boolean accept(Node node) {
		if (!(node instanceof Tag))
			return false;
		final Tag tag = (Tag) node;
		if (tag.getAttribute("name") == null)
			return false;
		if (!tag.getAttribute("name").equals(name))
			return false;
		return true;
	}
}
