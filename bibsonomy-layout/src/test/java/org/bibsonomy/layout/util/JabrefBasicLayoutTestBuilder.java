package org.bibsonomy.layout.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.layout.jabref.JabrefLayout;
import org.bibsonomy.layout.jabref.JabrefLayoutRenderer;
import org.bibsonomy.layout.jabref.JabrefLayoutRendererTest;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.services.URLGenerator;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class JabrefBasicLayoutTestBuilder {
	
	private static final JabrefLayoutRenderer RENDERER = new JabrefLayoutRenderer();
	static {
		RENDERER.setDefaultLayoutFilePath("src/main/resources/org/bibsonomy/layout/jabref");
		RENDERER.setUrlGenerator(new URLGenerator("http://www.bibsonomy.org"));
	}
	
	/*
	 * Specify the Layout and entryType, for which you want to create a TestCase/result-File
	 */
	private String layoutName = "html";
	private String[] entryTypes = {"article"};
		//{"article","book","booklet","conference","electronic","inbook","incollection","inproceedings","","manual","mastersthesis","phdthesis","proceedings","techreport","unpublished"};
	
	private String outputFolderPath = "src/test/resources/temp/";
	private String testNameTag = "";
	
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
		JabrefLayout layout = RENDERER.getLayout(layoutName, "foo");
		for (String entryType : entryTypes) {
			/*
			 * Here you can modify the post, from which the layoutTest should be build
			 */
			List<Post<BibTex>> testCasePost = JabrefLayoutRendererTest.getPosts(entryType);//JabrefLayoutAntiScriptInjectionTest.getTestCasePost(entryType);
			
			StringBuffer sb = RENDERER.renderLayout(layout, testCasePost, false);
			String renderedLayout = sb.toString();
			
			renderedLayout = modifyLayout(renderedLayout);
			
			File resultFile = new File(outputFolderPath + layoutName + "#" + testNameTag + entryType + ".layoutResult");
			resultFile.createNewFile();
			
			FileWriter fw = new FileWriter(resultFile);
			fw.write(renderedLayout);
			fw.close();
		}
	}
	
}
