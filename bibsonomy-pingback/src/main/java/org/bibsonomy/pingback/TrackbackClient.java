package org.bibsonomy.pingback;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.malethan.pingback.Link;
import com.malethan.pingback.PingbackClient;
import com.malethan.pingback.PingbackException;

/**
 * 
 * Sends pings according to the trackback protocol:
 * @see http://www.sixapart.com/pronet/docs/trackback_spec
 * 
 * 
 * @author rja
 * @version $Id$
 */
public class TrackbackClient implements PingbackClient {
	private static final Log log = LogFactory.getLog(TrackbackClient.class);

	/**
	 * The maximal number of characters to read from the body of a response.
	 */
	private static final int MAX_HTTP_BODY_CHARS = 500000;

	private static final Pattern XML_ERROR = Pattern.compile("<error>\\s*([01])\\s*</error>");
	private static final Pattern XML_ERROR_MESSAGE = Pattern.compile("<message>\\s*(.+?)\\s*</message>");

	private final HttpClient httpClient;

	public TrackbackClient() {
		this.httpClient = HttpClientHolder.getInstance().getHttpClient();
	}

	/**
	 * 
	 * 
	 * For example, a ping request might look like:
	 * <pre>
	 *   POST http://www.example.com/trackback/5
	 *   Content-Type: application/x-www-form-urlencoded; charset=utf-8
	 * 
	 *   title=Foo+Bar&url=http://www.bar.com/&excerpt=My+Excerpt&blog_name=Foo
	 * </pre>
	 * @see http://www.sixapart.com/pronet/docs/trackback_spec#Sending_a_TrackBack_Ping
	 * 
	 * @see com.malethan.pingback.PingbackClient#sendPingback(java.lang.String, com.malethan.pingback.Link)
	 */
	@Override
	public String sendPingback(final String articleUrl, final Link link) {
		if (link instanceof TrackbackLink) {
			final TrackbackLink trackbackLink = (TrackbackLink) link;
			final String trackbackUrl = trackbackLink.getPingbackUrl();
			try {
				final HttpPost httpPost = new HttpPost(trackbackUrl);
				/*
				 * set content type and payload
				 */

				final List<NameValuePair> formparams = new ArrayList<NameValuePair>();
				formparams.add(new BasicNameValuePair("url", articleUrl)); // required
				//				formparams.add(new BasicNameValuePair("title", "")); // optional
				//				formparams.add(new BasicNameValuePair("excerpt", "")); // optional
				//				formparams.add(new BasicNameValuePair("blog_name", "")); // optional
				final UrlEncodedFormEntity entity2 = new UrlEncodedFormEntity(formparams, "UTF-8");
				entity2.setContentType("application/x-www-form-urlencoded; charset=utf-8");
				httpPost.setEntity(entity2);

				try  {
					log.debug("sending trackback request to " + trackbackUrl);
					final HttpResponse response = this.httpClient.execute(httpPost);

					/*
					 * check response
					 */
					final HttpEntity entity = response.getEntity();
					if (present(entity)) {
						final Header contentType = entity.getContentType();
						if (present(contentType) && contentType.getValue().contains("application/x-www-form-urlencoded")) {
							final BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
							try {
								final String content = readContent(reader);

								final Matcher matcher = XML_ERROR.matcher(content);
								if (matcher.find()) {
									final String responseCode = matcher.group(1);
									if ("0".equals(responseCode)) {
										/*
										 * success!
										 */
										return "success";
									} else if ("1".equals(responseCode)) {
										/*
										 * error!
										 */
										final Matcher matcher2 = XML_ERROR_MESSAGE.matcher(content);
										final String message;
										if (matcher2.find()) {
											message = matcher2.group(1);
										} else {
											message = "the server did not return an error message";
										}
										throw new PingbackException(message, PingbackClient.UNKOWN_ERROR, trackbackUrl, trackbackLink.getUrl());
									}
								}
							} finally {
								reader.close();
							}
						}
					} else {
						throw new PingbackException("got no response from server", PingbackClient.TARGET_IS_NOT_PINGBACK_RESOURCE, trackbackUrl, trackbackLink.getUrl());
					}
				} finally {
					// ensure that the connection is released to the pool
					httpPost.abort();
				}
			} catch (final IOException e) {
				log.debug("got exception: ", e);
				throw new PingbackException("request error: " + e, PingbackClient.UPSTREAM_PROBLEM, trackbackUrl, trackbackLink.getUrl());
			}
			throw new PingbackException("unknown error", PingbackClient.UNKOWN_ERROR, trackbackUrl, trackbackLink.getUrl());
		} else {
			throw new IllegalArgumentException("Only instances of " + TrackbackLink.class.getSimpleName() + " are supported as 'link' argument.");
		}
	}

	protected String readContent(final BufferedReader reader) throws IOException {
		final StringBuilder content = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null && content.length() < MAX_HTTP_BODY_CHARS) {
			content.append(line);
		}
		return content.toString();
	}

}
