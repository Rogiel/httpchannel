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
package com.rogiel.httpchannel.util;

import java.io.IOException;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class ExceptionUtils {
	public static void asIOException(Exception e) throws IOException {
		final Throwable e2 = e.getCause();
		if (e2 == null) {
			throw new IOException(e);
		} else if (e instanceof IOException) {
			throw (IOException) e;
		} else if (e2 instanceof IOException) {
			throw (IOException) e2;
		} else if (e2 instanceof RuntimeException) {
			throw (RuntimeException) e2;
		} else {
			throw new IOException(e);
		}
	}
}
