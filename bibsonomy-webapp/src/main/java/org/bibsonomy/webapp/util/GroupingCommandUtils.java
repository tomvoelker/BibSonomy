package org.bibsonomy.webapp.util;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Group;
import org.bibsonomy.webapp.command.GroupingCommand;

/**
 * @author dzo
 * @version $Id$
 */
public abstract class GroupingCommandUtils {
	private static final Log log = LogFactory.getLog(GroupingCommandUtils.class);
	
	/**
	 * TODO
	 */
	public static final String OTHER_ABSTRACT_GROUPING = "other";
	
	/**
	 * Copy the groups from the command into provided groups set (make proper groups from
	 * them)
	 * 
	 * @param command -
	 *            contains the groups as represented by the form fields.
	 * @param groupsToInit -
	 *            the groups should be populated from the command.
	 */
	public static void initGroups(final GroupingCommand command, final Set<Group> groupsToInit) {
		log.debug("initializing groups from command");
		/*
		 * we can avoid some checks here, because they're done in the validator
		 * ...
		 */
		final String abstractGrouping = command.getAbstractGrouping();
		if (OTHER_ABSTRACT_GROUPING.equals(abstractGrouping)) {
			log.debug("found 'other' grouping");
			/*
			 * copy groups into post
			 */
			final List<String> groups = command.getGroups();
			log.debug("groups in command: " + groups);
			for (final String groupname : groups) {
				groupsToInit.add(new Group(groupname));
			}
			log.debug("groups: " + groupsToInit);
		} else {
			log.debug("public or private post");
			/*
			 * if the post is private or public --> remove all groups and add
			 * one (private or public)
			 */
			groupsToInit.clear();
			groupsToInit.add(new Group(abstractGrouping));
		}
	}
}
