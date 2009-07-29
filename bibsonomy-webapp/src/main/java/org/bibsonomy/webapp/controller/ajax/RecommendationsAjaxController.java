package org.bibsonomy.webapp.controller.ajax;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.DBLogic;
import org.bibsonomy.database.systemstags.SystemTags;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.recommender.tags.multiplexer.MultiplexingTagRecommender;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.impl.XMLRenderer;
import org.bibsonomy.services.recommender.TagRecommender;
import org.bibsonomy.webapp.command.actions.EditPostCommand;
import org.bibsonomy.webapp.command.ajax.AjaxPublicationRecommenderCommand;
import org.bibsonomy.webapp.command.ajax.AjaxRecommenderCommand;
import org.bibsonomy.webapp.controller.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Some common operations for recommendation tasks.
 * 
 * TODO: This is a candidate for refactoring/performance optimization:
 *       As in the post*controller, the post-command has to be filled -
 *       at least with grouping information, as private posts shouldn't
 *       be sent to remotely installed recommender
 * @author fei
 * @version $Id$
 */
public abstract class RecommendationsAjaxController<R extends Resource> extends AjaxController implements MinimalisticController<AjaxRecommenderCommand<R>> {
	private static final Log log = LogFactory.getLog(RecommendationsAjaxController.class);

	/** this identifies spammer, which are flagged by an admin */
	private static final String USERSPAMALGORITHM = "admin";
	
	/** default recommender for serving spammers */
	private TagRecommender spamTagRecommender;

	/** 
	 * To sort out spam posts, we need access to informations 
	 * from the spam detection framework 
	 */
	private LogicInterface adminLogic;
	
	/**
	 * Provides tag recommendations to the user.
	 */
	private MultiplexingTagRecommender tagRecommender = null;
	
	/**
	 * Copy the groups from the command into the post (make proper groups from
	 * them)
	 * 
	 * @param command -
	 *            contains the groups as represented by the form fields.
	 * @param post -
	 *            the post whose groups should be populated from the command.
	 * @see #initCommandGroups(EditPostCommand, Post)
	 */
	protected void initPostGroups(final EditPostCommand<R> command, final Post<R> post) {
		log.debug("initializing post's groups from command");
		/*
		 * we can avoid some checks here, because they're done in the validator
		 * ...
		 */
		final Set<Group> postGroups = post.getGroups();
		final String abstractGrouping = command.getAbstractGrouping();
		if ("other".equals(abstractGrouping)) {
			log.debug("found 'other' grouping");
			/*
			 * copy groups into post
			 */
			final List<String> groups = command.getGroups();
			log.debug("groups in command: " + groups);
			for (final String groupname : groups) {
				postGroups.add(new Group(groupname));
			}
			log.debug("groups in post: " + postGroups);
		} else {
			log.debug("public or private post");
			/*
			 * if the post is private or public --> remove all groups and add
			 * one (private or public)
			 */
			postGroups.clear();
			postGroups.add(new Group(abstractGrouping));
		}
	}
	
	@Override
	public View workOn(AjaxRecommenderCommand<R> command) {
		final RequestWrapperContext context = command.getContext();
		
		/*
		 * only users which are logged in might post bookmarks -> send them to
		 * login page
		 */
		if (!context.isUserLoggedIn()) {
			command.setResponseString("");
		} else {		
			final User loginUser = context.getLoginUser();
			
			//------------------------------------------------------------------------
			// THIS IS AN ISSUE WE STILL HAVE TO DISCUSS:
			// During the ECML/PKDD recommender challenge, many recommender systems
			// couldn't deal with the high load, so we filter out those users, which
			// are flagged as spammer either by an admin, or by the framework for sure 
			// TODO: we could probably also filter out those users, which are 
			//       flagged as 'spammer unsure' 
			//------------------------------------------------------------------------
			User dbUser = adminLogic.getUserDetails(loginUser.getName());

			/*
			 * set the user of the post to the loginUser (the recommender might need
			 * the user name)
			 */
			command.getPost().setUser(loginUser);

			/*
			 * initialize groups
			 */
			initPostGroups(command, command.getPost());

			// set postID for recommender
			command.getPost().setContentId(command.getPostID());

			if( dbUser.isSpammer() ) {///*(dbUser.isSpammer()==true)&&(dbUser.getPrediction()==1||dbUser.getAlgorithm()==USERSPAMALGORITHM) */) {
				// the user is a spammer
				log.debug("Filtering out recommendation request from spammer");
				if ( getSpamTagRecommender()!=null )	{
					SortedSet<RecommendedTag> result = 
						getSpamTagRecommender().getRecommendedTags(command.getPost());
					processRecommendedTags(command, result);
				} else {
					command.setResponseString("");
				}
			} else {				
				// the user doesn't seem to be a spammer
				/*
				 * get the recommended tags for the post from the command
				 */
				if ( getTagRecommender()!=null )	{
					SortedSet<RecommendedTag> result = 
						getTagRecommender().getRecommendedTags(command.getPost(), command.getPostID());
					processRecommendedTags(command, result);
				} else {
					command.setResponseString("");
				}
			}
		}
		return Views.AJAX_RESPONSE;
	}
	//------------------------------------------------------------------------
	// private helper functions
	//------------------------------------------------------------------------
	private void processRecommendedTags(AjaxRecommenderCommand<R> command, SortedSet<RecommendedTag> tags) {
		command.setRecommendedTags(tags);
		Renderer renderer = XMLRenderer.getInstance();
		StringWriter sw = new StringWriter(100);
		renderer.serializeRecommendedTags(sw, command.getRecommendedTags());
		command.setResponseString(sw.toString());
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

	public void setAdminLogic(LogicInterface adminDbLogic) {
		this.adminLogic = adminDbLogic;
	}

	public LogicInterface getAdminLogic() {
		return adminLogic;
	}

	public void setSpamTagRecommender(TagRecommender spamTagRecommender) {
		this.spamTagRecommender = spamTagRecommender;
	}

	public TagRecommender getSpamTagRecommender() {
		return spamTagRecommender;
	}

}
