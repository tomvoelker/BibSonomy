package org.bibsonomy.rest.enums;

import org.bibsonomy.rest.exceptions.InternServerException;

import junit.framework.TestCase;

public class HttpMethodTest extends TestCase {

	public void testGetHttpMethod() {
		assertEquals(HttpMethod.GET, HttpMethod.getHttpMethod("get"));
		assertEquals(HttpMethod.POST, HttpMethod.getHttpMethod("post"));
		assertEquals(HttpMethod.PUT, HttpMethod.getHttpMethod("put"));
		assertEquals(HttpMethod.DELETE, HttpMethod.getHttpMethod("delete"));

		assertEquals(HttpMethod.GET, HttpMethod.getHttpMethod("GET"));
		assertEquals(HttpMethod.POST, HttpMethod.getHttpMethod("pOSt"));
		assertEquals(HttpMethod.PUT, HttpMethod.getHttpMethod("pUt"));
		assertEquals(HttpMethod.DELETE, HttpMethod.getHttpMethod("dElEtE"));

		try {
			HttpMethod.getHttpMethod("hurz");
			fail("Should throw exception");
		} catch (final InternServerException ex) {
		}
	}

	/*
	 * We want to make sure that this is the case, because we are relying on it
	 * in our testcases.
	 */
	public void testToString() {
		assertEquals("GET", HttpMethod.GET.toString());
		assertEquals("POST", HttpMethod.POST.toString());
		assertEquals("PUT", HttpMethod.PUT.toString());
		assertEquals("DELETE", HttpMethod.DELETE.toString());
	}
}