package org.bibsonomy.webapp.controller.ajax;

import org.bibsonomy.model.Bookmark;


/**
 * Class handling ajax requests for given post's recommendations.
 *    
 * @author fei
 * @version $Id$
 */
public class GetBookmarkRecommendedTagsController extends RecommendationsAjaxController<Bookmark> {

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
