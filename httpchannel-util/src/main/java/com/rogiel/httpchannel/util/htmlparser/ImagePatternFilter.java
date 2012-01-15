package com.rogiel.httpchannel.util.htmlparser;

import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.tags.ImageTag;

public class ImagePatternFilter implements NodeFilter {
	private static final long serialVersionUID = 1L;
	private final Pattern pattern;

	public ImagePatternFilter(Pattern pattern) {
		this.pattern = pattern;
	}

	@Override
	public boolean accept(Node node) {
		if (!(node instanceof ImageTag))
			return false;
		final ImageTag frame = (ImageTag) node;
		return pattern.matcher(frame.getImageURL()).matches();
	}
}
