package org.bibsonomy.services.database;

/**
 * @author dzo
 * @version $Id$
 */
public interface DatabaseSchemaInformation {

	/**
	 * @param resourceClass
	 * @param property
	 * @return the max length of the property of the resource class
	 */
	public int getMaxColumnLengthForProperty(final Class<?> resourceClass, final String property);

}