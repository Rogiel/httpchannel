/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.rogiel.httpchannel.util.html.filter;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;

/**
 * An filter that selects all tags matching an given type
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class TypeTagFilter implements NodeFilter {
	private static final long serialVersionUID = 1L;
	/**
	 * The tag type
	 */
	private final Class<? extends Tag> type;

	/**
	 * Creates a new instance
	 * 
	 * @param type
	 *            the tag type
	 */
	public TypeTagFilter(Class<? extends Tag> type) {
		this.type = type;
	}

	@Override
	public boolean accept(Node node) {
		return type.isAssignableFrom(node.getClass());
	}
}
