package org.bibsonomy.webapp.util.spring.factorybeans;

import java.util.Arrays;

import org.bibsonomy.services.filesystem.extension.ExtensionChecker;
import org.bibsonomy.services.filesystem.extension.ListExtensionChecker;
import org.bibsonomy.services.filesystem.extension.WildcardExtensionChecker;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author dzo
 */
public class ExtensionCheckerFactoryBean implements FactoryBean<ExtensionChecker>{
	
	private String allowedExtensions;
	
	@Override
	public ExtensionChecker getObject() throws Exception {
		this.allowedExtensions = this.allowedExtensions.trim();
		
		if (WildcardExtensionChecker.WILDCARD.equals(this.allowedExtensions)) {
			return new WildcardExtensionChecker();
		}
		
		return new ListExtensionChecker(Arrays.asList(this.allowedExtensions.split(", ")));
	}

	@Override
	public Class<?> getObjectType() {
		return ExtensionChecker.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	/**
	 * @param allowedExtensions the allowedExtensions to set
	 */
	public void setAllowedExtensions(String allowedExtensions) {
		this.allowedExtensions = allowedExtensions;
	}

}
