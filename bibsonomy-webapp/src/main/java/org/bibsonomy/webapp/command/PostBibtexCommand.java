package org.bibsonomy.webapp.command;


import java.util.Collection;

import org.bibsonomy.common.enums.PublicationType;
import org.bibsonomy.common.enums.ResourceScope;


/**
 * @author ema
 * @version $Id$
 */

public class PostBibtexCommand extends TabsCommand<Object>{
	
	String[] tabTitleFmtKeys = {"post_bibtex.manual.title", "post_bibtex.pub_snippet.title", "post_bibtex.bibtex_endnote.title"};
	
	Collection<String> resourceTypes = null;
	Collection<String> resourceScopes = null;
	
	public Collection<String> getResourceScopes() {
		return this.resourceScopes;
	}

	public void setResourceScopes(Collection<String> resourceScopes) {
		this.resourceScopes = resourceScopes;
	}

	public PostBibtexCommand()
	{
		//Preparation for all tabs
		//=== make the tabtitle available
		for(int i=0; i<tabTitleFmtKeys.length; i++)
			addTab(i, tabTitleFmtKeys[i]);
		
		//=== change default tab to the manual tab
		selTab = 0;
		
		//Preparation for the 1st tab
		//=== make the sources available (0th tab)
		resourceTypes = PublicationType.getAllValues();
		
		//Preparation for the 2nd tab
		//=== make the sources available (0th tab)
		resourceScopes = ResourceScope.getAllValues();
	
		
	}
	
	public Collection<String> getResourceTypes() {
		return this.resourceTypes;
	}

	public void setResourceTypes(Collection<String> resourceTypes) {
		this.resourceTypes = resourceTypes;
	}
}
