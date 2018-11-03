package org.bibsonomy.database.common;

import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;

/**
 * this class provides methods for resource database managers
 * @param <R>
 *
 * @author dzo
 */
public abstract class ResourceAwareAbstractDatabaseManagerWithSessionManagement<R extends Resource> extends AbstractDatabaseManagerWithSessionManagement {

	private final Class<R> resourceClass;
	private final boolean useSuperiorResourceClass;

	/**
	 * default constructor
	 * @param resourceClass the resource class
	 */
	public ResourceAwareAbstractDatabaseManagerWithSessionManagement(Class<R> resourceClass) {
		this(resourceClass, false);
	}

	/**
	 * default constructor
	 * @param resourceClass
	 * @param useSuperiorResourceClass
	 */
	public ResourceAwareAbstractDatabaseManagerWithSessionManagement(Class<R> resourceClass, boolean useSuperiorResourceClass) {
		this.resourceClass = resourceClass;
		this.useSuperiorResourceClass = useSuperiorResourceClass;
	}

	/**
	 * @return the resource name for the query
	 */
	protected String getResourceName() {
		if (this.useSuperiorResourceClass) {
			final Class<? extends Resource> superiorResourceClass = ResourceFactory.findSuperiorResourceClass(this.resourceClass);
			return superiorResourceClass.getSimpleName();
		}

		return this.resourceClass.getSimpleName();
	}

	/**
	 * @return the {@link ConstantID} for the resource class
	 */
	protected ConstantID getConstantID() {
		return ConstantID.getContentTypeByClass(this.resourceClass);
	}
}
