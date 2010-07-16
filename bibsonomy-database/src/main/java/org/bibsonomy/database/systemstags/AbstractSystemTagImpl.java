package org.bibsonomy.database.systemstags;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author sdo
 * @version $Id$
 */
public abstract class AbstractSystemTagImpl implements SystemTag {
	protected static final Log log = LogFactory.getLog(SystemTag.class);

	private String argument;

	/**
	 * @param argument the argument to set
	 */
	@Override
	public void setArgument(String argument) {
		this.argument = argument;
	}

	@Override
	public String getArgument() {
		return this.argument;
	}
	
	@Override
	public Boolean isInstance(final String tagName) {
	    // in general a systemTag must have a prefix and an argument
	    return SystemTagsUtil.hasPrefixNameAndArgument(tagName);
	}
}
