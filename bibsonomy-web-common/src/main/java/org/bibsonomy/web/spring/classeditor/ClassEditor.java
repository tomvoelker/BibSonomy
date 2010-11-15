package org.bibsonomy.web.spring.classeditor;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;

/**
 * extends the functionality of the {@link org.springframework.beans.propertyeditors.ClassEditor}
 * by allowing to use simple string representation (found in the
 * {@link ResourceFactory#RESOURCE_CLASSES_BY_NAME} map) for {@link Resource} classes.
 * 
 * e.g. using the paramater value 'bookmark' sets a {@link Bookmark} class
 * 
 * @author dzo
 * @version $Id$
 */
public class ClassEditor extends org.springframework.beans.propertyeditors.ClassEditor {
	
	@Override
	public void setAsText(final String text) throws IllegalArgumentException {
		if (present(text)) {
			final Class<? extends Resource> clazz = ResourceFactory.getResourceClass(text);
			
			if (present(clazz)) {
				this.setValue(clazz);
				return; // got value; nothing to do
			}
		}
		
		super.setAsText(text);
	}
	
	@Override
	public String getAsText() {
		final Class<?> clazz = (Class<?>) getValue();
		if (present(clazz) && clazz.isAssignableFrom(Resource.class)) {
			@SuppressWarnings("unchecked") // checked; have a look at the if statement
			final Class<? extends Resource> resourceClass = (Class<? extends Resource>) clazz;
			return ResourceFactory.getResourceName(resourceClass);
		}
		
		return super.getAsText();
	}
}
