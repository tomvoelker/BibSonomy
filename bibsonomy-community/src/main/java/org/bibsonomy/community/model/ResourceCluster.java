package org.bibsonomy.community.model;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.community.model.Tag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;

/**
 * class for modeling automatically detected clusters/communities
 * @author fei
 *
 */
public class ResourceCluster {
	private final static Log log = LogFactory.getLog(ResourceCluster.class);

	/** collection of tags a user has assigned to this cluster */
	private Collection<Tag> annotation;
	
	/** collection of tags assigned to resources of this cluster */
	private Collection<Tag> tags;

	/** instances belonging to this cluster */
	private Collection<Post<Bookmark>> bookmark;
	
	private Collection<Post<BibTex>> bibtex;
	
	/** different runs of an algorithm lead to different clusters with the same clusterId*/
	private int runID;
	
	/** unique identifier for this cluster within the given run set */
	private int clusterID; 
	
	/** weighting value */
	private double weight;
	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setAnnotation(Collection<Tag> annotation) {
		this.annotation = annotation;
	}

	public Collection<Tag> getAnnotation() {
		return annotation;
	}

	public void setClusterID(int clusterID) {
		this.clusterID = clusterID;
		log.debug("Setting cluster id " + clusterID);
	}

	public int getClusterID() {
		return clusterID;
	}

	public void setRunID(int runID) {
		this.runID = runID;
	}

	public int getRunID() {
		return runID;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWeight() {
		return weight;
	}

	public void setBookmark(Collection<Post<Bookmark>> bookmark) {
		this.bookmark = bookmark;
	}

	public Collection<Post<Bookmark>> getBookmark() {
		return bookmark;
	}

	public void setBibtex(Collection<Post<BibTex>> bibtex) {
		this.bibtex = bibtex;
	}

	public Collection<Post<BibTex>> getBibtex() {
		return bibtex;
	}

	public void setTags(Collection<Tag> tags) {
		this.tags = tags;
	}

	public Collection<Tag> getTags() {
		return tags;
	}
}
