package org.bibsonomy.recommender.webapp.controller.ajax;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.tags.multiplexer.MultiplexingTagRecommender;
import org.bibsonomy.recommender.webapp.command.ajax.AjaxRecommenderCommand;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.impl.XMLRenderer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;


/**
 * Class handling ajax requests for given post's recommendations.
 *    
 * @author fei
 * @version $Id$
 */
public class GetPublicationRecommendedTagsController extends SimpleFormController {
	private static final Log log = LogFactory.getLog(GetPublicationRecommendedTagsController.class);

	/**
	 * Provides tag recommendations to the user.
	 */
	private MultiplexingTagRecommender tagRecommender = null;


	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		final AjaxRecommenderCommand<BibTex> command = new AjaxRecommenderCommand<BibTex>();
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
		command.getPost().setUser(new User());
		command.setAbstractGrouping("public");
		
		/*
		 * set default values.
		 */
		command.getPost().getResource().setUrl("http://");
		return command;
	}

	
	
	
	@Override
	protected ModelAndView onSubmit(Object cmd) throws Exception {
		if (cmd instanceof AjaxRecommenderCommand) {
			final AjaxRecommenderCommand<BibTex> command= (AjaxRecommenderCommand<BibTex>) cmd;

			log.info("work on called with command " + command);
			log.info("post = " + command.getPost());
			/*
			 * get the recommended tags for the post from the command
			 */
			if (tagRecommender != null)	{
				final SortedSet<RecommendedTag> result = tagRecommender.getRecommendedTags(command.getPost(), command.getPostID()); 
				command.setRecommendedTags(result);
				final Renderer renderer = XMLRenderer.getInstance();
				final StringWriter sw = new StringWriter(100);
				renderer.serializeRecommendedTags(sw, command.getRecommendedTags());
				command.setResponseString(sw.toString());
			}
		}
		return super.onSubmit(cmd);
	}


	//------------------------------------------------------------------------
	// Getter/Setter
	//------------------------------------------------------------------------
	public void setTagRecommender(MultiplexingTagRecommender tagRecommender) {
		this.tagRecommender = tagRecommender;
	}

	public MultiplexingTagRecommender getTagRecommender() {
		return tagRecommender;
	}

}
