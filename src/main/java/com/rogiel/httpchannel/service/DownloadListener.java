/*
 * This file is part of seedbox <github.com/seedbox>.
 *
 * seedbox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * seedbox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with seedbox.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.rogiel.httpchannel.service;

import com.rogiel.httpchannel.service.captcha.Captcha;

/**
 * This listener keeps an track on the progress on an {@link Downloader}
 * service.
 * 
 * @author Rogiel
 * @since 1.0
 */
public interface DownloadListener {
	/**
	 * Inform that the downloader will be waiting for an certain amount of time
	 * due to an timer in the download site.
	 * 
	 * @param time
	 *            the time in ms in which the service will be be waiting.
	 * @param reason
	 *            the reason why this timer is running
	 * @return true if desires to wait, false otherwise
	 */
	boolean timer(long time, TimerWaitReason reason);

	/**
	 * The reason why an certain timer is being ran.
	 * 
	 * @author Rogiel
	 */
	public enum TimerWaitReason {
		/**
		 * Normal download timer. An annoyance.
		 */
		DOWNLOAD_TIMER,
		/**
		 * This IP has already download up to the limit, waiting for releasing
		 * of the block.
		 */
		COOLDOWN,
		/**
		 * This is an unknown wait time.
		 */
		UNKNOWN;
	}

	/**
	 * Passes an captcha by parameter and waits for the response of the
	 * challenge.
	 * 
	 * @param captcha
	 *            the captcha challenge
	 */
	String captcha(Captcha captcha);
}
