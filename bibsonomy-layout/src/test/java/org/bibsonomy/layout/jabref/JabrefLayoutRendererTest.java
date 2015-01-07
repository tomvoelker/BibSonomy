/**
 * BibSonomy-Layout - Layout engine for the webapp.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.layout.jabref;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.util.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * 
 * @author:  rja
 */
@RunWith(Parameterized.class)
public class JabrefLayoutRendererTest extends AbstractJabrefLayoutTest{
	
	//Layouts that will be tested
	private static final Set<String> TESTEDLAYOUTS = 
		Sets.asSet(new String[]{"apa_html", "chicago", "din1505", "din1505year", "harvardhtml", "harvardhtmlyear",
								"harvardhtmlyear-linked", "harvardhtmlyear-linked-full",
								"publist-de","publist-en","publist-year-de","publist-year-en","publist-type-de","publist-type-en",
								"simplehtml", "simplehtmlyear", "tablerefs", "tablerefsabsbib", "tablerefsabsbibsort", "dblp", "html"});
	private static final String TESTCASEFOLDERPATH = "/jabref-layout-tests";
	private static final String ENTRYTYPE_SPLITSUFFIX = "";
	
	public JabrefLayoutRendererTest(File layoutTest, String layoutName) {
		super(layoutTest, layoutName);
	}
	
	@Parameters
	public static Collection<Object[]> data() {
		return initTests(TESTEDLAYOUTS, TESTCASEFOLDERPATH, ENTRYTYPE_SPLITSUFFIX);
	}
	
	@Override
	@Test
	public void testRender() throws Exception {
		testRender(getPosts(this.entryType));
	}
	
	public static List<Post<BibTex>> getPosts(String entryType) throws PersonListParserException {
		final User u = new User();
		u.setName("Wiglaf Droste");

		final BibTex publication = new BibTex(); 
		publication.setEntrytype(entryType);
		publication.setBibtexKey("benz2009managing"); 
		publication.setAddress("New York, NY, USA");
		publication.setAuthor(PersonNameUtils.discoverPersonNames("Dominik Benz and Folke Eisterlehner and Andreas Hotho and Robert Jäschke and Beate Krause and Gerd Stumme"));
		publication.setBooktitle("HT '09: Proceedings of the 20th ACM Conference on Hypertext and Hypermedia");
		publication.setEditor(PersonNameUtils.discoverPersonNames("Ciro Cattuto and Giancarlo Ruffo and Filippo Menczer"));
		publication.setPages("323--324");
		publication.setPublisher("ACM");
		publication.setTitle("Managing publications and bookmarks with BibSonomy");
		publication.setUrl("http://portal.acm.org/citation.cfm?doid=1557914.1557969#");
		publication.setYear("2009");
		publication.setMisc("isbn = {978-1-60558-486-7},\ndoi = {10.1145/1557914.1557969}");
		publication.setMonth("jun");
		publication.setPrivnote("This is a test note"); 
		publication.setAbstract("In this demo we present BibSonomy, a social bookmark and publication sharing system.");
		final Post<BibTex> post = new Post<BibTex>();
		post.setResource(publication);
		post.setUser(u);
		post.setDescription("Our demo at HT 2009");
		
		return Collections.singletonList(post);
	}
}
