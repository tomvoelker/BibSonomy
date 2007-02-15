package org.bibsonomy.database.newImpl;

import java.util.Set;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.enums.GroupingEntity;


/*
 * for every content type there should exists a separate class which extends this class.
 */
public abstract class AbstractContentDBManager {

	public abstract Set<Post<Resource>> getPosts(String authUser, GroupingEntity grouping, String groupingName, Set<String> tags, String hash, boolean popular, boolean added, int start, int end, boolean continuous);

}
