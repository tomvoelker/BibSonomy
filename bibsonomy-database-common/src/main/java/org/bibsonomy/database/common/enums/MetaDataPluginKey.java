package org.bibsonomy.database.common.enums;

/**
 * @author clemensbaier
 */
public enum MetaDataPluginKey {
	/**
	 * a publication was copied
	 */
	COPY_PUBLICATION("COPY_PUBLICATION"),
	
	/**
	 * a bookmark was copied
	 */
	COPY_BOOKMARK("COPY_BOOKMARK");
	
	private String name;
	
	private MetaDataPluginKey(final String name) {
		this.name =  name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
}
