package com.rogiel.httpchannel.util.htmlparser;

import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.nodes.TagNode;

public class FramePatternFilter implements NodeFilter {
	private static final long serialVersionUID = 1L;
	private final Pattern pattern;

	public FramePatternFilter(Pattern pattern) {
		this.pattern = pattern;
	}

	@Override
	public boolean accept(Node node) {
		if (!(node instanceof TagNode))
			return false;
		final TagNode frame = (TagNode) node;
		if (frame.getAttribute("src") == null)
			return false;
		return pattern.matcher(frame.getAttribute("src")).matches();
	}
}
