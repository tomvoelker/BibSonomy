package org.bibsonomy.webapp.validation.util;

import org.bibsonomy.services.database.DatabaseSchemaInformation;

/**
 * a mock {@link DatabaseSchemaInformation} implementation
 * returns 4 * property.length()
 * 
 * @author dzo
 * @version $Id$
 */
public class MockDatabaseSchemaInformation implements DatabaseSchemaInformation {

	@Override
	public int getMaxColumnLengthForProperty(Class<?> resourceClass, String property) {
		if (property == null) {
			return -1;
		}
		return property.length() * 4;
	}

}
