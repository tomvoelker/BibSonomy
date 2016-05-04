/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
 */
public class JabrefLayoutRendererTest {

	private JabrefLayoutRenderer renderer;

	@Before
	public void setUp() throws Exception {
		renderer = new JabrefLayoutRenderer(new URLGenerator("http://www.bibsonomy.org/"),JabrefLayoutRenderer.LAYOUT_SIMPLEHTML);
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
