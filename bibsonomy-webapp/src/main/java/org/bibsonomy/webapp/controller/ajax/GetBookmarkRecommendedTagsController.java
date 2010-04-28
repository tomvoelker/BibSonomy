package org.bibsonomy.webapp.controller.ajax;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.webapp.command.ajax.AjaxRecommenderCommand;


/**
 * Class handling ajax requests for given post's recommendations.
 *    
 * @author fei
 * @version $Id$
 */
public class GetBookmarkRecommendedTagsController extends RecommendationsAjaxController<Bookmark> {

	@Override
	protected AjaxRecommenderCommand<Bookmark> createNewCommand() {
		return new AjaxRecommenderCommand<Bookmark>();
	}

	@Override
	protected Bookmark initResource() {
		final Bookmark bookmark = new Bookmark();
		/*
		 * set default values
		 */
		bookmark.setUrl("http://");
		return bookmark;
	}

}
