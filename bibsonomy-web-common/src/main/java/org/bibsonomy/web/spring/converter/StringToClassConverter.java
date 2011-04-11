package org.bibsonomy.web.spring.converter;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.ClassUtils;

/**
 * @author dzo
 * @version $Id$
 */
public class StringToClassConverter implements Converter<String, Class<?>> {

	private ClassLoader loader = ClassUtils.getDefaultClassLoader();
	
	@Override
	public Class<?> convert(String text) {
		if (present(text)) {
			text = text.trim();
			final Class<? extends Resource> clazz = ResourceFactory.getResourceClass(text);
			
			if (present(clazz)) {
				return clazz;
			}
			
			return ClassUtils.resolveClassName(text, this.loader);
		}
		return null;
	}

	/**
	 * @param loader the loader to set
	 */
	public void setLoader(final ClassLoader loader) {
		this.loader = loader;
	}
}
