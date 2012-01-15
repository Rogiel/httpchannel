package com.rogiel.httpchannel.util.htmlparser;

import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.tags.ScriptTag;

public class ScriptSrcFilter implements NodeFilter {
	private static final long serialVersionUID = 1L;
	private final Pattern pattern;

	public ScriptSrcFilter(Pattern pattern) {
		this.pattern = pattern;
	}

	@Override
	public boolean accept(Node node) {
		if (!(node instanceof ScriptTag))
			return false;
		final ScriptTag script = (ScriptTag) node;
		if (script.getAttribute("src") == null)
			return false;
		return pattern.matcher(script.getAttribute("src")).matches();
	}
}
