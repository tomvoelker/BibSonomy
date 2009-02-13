package org.bibsonomy.layout;


/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public abstract class Layout {

	/**
	 * The name of the layout (used as identifier in the URL).
	 */
	protected final String name;
	/**
	 * The name shown to the user.
	 */
	protected String displayName;
	/**
	 * A short textual description.
	 */
	protected String description;
	/**
	 * The mime type of the rendered file.
	 */
	protected String mimeType;
	/**
	 * The extension of the rendered file.
	 */
	protected String extension;

	public Layout(final String name) {
		super();
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		return name + "(" + 
		displayName + ", '" + 
		description + "', " + 
		mimeType + ", " + 
		extension + ")";
	}

}
