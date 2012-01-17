/**
 * 
 */
package com.rogiel.httpchannel.http;

import java.net.URI;
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

	public GetRequest get(String uri) {
		return new GetRequest(this, uri);
	}

	public GetRequest get(URI uri) {
		return get(uri.toString());
	}

	public PostRequest post(String uri) {
		return new PostRequest(this, uri);
	}

	public PostMultipartRequest multipartPost(String uri) {
		return new PostMultipartRequest(this, uri);
	}
}
