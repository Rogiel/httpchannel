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
package com.rogiel.httpchannel.service;

/**
 * This is an utility class to help manage Capabilities of all the services.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @param <T>
 *            the capability enumeration
 * @since 1.0
 */
public class CapabilityMatrix<T> {
	/**
	 * The list of all supported capabilities
	 */
	private final T[] matrix;

	/**
	 * Creates a new matrix of capabilities
	 * 
	 * @param matrix
	 *            all the capabilities this service support
	 */
	@SafeVarargs
	public CapabilityMatrix(T... matrix) {
		this.matrix = matrix;
	}

	/**
	 * Check whether an certatin capability is in the matrix or not.
	 * 
	 * @param capability
	 *            the capability being searched in the matrix
	 * @return true if existent, false otherwise
	 */
	public boolean has(T capability) {
		for (final T capScan : matrix) {
			if (capScan == capability) {
				return true;
			}
		}
		return false;
	}
}
