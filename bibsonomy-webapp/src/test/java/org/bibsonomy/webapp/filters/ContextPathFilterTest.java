package org.bibsonomy.webapp.filters;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 * @author rja
 * @version $Id$
 */
public class ContextPathFilterTest {

	@Test
	public void testStripContextPath() {
		final ContextPathFilter.ContextPathFreeRequest req = new ContextPathFilter.ContextPathFreeRequest(new MockHttpServletRequest());
		
		assertEquals("/login", stripContextPath(req, "/bibsonomy-webapp/login", "/bibsonomy-webapp"));
		assertEquals("/login", stripContextPath(req, "/login", ""));
		assertEquals("http://my.biblicious.org/login_openid?rememberMe=true", stripContextPath(req, "http://my.biblicious.org/bibsonomy2/login_openid?rememberMe=true", "/bibsonomy2"));
		assertEquals("http://my.biblicious.org/login_openid?rememberMe=true", stripContextPath(req, "http://my.biblicious.org/login_openid?rememberMe=true", ""));
	}
	
	@Test
	public void testStripContextPath2() {
	final ContextPathFilter.ContextPathFreeRequest req = new ContextPathFilter.ContextPathFreeRequest(new MockHttpServletRequest());
		
		assertEquals("/login", req.stripContextPath("/bibsonomy-webapp/login", "/bibsonomy-webapp"));
		assertEquals("/login", req.stripContextPath("/login", ""));
		assertEquals("http://my.biblicious.org/login_openid?rememberMe=true", req.stripContextPath("http://my.biblicious.org/bibsonomy2/login_openid?rememberMe=true", "/bibsonomy2"));
		assertEquals("http://my.biblicious.org/login_openid?rememberMe=true", req.stripContextPath("http://my.biblicious.org/login_openid?rememberMe=true", ""));
	}
	
	private String stripContextPath(final ContextPathFilter.ContextPathFreeRequest req, final String s, final String c) {
		return req.stripContextPath(new StringBuffer(s), c).toString();
	}
}
