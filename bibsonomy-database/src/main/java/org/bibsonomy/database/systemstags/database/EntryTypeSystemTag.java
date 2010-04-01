package org.bibsonomy.database.systemstags.database;

import org.bibsonomy.database.systemstags.SystemTag;

/**
 * @author dzo
 * @version $Id$
 */
public class EntryTypeSystemTag extends DatabaseSystemTag {

	private String entryType;

	/**
	 * @param entryType the entryType to set
	 */
	public void setEntryType(String entryType) {
		this.entryType = entryType;
	}

	/**
	 * @return the entryType
	 */
	public String getEntryType() {
		return entryType;
	}

	@Override
	public SystemTag newInstance() {
		return new EntryTypeSystemTag();
	}

}
