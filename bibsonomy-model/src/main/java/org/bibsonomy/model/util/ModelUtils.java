package org.bibsonomy.model.util;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;

/**
 * We are juggling with the model.
 * 
 * @author Christian Schenk
 */
public class ModelUtils {

	/**
	 * Creates a set of posts from a list of resources.
	 */
	public static Set<Post> putResourcesIntoPosts(final List<? extends Resource> resources) {
		final Set<Post> rVal = new LinkedHashSet<Post>();
		for (final Resource resource : resources) {
			final Post<Resource> post = new Post<Resource>();
			post.setResource(resource);
			post.setTags(resource.getTags());
			final User user = new User();
			user.setName(resource.getUserName());
			post.setUser(user);
			rVal.add(post);
		}
		return rVal;
	}
}