/**
 * 
 */
package com.rogiel.httpchannel.http;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

import com.rogiel.httpchannel.util.HttpClientUtils;
import com.rogiel.httpchannel.util.htmlparser.HTMLPage;

public abstract class Request {
	protected final HttpContext ctx;
	protected final String uri;

	public Request(HttpContext ctx, String uri) {
		this.ctx = ctx;
		this.uri = uri;
	}

	public abstract HttpResponse request() throws IOException;

	public Future<HttpResponse> requestAsync() throws IOException {
		return ctx.threadPool.submit(new Callable<HttpResponse>() {
			@Override
			public HttpResponse call() throws Exception {
				return request();
			}
		});
	}

	public String asString() throws ClientProtocolException, IOException {
		return HttpClientUtils.toString(request());
	}

	public Future<String> asStringAsync() throws IOException {
		return ctx.threadPool.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				return asString();
			}
		});
	}

	public HTMLPage asPage() throws ClientProtocolException, IOException {
		return HTMLPage.parse(asString());
	}

	public Future<HTMLPage> asPageAsync() throws IOException {
		return ctx.threadPool.submit(new Callable<HTMLPage>() {
			@Override
			public HTMLPage call() throws Exception {
				return asPage();
			}
		});
	}

	public String getURI() {
		return uri;
	}
}