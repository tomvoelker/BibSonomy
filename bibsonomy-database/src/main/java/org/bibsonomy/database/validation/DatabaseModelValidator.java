package org.bibsonomy.database.validation;

import static org.bibsonomy.util.ValidationUtils.present;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.errors.FieldLengthErrorMessage;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DatabaseSchemaInformation;

/**
 * @author dzo
 * @version $Id$
 * @param <T> the model to validate
 */
public class DatabaseModelValidator<T> {
	private static final Log log = LogFactory.getLog(DatabaseModelValidator.class);
	
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
			 * if there are any performance issues, their cause might be here
			 */
			for (final PropertyDescriptor d : bi.getPropertyDescriptors()) {			
				final Method getter = d.getReadMethod();

				if (present(getter)) {					
					final Object value = getter.invoke(model, (Object[])null);

					/*
					 * check max length
					 */
					if (value instanceof String) {
						final String stringValue = (String) value;

						final int length = stringValue.length();
						final String propertyName = d.getName();
						final int maxLength = DatabaseSchemaInformation.getMaxColumnLengthForProperty(clazz, propertyName);

						if ((maxLength > 0) && (length > maxLength)) {
							fieldLengthError.addToFields(propertyName, maxLength);
						}
					}
				}
			}
			
			if (fieldLengthError.hasErrors()) {
				session.addError(id, fieldLengthError);
			}
		} catch (final Exception ex) {
			log.error("could not introspect object of class 'user'", ex);
		}
	}
}
