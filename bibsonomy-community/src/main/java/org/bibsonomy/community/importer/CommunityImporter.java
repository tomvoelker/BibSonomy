package org.bibsonomy.community.importer;

import java.util.Collection;

import org.bibsonomy.community.algorithm.Algorithm;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.community.model.Tag;
import org.bibsonomy.community.model.User;
import org.bibsonomy.community.model.Cluster;
import org.bibsonomy.model.Resource;

/**
 * interface for different importer depending on the algorithm used
 * 
 * @author fei
 *
 */
public interface CommunityImporter {
	
	public Algorithm getAlgorithm();

	public int getClusterCount();
	
	public int getTopicCount();
	
	public Collection<Cluster<User>> getCommunities();
	
	public Collection<Cluster<Tag>> getTopics();
	
	public Collection<Cluster<Post<? extends Resource>>> getResources();
}
