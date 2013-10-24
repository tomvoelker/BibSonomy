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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.testutil.TestUtils;
import org.bibsonomy.util.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

// TODO: add baseclass with methods and fields shared with JabRefLayoutRendererTest
@RunWith(Parameterized.class)
public class JabrefLayoutAntiScriptInjectionTest {
	/*		
	 * 		XMLEscaping Chars
	 * 		"   &quot;
	 * 		'   &apos;
	 * 		<   &lt;
	 * 		>   &gt;
	 * 		&   &amp;
	 */
	
	//Layouts that will be tested
	private static final Set<String> TESTEDLAYOUTS = Sets.asSet(new String[]{"apa_html","chicago","din1505","dblp","harvardhtml","simplehtml","simplehtmlyear",
																"tablerefs","tablerefsabsbib","tablerefsabsbibsort","html"});
	
	@Parameters
	public static Collection<Object[]> data() {
		final File testCaseFolder = new File(JabrefLayoutAntiScriptInjectionTest.class.getResource("/jabref-layout-anti-script-tests").getFile());
		final Collection<Object[]> layoutTests = new ArrayList<Object[]>();

		for (File file : testCaseFolder.listFiles(isResultLayoutOrDir)) {
			if (file != null) {
				if (file.isDirectory()) {
					String layoutName = file.getName();
					if (isTestedLayout(layoutName)) {
						for (File subDirFile : file.listFiles(isResultLayout)) {
							layoutTests.add(new Object[] {subDirFile, layoutName});
						}
					}
				}else {
					String[] parts = file.getName().split("#");
					String layoutName = parts[0];
					if (isTestedLayout(layoutName)) {
						layoutTests.add(new Object[] {file, layoutName});
					}
				}
			}
		}
		return layoutTests;
	}
	
	private File layoutTest;
	private String layoutName;
	private String entryType;
	
	private String renderedLayout;
	private String resultLayout;
	
	
	public JabrefLayoutAntiScriptInjectionTest(File layoutTest, String layoutName) {
		this.layoutTest = layoutTest;
		this.layoutName = layoutName;
		this.entryType = this.extractEntryType();
	}
	
	
	@Test
	public void xmlEscapingTest() throws Exception {
		//System.out.println(layoutName + "  ->  " + layoutTest.getName());
		final JabrefLayout layout = RENDERER.getLayout(this.layoutName, "foo");
		renderedLayout = RENDERER.renderLayout(layout, getTestCasePost(this.entryType), false).toString().replaceAll("\\r", "").trim();
		resultLayout = TestUtils.readEntryFromFile(layoutTest).trim();
		
		//Format JUnit output
		String printedEntryType = entryType;
		if (entryType.equals("")) {
			printedEntryType = "NA";
		}
		
		//Prepare Layouts - Remove varying lines etc.
		prepareTest();
		
		assertEquals("layout: " + layoutName + ", testfile: " + layoutTest + ", entrytype: " + printedEntryType, resultLayout, renderedLayout);
	}
	
	private void prepareTest() {
		//FIXME - use an EnumType for Layouts
		StringBuilder sb;
		int index;
		
		if (layoutName.equals("html")) {
			/*
			 * Deletes Lines containing a current timestamp ("Generated on ... TIME")
			 */
			sb = new StringBuilder(renderedLayout);
			final String titleAttr = "title=\"";
			index = sb.indexOf(titleAttr + "Generated on");
			if (index!=-1) {
				index += titleAttr.length();
				int index2 = sb.indexOf("\"", index);
				sb.delete(index, index2);
				renderedLayout = sb.toString();
			}
			
			index = sb.indexOf(">Generated on");
			if (index!=-1) {
				index++;
				int index2 = sb.indexOf("<", index);
				sb.delete(index, index2);
				renderedLayout = sb.toString();
			}	
		}
		else if (layoutName.equals("tablerefs") || layoutName.equals("tablerefsabsbib") || layoutName.equals("tablerefsabsbibsort")) {
			/*
			 * Deletes Lines containing a current timestamp ("Created by ... TIME")
			 */
			sb = new StringBuilder(resultLayout);
			index = sb.indexOf("<small>Created by");
			int index2 = sb.lastIndexOf("</small>");
			sb.delete(index, index2);
			resultLayout = sb.toString();
			
			sb = new StringBuilder(renderedLayout);
			index = sb.indexOf("<small>Created by");
			index2 = sb.lastIndexOf("</small>");
			sb.delete(index, index2);
			renderedLayout = sb.toString();
		}
	}
	
	private static boolean isTestedLayout(String layoutName) {
		return TESTEDLAYOUTS.contains(layoutName);
	}
	
	private String extractEntryType() {
		//Remove Extension
		String[] fileNameParts = layoutTest.getName().split(".layoutResult");
		String temp = fileNameParts[0];
		//Split at #xmlesc#
		fileNameParts = temp.split("#xmlesc#");
		String entryType = "";
		if (fileNameParts.length > 1) {
			entryType = fileNameParts[1];
		}
		return entryType;
	}
	
	private static final JabrefLayoutRenderer RENDERER = new JabrefLayoutRenderer();
	static {
		RENDERER.setDefaultLayoutFilePath("org/bibsonomy/layout/jabref");
		RENDERER.setUrlGenerator(new URLGenerator("http://www.bibsonomy.org"));
	}
	
	public static List<Post<BibTex>> getTestCasePost(String entryType) throws PersonListParserException{
		final User u = new User();
    	u.setName("TestUser TestUserUser");

		final BibTex publication = new BibTex(); 
		publication.setEntrytype(entryType);
		publication.setBibtexKey("<xxx>testuser2013test" + entryType + "</xxx>"); 
		publication.setAddress("<xxx>TestState</xxx>, <xxx>TestCountry</xxx>");
		publication.setAuthor(PersonNameUtils.discoverPersonNames("<xxx>TestUser TestUserUser</xxx> and <xxx>TestUser FriendOne</xxx>"));
		publication.setBooktitle("<xxx>BookTitleTest</xxx>");
		publication.setEditor(PersonNameUtils.discoverPersonNames("<xxx>TestEditorOne EditorOne</xxx> and <xxx>TestEditorTwo EditorTwo</xxx>"));
		publication.setPages("<xxx>323--324</xxx>");
		publication.setPublisher("<xxx>PublisherTest</xxx>");
		publication.setTitle("<xxx>alert \"ScriptTest\"</xxx><xxx>TitleTest</xxx>");
		publication.setUrl("<xxx>http://www.test.de</xxx>");
		publication.setYear("<xxx>20013</xxx>");
		publication.setMisc("isbn = {<xxx>978-1-60558-486-7</xxx>},\ndoi = {<xxx>10.1145/1557914.1557969</xxx>}");
		publication.setMonth("<xxx>jun</xxx>");
		publication.setPrivnote("<xxx>This is a test note</xxx>"); 
		publication.setAbstract("<xxx>AbstractTest</xxx>");
		final Post<BibTex> post = new Post<BibTex>();
		post.setResource(publication);
		post.setUser(u);
		post.setDescription("<xxx>DescriptionTest</xxx>");
		
		return Collections.singletonList(post);
	}
	
	
	
	private static final FilenameFilter isResultLayout = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			if (dir != null) {
				return name.endsWith(".layoutResult");
			}
			return false;
		}
	};
	
	private static final FilenameFilter isResultLayoutOrDir = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			if (dir != null) {
				return (dir.isDirectory() || name.endsWith(".layoutResult"));
			}
			return false;
		}
	};
}
