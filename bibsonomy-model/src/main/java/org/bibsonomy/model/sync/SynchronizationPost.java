package org.bibsonomy.model.sync;

import org.bibsonomy.model.Post;
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
	private Post<? extends Resource> post;

	/**
	 * Checks if two posts are the same
	 * 
	 * FIXME: should we overwrite equals() instead?
	 * 
	 * @see org.bibsonomy.model.sync.SynchronizationResource#isSame(org.bibsonomy.model.sync.SynchronizationResource)
	 * 
	 */
	@Override
	public boolean isSame(final SynchronizationResource post) {
		if (post instanceof SynchronizationPost) {
			final SynchronizationPost p = (SynchronizationPost)post;
			return (
					p.getChangeDate().equals(this.getChangeDate()) && 
					p.getCreateDate().equals(this.getCreateDate()) && 
					p.getIntraHash().equals(this.getIntraHash())
			);
		}
		return false;
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
	public void setPost(Post<? extends Resource> post) {
		this.post = post;
	}

	/**
	 * @return the post
	 */
	public Post<? extends Resource> getPost() {
		return post;
	}

	@Override
	public String toString() {
		return intraHash;
	}

}
