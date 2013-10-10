package org.bibsonomy.util.upload.impl;

import java.util.Collection;

import org.bibsonomy.services.filesystem.extension.ExtensionChecker;
import org.bibsonomy.util.Sets;

/**
 * {@link ExtensionChecker} which always returns true
 * 
 * @author dzo
 * @version $Id$
 */
public class WildcardExtensionChecker implements ExtensionChecker {

	/**
	 * the wildcard
	 */
	public static final String WILDCARD = "*";

	@Override
	public boolean checkExtension(String extension) {
		return true;
	}
	
	@Override
	public Collection<String> getAllowedExtensions() {
		return Sets.asSet(WILDCARD);
	}
}
