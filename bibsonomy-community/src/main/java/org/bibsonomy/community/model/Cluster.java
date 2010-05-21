package org.bibsonomy.community.model;

import java.util.Collection;

import org.bibsonomy.model.Tag;

/**
 * class for modeling automatically detected clusters/communities
 * @author fei
 *
 */
public class Cluster<T> {

	/** collection of tags a user has assigned to this cluster */
	private Collection<Tag> annotation;
	
	/** instances belonging to this cluster */
	private Collection<T> instances;
	
	/** unique identifier for this cluster */
	private int clusterID; 
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setAnnotation(Collection<Tag> annotation) {
		this.annotation = annotation;
	}

	public Collection<Tag> getAnnotation() {
		return annotation;
	}

	public void setInstances(Collection<T> instances) {
		this.instances = instances;
	}

	public Collection<T> getInstances() {
		return instances;
	}

	public void setClusterID(int clusterID) {
		this.clusterID = clusterID;
	}

	public int getClusterID() {
		return clusterID;
	}
}
