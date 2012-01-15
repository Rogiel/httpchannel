/**
 * 
 */
package com.rogiel.httpchannel.http;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class HttpContext {
	protected final ExecutorService threadPool = Executors
			.newCachedThreadPool();
	/**
	 * The {@link HttpClient} instance for this service
	 */
	protected DefaultHttpClient client = new DefaultHttpClient();

	public GetRequest get(String url) {
		return new GetRequest(this, url);
	}

	public GetRequest get(URL url) {
		return get(url.toString());
	}

	public PostRequest post(String url) {
		return new PostRequest(this, url);
	}

	public PostMultipartRequest multipartPost(String url) {
		return new PostMultipartRequest(this, url);
	}
}
