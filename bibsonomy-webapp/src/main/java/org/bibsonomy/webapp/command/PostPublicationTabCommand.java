package org.bibsonomy.webapp.command;




/**
 * @author ema
 * @version $Id$
 */

public class PostPublicationTabCommand extends TabsCommand<Object>{

	private final static String[] tabTitles = {
		"post_bibtex.manual.title", 
		"post_bibtex.pub_snippet.title", 
		"post_bibtex.bibtex_endnote.title", 
		"post_bibtex.doi_isbn.title"
	};

	public PostPublicationTabCommand(){
		
		// Preparation for all tabs
		//=== make the tabtitle available
		addTabs(tabTitles);

		//=== change default tab to the manual tab
		selTab = 0;
	}

}
