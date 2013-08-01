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
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.Collection;
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
		final File folder = new File("src/test/resources/jabref-layout-tests");
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
	
	private static JabrefLayoutRenderer getRenderer() {
		final JabrefLayoutRenderer renderer = new JabrefLayoutRenderer();
		renderer.setDefaultLayoutFilePath("org/bibsonomy/layout/jabref");
		renderer.setUrlGenerator(new URLGenerator("http://www.bibsonomy.org"));
		return renderer;
	}
	
	private final File layoutTest;
	
	public JabrefLayoutRendererTest(File layoutTest) {
		this.layoutTest = layoutTest;
	}
	
    @Test
    public void testRender() throws Exception {
	    final JabrefLayoutRenderer renderer = getRenderer();
	    String fileName = FilenameUtils.removeExtension(this.layoutTest.getName());
	    String layoutName = fileName;
	    String entryType = "article";
	    
		if (fileName.contains(LAYOUT_ENTRYTYPE_SPLIT)) {
	    	String[] parts = fileName.split(LAYOUT_ENTRYTYPE_SPLIT);
	    	layoutName = parts[0];
	    	entryType = parts[1];
	    }
	    final JabrefLayout layout = renderer.getLayout(layoutName, "foo");
	    final StringBuffer renderedLayout = renderer.renderLayout(layout, getPosts(entryType), false);
	    
	    assertEquals("layout: " + layoutName + ", entrytype: " + entryType, TestUtils.toString(new FileInputStream(this.layoutTest)).trim(), renderedLayout.toString().replaceAll("\\r", "").trim());
    }

    private List<Post<BibTex>> getPosts(String entryType) throws PersonListParserException {
    	final List<Post<BibTex>> posts = new LinkedList<Post<BibTex>>();

    	final User u = new User();
    	u.setName("Wiglaf Droste");

		/*
		 * next one
		 */
		final BibTex b2 = new BibTex(); 
		b2.setEntrytype(entryType);
		b2.setBibtexKey("benz2009managing"); 
		b2.setAddress("New York, NY, USA");
		b2.setAuthor(PersonNameUtils.discoverPersonNames("Dominik Benz and Folke Eisterlehner and Andreas Hotho and Robert Jäschke and Beate Krause and Gerd Stumme"));
		b2.setBooktitle("HT '09: Proceedings of the 20th ACM Conference on Hypertext and Hypermedia");
		b2.setEditor(PersonNameUtils.discoverPersonNames("Ciro Cattuto and Giancarlo Ruffo and Filippo Menczer"));
		b2.setPages("323--324");
		b2.setPublisher("ACM");
		b2.setTitle("Managing publications and bookmarks with BibSonomy");
		b2.setUrl("http://portal.acm.org/citation.cfm?doid=1557914.1557969#");
		b2.setYear("2009");
		b2.setMisc("isbn = {978-1-60558-486-7},\ndoi = {10.1145/1557914.1557969}");
		b2.setMonth("jun");
		b2.setPrivnote("This is a test note"); 
		b2.setAbstract("In this demo we present BibSonomy, a social bookmark and publication sharing system.");
	
		final Post<BibTex> p2 = new Post<BibTex>();
		p2.setResource(b2);
		p2.setUser(u);
		p2.setDescription("Our demo at HT 2009");
		
		posts.add(p2);
	
		return posts;
    }
}

