package org.bibsonomy.webapp.command.ajax;

import java.util.SortedSet;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.AjaxCommand;
import org.bibsonomy.webapp.command.actions.EditBookmarkCommand;



/**
 * Command for recommendation ajax requests.
 * 
 * @author fei
 * @version $Id$
 */
public class AjaxBookmarkRecommenderCommand extends EditBookmarkCommand implements AjaxCommandInterface {
	private String responseString;

	public String getResponseString() {
		return this.responseString;
	}

	public void setResponseString(String response) {
		this.responseString = response;
	}
}
