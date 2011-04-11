package org.bibsonomy.model.sync;

import org.bibsonomy.model.Resource;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationPost extends SynchronizationResource {
	
	/**
	 * Expected memory usage for one Post: 2 * 32 byte (hasheS) + 2 * 24 byte (dates) + 4byte (state) +   = 112 byte
	 * for 100k Posts: approximately 11 MB
	 */
	
	
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

}
