package org.bibsonomy.webapp.controller.ajax;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.systemstags.SystemTags;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.webapp.command.actions.EditPostCommand;
import org.bibsonomy.webapp.controller.AjaxController;

/**
 * Some common operations for recommendation tasks.
 * 
 * TODO: This is a candidate for refactoring/performance optimisation:
 *       As in the post*controller, the post-command has to be filled -
 *       at least with grouping information, as private posts shouldn't
 *       be sent to remotely installed recommender
 * @author fei
 * @version $Id$
 */
public class RecommendationsAjaxController<R extends Resource> extends AjaxController {
	private static final Log log = LogFactory.getLog(RecommendationsAjaxController.class);

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
}
