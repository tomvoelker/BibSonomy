package org.bibsonomy.webapp.controller.ajax;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.webapp.command.ajax.AjaxPublicationRecommenderCommand;
import org.bibsonomy.webapp.command.ajax.AjaxRecommenderCommand;


/**
 * Class handling ajax requests for given post's recommendations.
 *    
 * @author fei
 * @version $Id$
 */
public class GetPublicationRecommendedTagsController extends RecommendationsAjaxController<BibTex>  {
	
	@Override
	protected AjaxRecommenderCommand<BibTex> createNewCommand() {
		return new AjaxPublicationRecommenderCommand();
	}

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
