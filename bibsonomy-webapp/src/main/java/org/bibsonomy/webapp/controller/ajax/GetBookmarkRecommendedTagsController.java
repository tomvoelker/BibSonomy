package org.bibsonomy.webapp.controller.ajax;

import java.io.StringWriter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.tags.multiplexer.MultiplexingTagRecommender;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.impl.XMLRenderer;
import org.bibsonomy.webapp.command.ajax.AjaxRecommenderCommand;
import org.bibsonomy.webapp.controller.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;


/**
 * Class handling ajax requests for given post's recommendations.
 *    
 * @author fei
 * @version $Id$
 */
public class GetBookmarkRecommendedTagsController extends AjaxController implements MinimalisticController<AjaxRecommenderCommand<Bookmark>> {
	private static final Log log = LogFactory.getLog(GetBookmarkRecommendedTagsController.class);

	/**
	 * Provides tag recommendations to the user.
	 */
	private MultiplexingTagRecommender tagRecommender = null;
	
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

	public View workOn(AjaxRecommenderCommand<Bookmark> command) {
		final RequestWrapperContext context = command.getContext();
		
		/*
		 * only users which are logged in might post bookmarks -> send them to
		 * login page
		 */
		if (!context.isUserLoggedIn()) {
			command.setResponseString("");
		} else {		
			final User loginUser = context.getLoginUser();
			/*
			 * set the user of the post to the loginUser (the recommender might need
			 * the user name)
			 */
			command.getPost().setUser(loginUser);

			// FIXME: post's grouping is not handled - this is necessary if we don't want private
			//        posts to be send to remote recommenders
			
			/*
			 * get the recommended tags for the post from the command
			 */
			if (getTagRecommender() != null)	{
				SortedSet<RecommendedTag> result = getTagRecommender().getRecommendedTags(command.getPost(), command.getPostID()); 
				command.setRecommendedTags(result);
				Renderer renderer = XMLRenderer.getInstance();
				StringWriter sw = new StringWriter(100);
				renderer.serializeRecommendedTags(sw, command.getRecommendedTags());
				command.setResponseString(sw.toString());
			}
		}
		return Views.AJAX_RESPONSE;
	}
	
	//------------------------------------------------------------------------
	// private helper
	//------------------------------------------------------------------------
	private static Post<? extends Resource> createPost() {
		final Post<Resource> post = new Post<Resource>();
		final User user = new User();
		user.setName("foo");
		final Group group = new Group();
		group.setName("bar");
		final Tag tag = new Tag();
		tag.setName("foobar");
		post.setUser(user);
		post.getGroups().add(group);
		post.getTags().add(tag);
		post.setDate(new Date(System.currentTimeMillis()));
		final BibTex bibtex = new BibTex();
		bibtex.setTitle("foo and bar");
		bibtex.setIntraHash("abc");
		bibtex.setInterHash("abc");
		bibtex.setYear("2009");
		bibtex.setBibtexKey("test");
		bibtex.setEntrytype("twse");
		post.setResource(bibtex);
		
		return post;
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
