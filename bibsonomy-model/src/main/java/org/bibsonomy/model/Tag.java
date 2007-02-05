package org.bibsonomy.model;

/**
 * This class represents a tag.
 */
public class Tag {

	private int id;
	private final String name;
	private String stem;
	private int count;

	public Tag(final String name) {
		this.name = name;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getName() {
		return this.name;
	}

	public String getStem() {
		return this.stem;
	}

	public void setStem(String stem) {
		this.stem = stem;
	}

	@Override
	public String toString() {
		return this.id + " '" + this.name + "' '" + this.stem + "' " + this.count;
	}
}