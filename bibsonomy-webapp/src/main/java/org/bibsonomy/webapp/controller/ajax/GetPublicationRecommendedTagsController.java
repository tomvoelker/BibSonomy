package org.bibsonomy.webapp.controller.ajax;

import org.bibsonomy.model.BibTex;


/**
 * Class handling ajax requests for given post's recommendations.
 *    
 * @author fei
 * @version $Id$
 */
public class GetPublicationRecommendedTagsController extends RecommendationsAjaxController<BibTex>  {

	@Override
	protected BibTex initResource() {
		final BibTex publication = new BibTex();
		/*
		 * set default values
		 */
		publication.setUrl("http://");
		return publication;
	}
}
