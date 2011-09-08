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

import java.io.IOException;

import com.rogiel.httpchannel.service.DownloadListener.TimerWaitReason;
import com.rogiel.httpchannel.util.ThreadUtils;


/**
 * @author rogiel
 */
public abstract class AbstractDownloader implements Downloader {
	protected void timer(DownloadListener listener, long timer) {
		listener.timer(timer, TimerWaitReason.DOWNLOAD_TIMER);
		ThreadUtils.sleep(timer);
	}

	protected boolean cooldown(DownloadListener listener, long cooldown)
			throws IOException {
		if (listener.timer(cooldown, TimerWaitReason.COOLDOWN)) {
			ThreadUtils.sleep(cooldown);
			return true;
		} else {
			throw new IOException("Timer " + TimerWaitReason.COOLDOWN
					+ " aborted due to listener request");
		}
	}
}
