package org.bibsonomy.community.importer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.community.algorithm.Algorithm;
import org.bibsonomy.community.algorithm.MockAlgorithm;
import org.bibsonomy.community.model.Cluster;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.community.model.Tag;
import org.bibsonomy.community.model.User;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;

public class MockupImporter implements CommunityImporter {
	private static final int N_CLUSTERS = 5;
	private static final int N_TOPICS = 3;
	private static final int N_USERS = 10;
	private static final int N_TAGS = 10;
	private static final int N_RESOURCES = 100;

	String[] topics = {"politics", "computer", "medcine"};

	public int getClusterCount() {
		return N_CLUSTERS;
	}

	public int getTopicCount() {
		return N_TOPICS;
	}
	
	public Collection<Cluster<User>> getCommunities() {
		return generateCommunities();
	}


	public Collection<Cluster<Tag>> getTopics() {
		return generateTopics();
	}
	
	private Collection<Cluster<User>> generateCommunities() {
		List<Tag> annotations = new LinkedList<Tag>();
		for( String topic : topics ) {
			annotations.add(new Tag(topic));
		}
		
		Collection<Cluster<User>> clusters = new ArrayList<Cluster<User>>(N_CLUSTERS); 
		for( int i=0; i<N_CLUSTERS; i++ ) {
			Cluster<User> cluster = new Cluster<User>();
			cluster.setClusterID(i);
			Collection<User> users = new LinkedList<User>();
			int nUsers = (int)Math.floor(Math.random()*N_USERS);
			for( int j=0; j<nUsers; j++ ) {
				String name = topics[i%topics.length]+"_"+j+"_"+Math.random();
				User user = new User(name.substring(0,Math.min(30, name.length())));
				user.setWeight(Math.random());
				users.add(user);
			}
			cluster.setInstances(users);
			cluster.setAnnotation(annotations.subList((i%topics.length), (i%topics.length)+1));
			clusters.add(cluster);
		}
		
		return clusters;
	}
	
	private Collection<Cluster<Tag>> generateTopics() {	
		List<Tag> annotations = new LinkedList<Tag>();
		for( String topic : topics ) {
			annotations.add(new Tag(topic));
		}

		Collection<Cluster<Tag>> clusters = new ArrayList<Cluster<Tag>>(topics.length); 
		for( int i=0; i<topics.length; i++ ) {
			Cluster<Tag> cluster = new Cluster<Tag>();
			cluster.setClusterID(i);
			Collection<Tag> tags = new LinkedList<Tag>(); 
			int nTags= (int)Math.floor(Math.random()*N_TAGS);
			for( int j=0; j<nTags; j++ ) {
				Tag tag = new Tag(topics[i]+"_"+j);
				tag.setUsercount((int)Math.round(100*1.0/(j+1)));
				tag.setWeight(Math.random());
				tags.add(tag);
			}
			cluster.setInstances(tags);
			cluster.setAnnotation(annotations.subList(i, i+1));
			clusters.add(cluster);
		}
		
		return clusters;
	}

	public Collection<Cluster<Post<? extends Resource>>> getResources() {
		List<Tag> annotations = new LinkedList<Tag>();
		for( String topic : topics ) {
			annotations.add(new Tag(topic));
		}

		int idx = 0;
		Collection<Cluster<Post<? extends Resource>>> clusters = new ArrayList<Cluster<Post<? extends Resource>>>(topics.length); 
		for( int i=0; i<N_CLUSTERS; i++ ) {
			Cluster<Post<? extends Resource>> cluster = new Cluster<Post<? extends Resource>>();
			cluster.setClusterID(i);
			Collection<Post<? extends Resource>> posts = new LinkedList<Post<? extends Resource>>(); 
			int nPosts = (int)Math.floor(Math.random()*N_RESOURCES);
			for( int j=0; j<nPosts; j++ ) {
				Post<? extends Resource> post = new Post<Bookmark>();
				post.setContentId(idx++);
				post.setWeight(Math.random());
				posts.add(post);
			}
			cluster.setInstances(posts);
			cluster.setAnnotation(annotations.subList((i%topics.length), (i%topics.length)+1));
			clusters.add(cluster);
		}
		
		return clusters;
	}

	public Algorithm getAlgorithm() {
		return new MockAlgorithm("MockupAlgorithm", "testSettings");
	}
}
