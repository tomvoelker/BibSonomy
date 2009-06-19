package org.bibsonomy.util.filter.posts.parser;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.bibsonomy.util.filter.posts.PostFilter;
import org.junit.Test;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class FilterRuleParserTest {


	@Test
	public void testParser() {
		final String tagString = "&[ resource.year >= 1992 resource.publisher = 'Springer'  (resource.address = 'Heidelberg' | resource.address = 'Berlin') : resource.address := 'Berlin/Heidelberg']";
		CommonTokenStream tokens = new CommonTokenStream();
		tokens.setTokenSource(new FilterRuleLexer(new ANTLRStringStream(tagString)));
		FilterRuleParser parser = new FilterRuleParser(tokens);
		try {
			System.out.println("################################################");
			parser.filter();
			final PostFilter postFilter = parser.getPostFilter();
			System.out.println(postFilter.getMatcher());
			System.out.println(":");
			System.out.println(postFilter.getModifier());
			System.out.println("################################################");
		} catch (RecognitionException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

}

