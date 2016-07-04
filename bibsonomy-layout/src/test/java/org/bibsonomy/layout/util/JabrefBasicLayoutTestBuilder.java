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
package org.bibsonomy.layout.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.layout.jabref.AbstractJabRefLayout;
import org.bibsonomy.layout.jabref.JabRefConfig;
import org.bibsonomy.layout.jabref.JabrefLayoutRenderer;
import org.bibsonomy.layout.jabref.JabrefLayoutRendererTest;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.services.URLGenerator;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * TODO: add documentation to this class
 *
 * @author mme
 */
@Ignore
public class JabrefBasicLayoutTestBuilder {
	
	private static final JabrefLayoutRenderer RENDERER;
	static {
		final JabRefConfig config = new JabRefConfig();
		config.setDefaultLayoutFilePath("src/main/resources/org/bibsonomy/layout/jabref");
		try {
			RENDERER = new JabrefLayoutRenderer(config);
			RENDERER.setUrlGenerator(new URLGenerator("http://www.bibsonomy.org"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * Specify the Layout and entryType, for which you want to create a TestCase/result-File
	 */
	private String layoutName = "boxed";
	private String[] entryTypes = {"book"};
		//{"article","book","booklet","conference","electronic","inbook","incollection","inproceedings","","manual","mastersthesis","phdthesis","proceedings","techreport","unpublished"};
	
	private String outputFolderPath = "src/test/resources/temp/";
	
	private static final String LAYOUT_ENTRYTYPE_SPLIT = "#";
	private static final String LAYOUT_ENTRYTYPE_SPLITSUFFIX = ""; //"xmlesc#";
	private static final String LAYOUT_FILEEXTENSION = ".html"; //".layoutResult";
	
	
	/*
	 * 
	 */
	private String modifyLayout(String renderedLayout) {
		
		//renderedLayout = renderedLayout.replaceAll("<xxx>", "&ltxxx&gt");
		//renderedLayout = renderedLayout.replaceAll("</xxx>", "&lt/xxx&gt");
		
		return renderedLayout;
	}
	
	@Test
	public void createBasicResultLayout() throws LayoutRenderingException, IOException, PersonListParserException {
		AbstractJabRefLayout layout = RENDERER.getLayout(layoutName, "foo");
		for (String entryType : entryTypes) {
			/*
			 * Here you can modify the post, from which the layoutTest should be build
			 */
			List<Post<BibTex>> testCasePost = JabrefLayoutRendererTest.getPosts(entryType);//JabrefLayoutAntiScriptInjectionTest.getTestCasePost(entryType);
			
			StringBuffer sb = RENDERER.renderLayout(layout, testCasePost, false);
			String renderedLayout = sb.toString().replaceAll("\\r", "").trim();
			
			renderedLayout = modifyLayout(renderedLayout);
			
			File resultFile = new File(outputFolderPath + layoutName + LAYOUT_ENTRYTYPE_SPLIT  + LAYOUT_ENTRYTYPE_SPLITSUFFIX + entryType + LAYOUT_FILEEXTENSION);
			resultFile.createNewFile();
			
			FileWriter fw = new FileWriter(resultFile);
			fw.write(renderedLayout);
			fw.close();
		}
	}
	
}
