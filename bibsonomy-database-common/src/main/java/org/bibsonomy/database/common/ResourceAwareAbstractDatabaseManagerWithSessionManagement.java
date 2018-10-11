package org.bibsonomy.database.common;

import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.model.Resource;

/**
 * this class provideds methods for resource database managers
 * @param <R>
 *
 * @author dzo
 */
public abstract class ResourceAwareAbstractDatabaseManagerWithSessionManagement<R extends Resource> extends AbstractDatabaseManagerWithSessionManagement {

	private final Class<R> resourceClass;

	/**
	 * default constructor
	 * @param resourceClass the resource class
	 */
	public ResourceAwareAbstractDatabaseManagerWithSessionManagement(Class<R> resourceClass) {
		this.resourceClass = resourceClass;
	}

	/**
	 * @return the resource name for the query
	 */
	protected String getResourceName() {
		return this.resourceClass.getSimpleName();
	}

	/**
	 * @return the {@link ConstantID} for the resource class
	 */
	protected ConstantID getConstantID() {
		return ConstantID.getContentTypeByClass(this.resourceClass);
	}
}
