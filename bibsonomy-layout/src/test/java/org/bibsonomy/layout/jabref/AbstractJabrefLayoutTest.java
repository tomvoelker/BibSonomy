/**
 * BibSonomy-Layout - Layout engine for the webapp.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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

import static org.junit.Assert.assertEquals;

import org.apache.commons.io.FilenameUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.testutil.TestUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public abstract class AbstractJabrefLayoutTest {
	
	protected File layoutTest;
	protected String layoutName;
	protected String entryType;
	
	protected static Set<String> testedLayouts;
	
	private static final String LAYOUT_ENTRYTYPE_SPLIT = "#";
	private static String entryTypeSplitSuffix;
	
	protected static final JabrefLayoutRenderer RENDERER;
	static {
		try {
			final JabRefConfig config = new JabRefConfig();
			config.setDefaultLayoutFilePath("org/bibsonomy/layout/jabref");
			RENDERER = new JabrefLayoutRenderer(config);

			RENDERER.setUrlGenerator(new URLGenerator("http://www.bibsonomy.org"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public AbstractJabrefLayoutTest(File layoutTest, String layoutName) {
		this.layoutTest = layoutTest;
		this.layoutName = layoutName;
		this.entryType = this.extractEntryType();
	}
	
	protected static Collection<Object[]> initTests(Set<String> testedLayouts, String testCaseFolderPath, String entryTypeSplitSuffix) {
		AbstractJabrefLayoutTest.testedLayouts = testedLayouts;
		AbstractJabrefLayoutTest.entryTypeSplitSuffix = entryTypeSplitSuffix;
		final File testCaseFolder = new File(JabrefLayoutAntiScriptInjectionTest.class.getResource(testCaseFolderPath).getFile());
		final Collection<Object[]> layoutTests = new ArrayList<Object[]>();

		for (File file : testCaseFolder.listFiles(isResultLayoutOrDir)) {
			if (file != null) {
				if (file.isDirectory()) {
					String layoutName = file.getName();
					if (testedLayouts.contains(layoutName)) {
						for (File subDirFile : file.listFiles(isResultLayout)) {
							layoutTests.add(new Object[] {subDirFile, layoutName});
						}
					}
				}else {
					String[] parts = file.getName().split("#");
					String layoutName = parts[0];
					if (testedLayouts.contains(layoutName)) {
						layoutTests.add(new Object[] {file, layoutName});
					}
				}
			}
		}
		return layoutTests;
	}
	
	protected void testRender(List<Post<BibTex>> testCasePost) throws Exception{
		final AbstractJabRefLayout layout = RENDERER.getLayout(this.layoutName, "foo");
		String renderedLayout = RENDERER.renderLayout(layout, testCasePost, false).toString();
		String resultLayout = TestUtils.readEntryFromFile(this.layoutTest).trim();
		
		
		
		// format JUnit output
		final String printedEntryType = this.entryType.equals("") ? "NA" : this.entryType;
		
		// prepare Layouts - Remove varying lines etc.
		renderedLayout = prepareTest(renderedLayout, this.layoutName);
		resultLayout = prepareTest(resultLayout, this.layoutName);
		
		assertEquals("layout: " + this.layoutName + ", testfile: " + this.layoutTest + ", entrytype: " + printedEntryType, resultLayout, renderedLayout);
		
	}
	
	private static String prepareTest(String renderedLayout, final String layoutName) {
		// FIXME: use an EnumType for Layouts
		StringBuilder sb;
		int index;

		if (layoutName.equals("html")) {
			/*
			 * Deletes Lines containing a current timestamp ("Generated on ... TIME")
			 */
			sb = new StringBuilder(renderedLayout);
			final String titleAttr = "title=\"";
			index = sb.indexOf(titleAttr + "Generated on");
			if (index != -1) {
				index += titleAttr.length();
				int index2 = sb.indexOf("\"", index);
				sb.delete(index, index2);
				renderedLayout = sb.toString();
			}

			index = sb.indexOf(">Generated on");
			if (index != -1) {
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
			sb = new StringBuilder(renderedLayout);
			index = sb.indexOf("<small>Created by");
			int index2 = sb.lastIndexOf("</small>");
			sb.delete(index, index2);
			renderedLayout = sb.toString();
		}
		else if (layoutName.equals("din1505year") || layoutName.equals("simplehtmlyear") || layoutName.startsWith("harvardhtmlyear")) {
			/*
			 * Deletes randomly appearing bibsonomy quicknav_group
			 */
			sb = new StringBuilder(renderedLayout);
			index = sb.indexOf("<h3 class=\"bibsonomy_quicknav_group\">");
			if (index != -1) {
				final String find = "</a></h3>";
				int index2 = sb.indexOf(find, index);
				sb.delete(index, index2 + find.length());
				renderedLayout = sb.toString();
			}
		}
		else if (layoutName.startsWith("publist-year")) {
			/*
			 * Deletes randomly appearing bibsonomy quicknav_group with id
			 */
			sb = new StringBuilder(renderedLayout);
			index = sb.indexOf("<h3 id=\"");
			if (index != -1) {
				final String find = "</a></h3>";
				int index2 = sb.indexOf(find, index);
				sb.delete(index, index2 + find.length());
				renderedLayout = sb.toString();
			}
		}
		
		
		renderedLayout = renderedLayout.replaceAll("\\r", "").trim();
		return renderedLayout;
	}
	
	private String extractEntryType() {
		// remove extension
		final String fileName = FilenameUtils.removeExtension(this.layoutTest.getName());
		final String[] fileNameParts = fileName.split(LAYOUT_ENTRYTYPE_SPLIT + entryTypeSplitSuffix);
		if (fileNameParts.length > 1) {
			return fileNameParts[1];
		}
		return "";
	}
	
	protected static final FilenameFilter isResultLayout = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			if (dir != null) {
				return name.endsWith(".layoutResult");
			}
			return false;
		}
	};
	
	protected static final FilenameFilter isResultLayoutOrDir = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			if (dir != null) {
				return (dir.isDirectory() || name.endsWith(".layoutResult"));
			}
			return false;
		}
	}; 
	
}
