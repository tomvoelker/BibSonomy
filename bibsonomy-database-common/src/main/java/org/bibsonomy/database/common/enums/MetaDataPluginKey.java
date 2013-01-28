package org.bibsonomy.database.common.enums;

public enum MetaDataPluginKey {
	COPY_PUBLICATION("COPY_PUBLICATION"),	
	COPY_BOOKMARK("COPY_BOOKMARK");
	
	private String name;
	
	private MetaDataPluginKey(final String name) {
		this.name =  name;
	}
	
	public String getName() {
		return this.name;
	}
}
