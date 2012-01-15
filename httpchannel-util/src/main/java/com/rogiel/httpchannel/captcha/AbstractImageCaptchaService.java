/**
 * 
 */
package com.rogiel.httpchannel.captcha;

import java.io.IOException;
import java.net.URL;

import com.rogiel.httpchannel.http.GetRequest;
import com.rogiel.httpchannel.http.HttpContext;
import com.rogiel.httpchannel.http.PostMultipartRequest;
import com.rogiel.httpchannel.http.PostRequest;
import com.rogiel.httpchannel.util.htmlparser.HTMLPage;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public abstract class AbstractImageCaptchaService<C extends AbstractImageCaptcha>
		implements ImageCaptchaService<C> {
	protected final HttpContext http = new HttpContext();

	@Override
	public final C create(String html) {
		try {
			return create(HTMLPage.parse(html));
		} catch (IOException e) {
			return null;
		}
	}

	public abstract C create(HTMLPage page) throws IOException;

	@Override
	public boolean resolve(C captcha) {
		return false;
	}

	public GetRequest get(String url) {
		return http.get(url);
	}

	public GetRequest get(URL url) {
		return http.get(url);
	}

	public PostRequest post(String url) {
		return http.post(url);
	}

	public PostMultipartRequest multipartPost(String url) {
		return http.multipartPost(url);
	}
}
