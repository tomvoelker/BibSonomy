package org.bibsonomy.services.filesystem.extension;

import java.util.Collection;

import org.bibsonomy.util.StringUtils;

/**
 * @author dzo
 * @version $Id$
 */
public class ListExtensionChecker implements ExtensionChecker {
	
	private final Collection<String> allowedExtensions;
	
	/**
	 * @param allowedExtensions all allowed extensions
	 */
	public ListExtensionChecker(Collection<String> allowedExtensions) {
		super();
		this.allowedExtensions = allowedExtensions;
	}

	@Override
	public boolean checkExtension(String extension) {
		return StringUtils.matchExtension(extension, this.allowedExtensions);
	}

	/**
	 * @return the allowedExtensions
	 */
	@Override
	public Collection<String> getAllowedExtensions() {
		return allowedExtensions;
	}

}
