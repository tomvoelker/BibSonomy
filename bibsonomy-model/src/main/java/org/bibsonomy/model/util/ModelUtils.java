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
	// FIXME we don't need this anymore...
//	public static Set<Post<Resource>> putResourcesIntoPosts(final List<? extends Resource> resources) {
//		final Set<Post<Resource>> rVal = new LinkedHashSet<Post<Resource>>();
//		for (final Resource resource : resources) {
//			final Post<Resource> post = new Post<Resource>();
////			post.setResource(resource);
//			post.setTags(resource.getTags());
//			final User user = new User();
//			user.setName(resource.getUserName());
//			post.setUser(user);
//			rVal.add(post);
//		}
//		return rVal;
//	}
}