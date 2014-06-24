package org.bibsonomy.rest.database.renderer.impl;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.renderer.impl.JabrefMSOfficeXMLRenderer;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.Before;
import org.junit.Test;

public class JabrefMSOfficeXMLRendererTest {
	
	private static final String TESTRESULTFILEPATH = "JabrefMSOfficeXMLRendererTest.result";
	
	private JabrefMSOfficeXMLRenderer renderer;

	@Before
	public void setUp() {
		renderer = new JabrefMSOfficeXMLRenderer(new URLGenerator("http://www.bibsonomy.org/"));
	}

	@Test
	public void testSerializePost() throws Exception {
		final StringWriter writer = new StringWriter();
		Post<BibTex> post = ModelUtils.generatePost(BibTex.class);
		
		//Test umlauts/invalid xml chars/latex commands
		post.getResource().setNote("< >  & \" ' ä ö ü \\\"a{bla}");
		post.getResource().setTitle("< >  & \" ' ä ö ü \\\"a{bla}");
		final ViewModel model = new ViewModel();
		renderer.serializePost(writer, post, model);
		final String result = writer.getBuffer().toString();
		
		//Read Result
		byte[] encoded = Files.readAllBytes(Paths.get(JabrefMSOfficeXMLRendererTest.class.getClassLoader().getResource(TESTRESULTFILEPATH).toURI()));
		final String testResult = new String(encoded, StandardCharsets.UTF_8);
		
		assertEquals(result,testResult);
		System.out.println(result);
	}
	
}
