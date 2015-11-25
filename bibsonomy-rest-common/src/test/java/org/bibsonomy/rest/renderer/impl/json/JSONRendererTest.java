/**
 * BibSonomy-Rest-Common - Common things for the REST-client and server.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.renderer.impl.json;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import net.sf.json.test.JSONAssert;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.data.NoDataAccessor;
import org.bibsonomy.rest.renderer.AbstractRenderer;
import org.bibsonomy.rest.renderer.AbstractRendererTest;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.bibsonomy.testutil.TestUtils;
import org.junit.Test;

/**
 * @author dzo
 */
public class JSONRendererTest extends AbstractRendererTest {
	private static final JSONRenderer RENDERER = new JSONRenderer(new UrlRenderer("http://www.bibsonomy.org/api/"));

	@Override
	public void compare(final String expected, final String actual) throws Exception {
		JSONAssert.assertJsonEquals(expected, actual);
	}
	
	@Override
	public String getPathToTestFiles() {
		return "jsonrenderer/";
	}

	@Override
	public String getFileExt() {
		return ".json";
	}

	@Override
	public AbstractRenderer getRenderer() {
		return RENDERER;
	}

	@Override
	protected String getQuotingTestString() {
		return "testen\"test\\";
	}
	
	/**
	 * @throws IOException
	 */
	@Test
	public void testParsePostFromFile() throws IOException {
		final String file = TestUtils.readEntryFromFile("jsonrenderer/ParsePost.json");
		final Post<? extends Resource> post = RENDERER.parsePost(new StringReader(file), NoDataAccessor.getInstance());
		assertEquals("Test", post.getDescription());
		assertEquals(2, post.getTags().size());
		final BibTex publication = (BibTex) post.getResource();
		assertEquals("Test JSON Post", publication.getTitle());
	}
	
	/**
	 * tests {@link EnumDeserializer}
	 * @throws IOException
	 */
	@Test
	public void testEnum() throws IOException {
		final String file = TestUtils.readEntryFromFile("jsonrenderer/StatusTest.json");
		String parseStat = RENDERER.parseStat(new StringReader(file));
		assertEquals("fail", parseStat);
	}
}
