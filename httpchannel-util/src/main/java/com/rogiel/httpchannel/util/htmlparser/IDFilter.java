package com.rogiel.httpchannel.util.htmlparser;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;

public class IDFilter implements NodeFilter {
	private static final long serialVersionUID = 1L;
	private final String id;

	public IDFilter(String id) {
		this.id = id;
	}

	@Override
	public boolean accept(Node node) {
		if (!(node instanceof Tag))
			return false;
		final Tag tag = (Tag) node;
		if (tag.getAttribute("id") == null)
			return false;
		if (!tag.getAttribute("id").equals(id))
			return false;
		return true;
	}
}
