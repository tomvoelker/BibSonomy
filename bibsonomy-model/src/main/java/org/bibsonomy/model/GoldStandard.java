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
	 */
	public void addAllToReferences(final Set<R> resources);

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

}