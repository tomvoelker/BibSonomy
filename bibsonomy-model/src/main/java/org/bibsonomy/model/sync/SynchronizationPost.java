package org.bibsonomy.model.sync;

import org.bibsonomy.model.Resource;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationPost extends SynchronizationResource {
	
	/**
	 * interHash of this post
	 */
	private String intraHash;
	/**
	 * intraHash of this post
	 */
	private String interHash;
	
	/**
	 * class of this post, e. g. Bibtex or Bookmark
	 */
	private Class<? extends Resource> resourceType;
	
	/**
	 * resource attached from server
	 */
	private Resource post;
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.model.synch.SynchronizationResource#same(org.bibsonomy.model.synch.SynchronizationResource)
	 */
	@Override
	public boolean isSame(SynchronizationResource post) {
		SynchronizationPost p = (SynchronizationPost)post;
		return (p.getChangeDate().equals(this.getChangeDate()) && p.getCreateDate().equals(this.getCreateDate()) && p.getIntraHash().equals(this.getIntraHash()));
	}
	
	/**
	 * @param intraHash the intraHash to set
	 */
	public void setIntraHash(String intraHash) {
		this.intraHash = intraHash;
	}
	/**
	 * @return the intraHash
	 */
	public String getIntraHash() {
		return intraHash;
	}
	/**
	 * @param interHash the interHash to set
	 */
	public void setInterHash(String interHash) {
		this.interHash = interHash;
	}
	/**
	 * @return the interHash
	 */
	public String getInterHash() {
		return interHash;
	}
	
	/**
	 * @param resourceType to set
	 */
	public void setResourceType(Class<? extends Resource> resourceType) {
		this.resourceType = resourceType;
	}
	
	/**
	 * 
	 * @return the resourceType
	 */
	public Class<? extends Resource> getResourceType() {
		return resourceType;
	}

	/**
	 * @param post the post to set
	 */
	public void setPost(Resource post) {
		this.post = post;
	}

	/**
	 * @return the post
	 */
	public Resource getPost() {
		return post;
	}

}
