package org.bibsonomy.webapp.util.spring.i18n;

import java.util.Collection;
import java.util.Locale;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * some helper methods for the MessageBundleSource
 *
 * @author dzo
 */
public class ExposedResourceMessageBundleSource extends ReloadableResourceBundleMessageSource {
	
	/**
	 * get all message keys
	 * @param locale
	 * @return the message keys for the specified locale
	 */
	public Collection<Object> getAllMessageKeys(final Locale locale) {
		return getMergedProperties(locale).getProperties().keySet();
	}
}
