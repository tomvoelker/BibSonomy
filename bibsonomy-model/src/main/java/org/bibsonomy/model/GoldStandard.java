package org.bibsonomy.model;

import java.util.Set;

/**
 * @author dzo
 * @version $Id$
 * @param <R> 
 */
public interface GoldStandard<R extends Resource> {

	/**
	 * @return the references of the resource (unmodifiable)
	 */
	public Set<R> getReferences();

	/**
	 * adds a resource to the references
	 * @param resource
	 * @return <tt>true</tt> iff references did not already contain the specified resource
	 */
	public boolean addToReferences(final R resource);

	/**
	 * adds a set of resources to the references
	 * @param resources
	 * @return <code>true</code> iff the references were changed as a result of the call
	 */
	public boolean addAllToReferences(final Set<R> resources);

	/**
	 * removes a resource from the reference list
	 * @param resource
	 * @return <tt>true</tt> iff reference contained the specified resource
	 */
	public boolean removeFromReferences(final R resource);
	
	/**
	 * removes all references
	 * @param resources the resources to remove from the references
	 * @return <code>true</code> iff the references were changed as a result of the call
	 */
	public boolean removeAllFromReferences(final Set<R> resources);
	
	/**
	 * @return the publications which reference the goldstandard
	 */
	public Set<R> getReferencedBy();
	
	/**
	 * adds a resoure to the referenced by set
	 * @param resource the resource to add to referenced by
	 * @return <code>true</code> iff referenced by did not already contained the specified resource
	 */
	public boolean addToReferencedBy(final R resource);
	
	/**
	 * adds a set of resources to the referenced by set
	 * @param resources
	 * @return <code>true</code> iff the referenced by set was changed as a result of the call
	 */
	public boolean addAllToReferencedBy(final Set<R> resources);

	/**
	 * removes a resource from the referenced by list
	 * @param resource
	 * @return <tt>true</tt> iff referenced by contained the specified resource
	 */
	public boolean removeFromReferencedBy(final R resource);
	
	/**
	 * removes all referenced by publications
	 * @param resources the resources to remove from the references
	 * @return <code>true</code> iff the referenced by set was changed as a result of the call
	 */
	public boolean removeAllFromReferencedBy(final Set<R> resources);

}