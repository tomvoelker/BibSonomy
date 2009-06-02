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
import org.bibsonomy.model.Bookmark;
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
public class GetBookmarkRecommendedTagsController extends SimpleFormController {
	private static final Log log = LogFactory.getLog(GetBookmarkRecommendedTagsController.class);

	/**
	 * Provides tag recommendations to the user.
	 */
	private MultiplexingTagRecommender tagRecommender = null;

	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
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
		command.getPost().setUser(new User());
		command.setAbstractGrouping("public");

		/*
		 * set default values.
		 */
		command.getPost().getResource().setUrl("http://");
		return command;

	}
	
	
	@Override
	protected ModelAndView onSubmit(Object command) throws Exception {
		
		if (command instanceof AjaxRecommenderCommand) {
			final AjaxRecommenderCommand<Bookmark> cmd = (AjaxRecommenderCommand<Bookmark>) command;
			
			/*
			 * get the recommended tags for the post from the command
			 */
			if (tagRecommender != null)	{
				final SortedSet<RecommendedTag> result = tagRecommender.getRecommendedTags(cmd.getPost(), cmd.getPostID()); 
				cmd.setRecommendedTags(result);
				final Renderer renderer = XMLRenderer.getInstance();
				final StringWriter sw = new StringWriter(100);
				renderer.serializeRecommendedTags(sw, cmd.getRecommendedTags());
				cmd.setResponseString(sw.toString());
			}
		}
		
		return super.onSubmit(command);
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
