package org.bibsonomy.model.comparators;

import java.util.Comparator;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;

/**
 * Uses a {@link PostComparator} to sort {@link ResourcePersonRelation}s by their {@link Post}
 */
public class ResourcePersonRelationByPostComparator implements Comparator<ResourcePersonRelation> {
	private final Comparator<Post<? extends BibTex>> postComparator;
	
	/**
	 * @param postComparator 
	 */
	public ResourcePersonRelationByPostComparator(final Comparator<Post<? extends BibTex>> postComparator) {
		this.postComparator = postComparator;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(ResourcePersonRelation o1, ResourcePersonRelation o2) {
		return this.postComparator.compare(o1.getPost(), o2.getPost());
	}

}
