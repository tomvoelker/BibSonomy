/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.validation;

import static org.bibsonomy.util.ValidationUtils.present;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.errors.FieldLengthErrorMessage;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.services.database.DatabaseSchemaInformation;

/**
 * @author dzo
 * @param <T> the model to validate
 */
public class DatabaseModelValidator<T> {
	private static final Log log = LogFactory.getLog(DatabaseModelValidator.class);
	
	private DatabaseSchemaInformation databaseSchemaInformation;
	
	/**
	 * checks if the string attributes of the model respect the field lengths of
	 * the database
	 * 
	 * @param model 	the model to validate
	 * @param id    	the id of the model (used for the errormessage)
	 * @param session	the session
	 */
	public void validateFieldLength(final T model, final String id, final DBSession session) {
		final Class<? extends Object> clazz = model.getClass();
		final FieldLengthErrorMessage fieldLengthError = new FieldLengthErrorMessage();
		try {
			final BeanInfo bi = Introspector.getBeanInfo(clazz);
			
			/*
			 * loop through all properties
			 */
			for (final PropertyDescriptor d : bi.getPropertyDescriptors()) {
				final Method getter = d.getReadMethod();

				if (present(getter)) {
					final Object value = getter.invoke(model, (Object[])null);
					final String propertyName = d.getName();
					
					/*
					 * check max length
					 * TODO: get type handler and convert url to string and than
					 * check for valid length
					 */
					if (value instanceof String) {
						final String stringValue = (String) value;

						checkProperty(clazz, fieldLengthError, propertyName, stringValue);
					} else {
						// try to convert property to string using the type handlers
						final String convertedValue = this.databaseSchemaInformation.callTypeHandler(clazz, propertyName, value, String.class);
						if (convertedValue != null) {
							checkProperty(clazz, fieldLengthError, propertyName, convertedValue);
						}
					}
				}
			}
			
			if (fieldLengthError.hasErrors()) {
				session.addError(id, fieldLengthError);
				log.debug("added fieldlengthError");
			} 
		} catch (final Exception ex) {
			log.error("could not introspect object of class '" + model.getClass().getSimpleName() + "'", ex);
		}
	}

	/**
	 * @param clazz
	 * @param fieldLengthError
	 * @param propertyName
	 * @param stringValue
	 */
	private void checkProperty(final Class<? extends Object> clazz, final FieldLengthErrorMessage fieldLengthError, final String propertyName, final String stringValue) {
		final int length = stringValue.length();
		final int maxLength = this.databaseSchemaInformation.getMaxColumnLengthForProperty(clazz, propertyName);

		if ((maxLength > 0) && (length > maxLength)) {
			fieldLengthError.addToFields(propertyName, maxLength);
		}
	}

	/**
	 * @param databaseSchemaInformation the databaseSchemaInformation to set
	 */
	public void setDatabaseSchemaInformation(final DatabaseSchemaInformation databaseSchemaInformation) {
		this.databaseSchemaInformation = databaseSchemaInformation;
	}
}
