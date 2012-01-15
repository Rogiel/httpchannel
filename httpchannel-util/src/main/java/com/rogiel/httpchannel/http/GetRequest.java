/**
 * 
 */
package com.rogiel.httpchannel.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

public class GetRequest extends Request {
	private long position = 0;

	public GetRequest(HttpContext ctx, String url) {
		super(ctx, url);
	}

	@Override
	public HttpResponse request() throws IOException {
		final HttpGet get = new HttpGet(url);
		if (position > 0)
			get.addHeader("Range", "bytes=" + position + "-");
		return ctx.client.execute(get);
	}

	public GetRequest position(long position) {
		this.position = position;
		return this;
	}
}