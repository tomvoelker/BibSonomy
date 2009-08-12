package org.bibsonomy.webapp.controller.ajax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Tag;
import org.bibsonomy.webapp.command.ajax.AjaxRecommenderCommand;


/**
 * Class handling ajax requests for given post's recommendations.
 *    
 * @author fei
 * @version $Id$
 */
public class GetBookmarkRecommendedTagsController extends RecommendationsAjaxController<Bookmark> {
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(GetBookmarkRecommendedTagsController.class);

	//------------------------------------------------------------------------
	// MinimalisticController interface
	//------------------------------------------------------------------------
	public AjaxRecommenderCommand<Bookmark> instantiateCommand() {
		final AjaxRecommenderCommand<Bookmark> command = new AjaxRecommenderCommand<Bookmark>();
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
		command.setPost(new Post<Bookmark>());
		command.getPost().setResource(new Bookmark());
		command.setAbstractGrouping("public");

		/*
		 * set default values.
		 */
		command.getPost().getResource().setUrl("http://");
		return command;
	}

}
