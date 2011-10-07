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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.rogiel.httpchannel.service.captcha.CaptchaResolver;
import com.rogiel.httpchannel.service.config.ServiceConfiguration;
import com.rogiel.httpchannel.util.AlwaysRedirectStrategy;
import com.rogiel.httpchannel.util.HttpClientUtils;
import com.rogiel.httpchannel.util.htmlparser.HTMLPage;

/**
 * Abstract base service for HTTP enabled services.
 * 
 * @author Rogiel
 * @since 1.0
 */
public abstract class AbstractHttpService<T extends ServiceConfiguration>
		extends AbstractService<T> implements Service {
	private static final ExecutorService threadPool = Executors
			.newCachedThreadPool();

	/**
	 * The {@link HttpClient} instance for this service
	 */
	protected DefaultHttpClient client = new DefaultHttpClient();

	/**
	 * The captcha resolver
	 */
	protected CaptchaResolver captchaResolver;

	protected AbstractHttpService(T configuration) {
		super(configuration);
		client.setRedirectStrategy(new AlwaysRedirectStrategy());
		// client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS,
		// true);
		// client.getParams().setIntParameter(ClientPNames.MAX_REDIRECTS, 10);
		// client.setRedirectStrategy(new DefaultRedirectStrategy());
	}

	protected HttpResponse get(String url) throws ClientProtocolException,
			IOException {
		final HttpGet request = new HttpGet(url);
		return client.execute(request);
	}

	protected HttpResponse get(String url, long rangeStart)
			throws ClientProtocolException, IOException {
		final HttpGet request = new HttpGet(url);
		if (rangeStart >= 0)
			request.addHeader("Range", "bytes=" + rangeStart + "-");
		return client.execute(request);
	}

	protected String getAsString(String url) throws ClientProtocolException,
			IOException {
		return HttpClientUtils.toString(get(url));
	}

	protected HTMLPage getAsPage(String url) throws ClientProtocolException,
			IOException {
		return HTMLPage.parse(getAsString(url));
	}

	public Future<HttpResponse> getAsync(final String url) throws IOException {
		return threadPool.submit(new Callable<HttpResponse>() {
			@Override
			public HttpResponse call() throws Exception {
				return get(url);
			}
		});
	}

	public Future<String> getAsStringAsync(final String url) throws IOException {
		return threadPool.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				return getAsString(url);
			}
		});
	}

	public Future<HTMLPage> getAsPageAsync(final String url) throws IOException {
		return threadPool.submit(new Callable<HTMLPage>() {
			@Override
			public HTMLPage call() throws Exception {
				return getAsPage(url);
			}
		});
	}

	protected HttpResponse post(String url, HttpEntity entity)
			throws ClientProtocolException, IOException {
		final HttpPost request = new HttpPost(url);
		request.setEntity(entity);
		return client.execute(request);
	}

	protected String postAsString(String url, HttpEntity entity)
			throws ClientProtocolException, IOException {
		return HttpClientUtils.toString(post(url, entity));
	}

	protected HTMLPage postAsPage(String url, HttpEntity entity)
			throws ClientProtocolException, IOException {
		return HTMLPage.parse(postAsString(url, entity));
	}

	protected Future<HttpResponse> postAsync(final String url,
			final HttpEntity entity) throws IOException {
		return threadPool.submit(new Callable<HttpResponse>() {
			@Override
			public HttpResponse call() throws Exception {
				return post(url, entity);
			}
		});
	}

	protected Future<String> postAsStringAsync(final String url,
			final HttpEntity entity) throws IOException {
		return threadPool.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				return postAsString(url, entity);
			}
		});
	}

	protected Future<HTMLPage> postAsPageAsync(final String url,
			final HttpEntity entity) throws IOException {
		return threadPool.submit(new Callable<HTMLPage>() {
			@Override
			public HTMLPage call() throws Exception {
				return postAsPage(url, entity);
			}
		});
	}
}
