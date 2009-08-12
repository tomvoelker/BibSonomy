package org.bibsonomy.webapp.controller.ajax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Tag;
import org.bibsonomy.webapp.command.ajax.AjaxPublicationRecommenderCommand;


/**
 * Class handling ajax requests for given post's recommendations.
 *    
 * @author fei
 * @version $Id$
 */
public class GetPublicationRecommendedTagsController extends RecommendationsAjaxController<BibTex>  {
	private static final Log log = LogFactory.getLog(GetPublicationRecommendedTagsController.class);

	//------------------------------------------------------------------------
	// MinimalisticController interface
	//------------------------------------------------------------------------
	public AjaxPublicationRecommenderCommand instantiateCommand() {
		final AjaxPublicationRecommenderCommand command = new AjaxPublicationRecommenderCommand();
		/*
		 * initialize lists
		 * FIXME: is it really neccessary to initialize ALL those lists? Which are really needed?
		 */
		command.setGroups(new ArrayList<String>());
		command.setRelevantGroups(new ArrayList<String>());
		command.setRelevantTagSets(new HashMap<String, Map<String, List<String>>>());
		command.setRecommendedTags(new TreeSet<RecommendedTag>());
		command.setCopytags(new ArrayList<Tag>());
		/*
		 * initialize post & resource
		 */
		command.setPost(new Post<BibTex>());
		command.getPost().setResource(new BibTex());
		command.setAbstractGrouping("public");

		/*
		 * set default values.
		 */
		command.getPost().getResource().setUrl("http://");
		return command;
	}

}
