package org.bibsonomy.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.DOMReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Blackbox tests for the REST-API.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class WebServiceTest extends AbstractWebServiceTest {

	@Test
	@Ignore // FXME: test which depend on www.biblicous.org are suboptimal...
	public void aGetRequestWithoutAuthentication() throws HttpException, IOException {
		this.doc = this.getDocumentForWebServiceAction("posts?resourcetype=bibtex", HttpServletResponse.SC_UNAUTHORIZED, false);
	}

	@Ignore
	public void requestWithoutAction() throws IOException {
		this.doc = this.getDocumentForWebServiceAction("", HttpServletResponse.SC_FORBIDDEN, true);
		assertEquals(1, doc.selectObject("count(//error)"));
	}

	@Ignore
	public void getPosts() throws IOException {
		for (final String resourcetype : new String[] { "bibtex"/* TODO: , "bookmark" */}) {
			this.doc = this.getDocumentForWebServiceAction("posts?resourcetype=" + resourcetype, HttpServletResponse.SC_OK, true);
			// Check posts count
			final Node posts = this.doc.selectSingleNode("//posts");
			assertEquals(0, Integer.parseInt(posts.valueOf("@start")));
			assertEquals(20, Integer.parseInt(posts.valueOf("@end")));
			final Number numPosts = this.doc.numberValueOf("count(//post)");
			assertEquals(20, numPosts.intValue());
		}
	}

	// FIXME: db inconsistency @Test
	public void get100Posts() {
		this.doc = this.getDocumentForWebServiceAction("posts?resourcetype=bibtex&start=5&end=30", HttpServletResponse.SC_OK, true);
		// Check posts count
		final Node posts = this.doc.selectSingleNode("//posts");
		assertEquals(5, Integer.parseInt(posts.valueOf("@start")));
		final Number numPosts = this.doc.numberValueOf("count(//post)");
		assertEquals(25, numPosts.intValue());
		assertEquals(30, Integer.parseInt(posts.valueOf("@end")));
	}
	
	
}