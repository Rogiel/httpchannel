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
 * An {@link AccountDetails} instance can provide several information about an
 * authenticated account. This instance itself provides little information,
 * restricted to whether the account {@link #isActive() is active} and its
 * {@link #getUsername() username}. More details are provided by additional
 * interfaces:
 * <ul>
 * <li>{@link PremiumAccountDetails} - for services that provide premium
 * accounts</li>
 * <li>{@link DiskQuotaAccountDetails} - for services that have limited disk
 * quota</li>
 * <li>{@link BandwidthQuotaAccountDetails} - for services that have limited
 * bandwidth quota</li>
 * <li>{@link FilesizeLimitAccountDetails} - for services that have limited file
 * sizes depending on the account</li>
 * </ul>
 * You should not try to cast instances by yourself, instead they should be
 * safely casted as such:
 * 
 * <pre>
 * final {@link AccountDetails} details = ...;
 * if(details.{@link #is(Class) is}({@link PremiumAccountDetails}.class)) {
 * 	details.{@link #as(Class) as}({@link PremiumAccountDetails}.class).{@link PremiumAccountDetails#isPremium() isPremium()};
 * }
 * </pre>
 * 
 * A single {@link AccountDetails} can implement none, one extended or even more
 * than one extended details interfaces, however all instances are required to
 * implement at least the basic methods defined in this interface.
 * <p>
 * Services could implement their own methods for details, but this should be
 * avoided because it would make user code dependent on implementation meta data
 * which is not stable and could break compatibility with other implementation
 * versions. If possible, it is recommended to add a new interface into the API
 * module.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @since 1.0
 */
public interface AccountDetails {
	/**
	 * @return the account username
	 */
	String getUsername();

	/**
	 * @return <code>true</code> if the account is currently active
	 */
	boolean isActive();

	/**
	 * @return the service that provided this account
	 */
	AuthenticationService<?> getService();

	/**
	 * Checks whether the account object can be casted to <code>type</code>
	 * 
	 * @param type
	 *            the casting type
	 * @return <code>true</code> if this object can be casted to
	 *         <code>type</code>
	 */
	boolean is(Class<? extends AccountDetails> type);

	/**
	 * Casts this object to <code>type</code>. If cannot be casted,
	 * <code>null</code> is returned.
	 * 
	 * @param type
	 *            the casting type
	 * @return the casted account
	 */
	<T extends AccountDetails> T as(Class<T> type);

	/**
	 * Service accounts that has premium accounts must implement this interface
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 */
	public interface PremiumAccountDetails extends AccountDetails {
		/**
		 * @return <code>true</code> if the account is premium
		 */
		boolean isPremium();
	}

	/**
	 * Service accounts that has accounts with limited disk space should
	 * implement this interface
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 */
	public interface DiskQuotaAccountDetails extends AccountDetails {
		/**
		 * @return the currently free disk space. <code>-1</code> means no limit
		 */
		long getFreeDiskSpace();

		/**
		 * @return the currently used disk space. Cannot be negative.
		 */
		long getUsedDiskSpace();

		/**
		 * @return the maximum amount of disk space. <code>-1</code> means no
		 *         limit
		 */
		long getMaximumDiskSpace();
	}

	/**
	 * Service accounts that has accounts with limited bandwidth should
	 * implement this interface
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 */
	public interface BandwidthQuotaAccountDetails extends AccountDetails {
		/**
		 * @return the currently free bandwidth. <code>-1</code> means no limit
		 */
		long getFreeBandwidth();

		/**
		 * @return the currently used bandwidth. Cannot be negative.
		 */
		long getUsedBandwidth();

		/**
		 * @return the maximum amount of bandwidth available. <code>-1</code>
		 *         means no limit
		 */
		long getMaximumBandwidth();
	}

	/**
	 * Service accounts that has accounts with hotlink traffic should implement
	 * this interface
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 */
	public interface HotLinkingAccountDetails extends AccountDetails {
		/**
		 * @return the currently free hotlink traffic. <code>-1</code> means no
		 *         limit
		 */
		long getHotlinkTraffic();
	}

	/**
	 * Service accounts that has accounts with limited bandwidth should
	 * implement this interface
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 */
	public interface FilesizeLimitAccountDetails extends AccountDetails {
		/**
		 * @return the maximum filesize for the account. <code>-1</code> means
		 *         no limit
		 */
		long getMaximumFilesize();
	}

	/**
	 * Service accounts that has referring support
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 */
	public interface ReferralAccountDetails extends AccountDetails {
		/**
		 * @return the number of members referred
		 */
		int getMembersReferred();

		/**
		 * @return the account referral URL
		 */
		String getReferralURL();
	}

	/**
	 * Service account that has points attached to it (normally acquired through
	 * downloads of files from the account)
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 */
	public interface PointAccountDetails extends AccountDetails {
		/**
		 * @return the number of point on the account
		 */
		int getPoints();
	}
}
