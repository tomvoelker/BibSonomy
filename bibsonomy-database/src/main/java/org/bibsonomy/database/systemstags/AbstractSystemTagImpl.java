package org.bibsonomy.database.systemstags;

import org.bibsonomy.model.Tag;

/**
 * @author sdo
 * @version $Id$
 */
public class AbstractSystemTagImpl implements SystemTag {

	private Tag tag;
	private String argument;
	private String name;


	/**
	 * Sets this instance's tag input representation and extracts tag's argument.
	 * Precondition:
	 *   Given tag is a system tag.
	 * @param tag as given by user
	 */
	public void setTag(final Tag tag) {
		this.tag = tag;
	}

	/**
	 * @return the tag
	 */
	public Tag getTag() {
		return this.tag;
	} 


	/**
	 * @return the value
	 */
	public String getArgument() {
		return this.argument;
	}

	/**
	 * @param value the value to set
	 */
	@Override
	public void setArgument(String argument) {
		this.argument = argument;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;		
	}

}
