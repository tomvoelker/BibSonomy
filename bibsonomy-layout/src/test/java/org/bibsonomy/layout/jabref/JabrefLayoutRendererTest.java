/**
 *
 *  BibSonomy-Layout - Layout engine for the webapp.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.layout.jabref;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.testutil.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 */
@RunWith(Parameterized.class)
public class JabrefLayoutRendererTest {
	private static final String LAYOUT_ENTRYTYPE_SPLIT = "#";
	
	@Parameters
	public static Collection<Object[]> data() {
		final File folder = new File(JabrefLayoutRendererTest.class.getResource("/jabref-layout-tests").getFile());
		final File[] listOfFiles = folder.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".layoutResult");
			}
		});
		
		final Collection<Object[]> files = new LinkedList<Object[]>();
		for (File file : listOfFiles) {
			files.add(new Object[] { file });
		}
		return files;
	}
	
	private static final JabrefLayoutRenderer RENDERER = new JabrefLayoutRenderer();
	static {
		RENDERER.setDefaultLayoutFilePath("org/bibsonomy/layout/jabref");
		RENDERER.setUrlGenerator(new URLGenerator("http://www.bibsonomy.org"));
	}
	
	private final File layoutTest;
	
	public JabrefLayoutRendererTest(File layoutTest) {
		this.layoutTest = layoutTest;
	}
	
    @Test
    public void testRender() throws Exception {
		String fileName = FilenameUtils.removeExtension(this.layoutTest.getName());
		String layoutName = fileName;
		String entryType = "article";
		
		if (fileName.contains(LAYOUT_ENTRYTYPE_SPLIT)) {
			String[] parts = fileName.split(LAYOUT_ENTRYTYPE_SPLIT);
	    	layoutName = parts[0];
	    	entryType = parts[1];
	    }
	    final JabrefLayout layout = RENDERER.getLayout(layoutName, "foo");
		final StringBuffer renderedLayout = RENDERER.renderLayout(layout, getPosts(entryType), false);
	    assertEquals("layout: " + layoutName + ", entrytype: " + entryType, TestUtils.readEntryFromFile(this.layoutTest).trim(), renderedLayout.toString().replaceAll("\\r", "").trim());
	}

    private List<Post<BibTex>> getPosts(String entryType) throws PersonListParserException {
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

