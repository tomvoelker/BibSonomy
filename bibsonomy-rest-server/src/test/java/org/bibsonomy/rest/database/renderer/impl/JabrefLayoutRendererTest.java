package org.bibsonomy.rest.database.renderer.impl;

import static org.junit.Assert.assertNotNull;

import java.io.StringWriter;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.renderer.impl.JabrefLayoutRenderer;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class JabrefLayoutRendererTest {

	private JabrefLayoutRenderer renderer;

	@Before
	public void setUp() {
		renderer = new JabrefLayoutRenderer(new URLGenerator("http://www.bibsonomy.org/"));
	}

	@Test
	public void testSerializePost() throws Exception {
		final StringWriter writer = new StringWriter();
		final Post<BibTex> post = ModelUtils.generatePost(BibTex.class);
		final ViewModel model = new ViewModel();
		renderer.serializePost(writer, post, model);
		final String result = writer.getBuffer().toString();
		assertNotNull(result);
		System.out.println(result);
	}

}
