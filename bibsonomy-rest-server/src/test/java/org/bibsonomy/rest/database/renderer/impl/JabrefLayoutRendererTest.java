/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
		renderer = new JabrefLayoutRenderer(new URLGenerator("https://www.bibsonomy.org/"),JabrefLayoutRenderer.LAYOUT_SIMPLEHTML);
	}

	@Test
	public void testSerializePost() {
		final StringWriter writer = new StringWriter();
		final Post<BibTex> post = ModelUtils.generatePost(BibTex.class);
		final ViewModel model = new ViewModel();
		renderer.serializePost(writer, post, model);
		final String result = writer.getBuffer().toString();
		assertNotNull(result);
	}

}
