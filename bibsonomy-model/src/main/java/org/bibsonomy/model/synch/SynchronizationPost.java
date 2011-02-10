package org.bibsonomy.model.synch;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
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
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.model.synch.SynchronizationResource#same(org.bibsonomy.model.synch.SynchronizationResource)
	 */
	@Override
	public boolean same(SynchronizationResource post) {
		SynchronizationPost p = (SynchronizationPost)post;
		if (resourceType == BibTex.class) {
			//TODO maybe to much parameters
			return (this.getIntraHash().equals(p.getIntraHash()) && this.getChangeDate().equals(p.getChangeDate()) && this.getCreateDate().equals(p.getCreateDate()));
		} else if (resourceType == Bookmark.class) {
			//TODO bookmarks ???
			return false;
		} else return false;
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

}
