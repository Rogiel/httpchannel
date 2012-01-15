package com.rogiel.httpchannel.util.htmlparser;

import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;

public class ContainsFilter implements NodeFilter {
	private static final long serialVersionUID = 1L;
	private final Pattern content;

	public ContainsFilter(Pattern content) {
		this.content = content;
	}

	@Override
	public boolean accept(Node node) {
		return content.matcher(node.getText()).find();
	}
}
