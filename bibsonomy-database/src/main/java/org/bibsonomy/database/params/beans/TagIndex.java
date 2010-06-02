package org.bibsonomy.database.params.beans;

/**
 * This class holds the tagname and the corresponding index and join-index.
 * While the name of the class might be misleading, it can be used for tags as
 * well for concepts.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
@Deprecated
public class TagIndex {

	/** This name can be both a name of a tag or concept. */
	private final String tagName;
	/** A index to produce a self-join, like t1...=t2..., t2...=t3..., etc. */
	private final int index;

	/**
	 * Creates a new instance with the given namen an start index.
	 * 
	 * @param tagName
	 * @param index
	 */
	public TagIndex(final String tagName, final int index) {
		this.tagName = tagName;
		this.index = index;
	}

	/**
	 * @return the tag's name
	 */
	public String getTagName() {
		return this.tagName;
	}

	/**
	 * @return current index
	 */
	public int getIndex() {
		return this.index;
	}

	/**
	 * Retrieves the join-index which is always the current index plus one.
	 * 
	 * Hint: a call to this function isn't idempotent, i.e. it changes the value
	 * of the index.
	 * 
	 * @return current index plus one
	 */
	public int getIndex2() {
		return (this.index + 1);
	}
}