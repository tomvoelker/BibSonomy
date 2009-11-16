package org.bibsonomy.lucene.param;

@Deprecated
public enum RecordType {

	Bookmark(0),
	BibTex(1);
	
	
	private final int id;

	private RecordType(final int id) {
		this.id = id;
	}

	/**
	 * @return the constant value behind the symbol
	 */
	public int getId() {
		return this.id;
	}

	
}
