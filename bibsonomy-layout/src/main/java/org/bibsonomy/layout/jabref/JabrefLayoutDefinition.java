package org.bibsonomy.layout.jabref;


/**
 * Represents an entry of a jabref layout definition XML file according to
 * JabrefLayoutDefinition.xsd.  
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class JabrefLayoutDefinition {

	/**
	 * The name of the layout (used as identifier in the URL).
	 */
	private final String name;
	/**
	 * The name shown to the user.
	 */
	public String displayName;
	/**
	 * A short textual description.
	 */
	public String description;
	/**
	 * If the layout files are in a subdirectory of the layout directory, the name of the directory.
	 */
	public String directory;
	/**
	 * The base file name, most often equal to {@link #name}.
	 */
	public String baseFileName;
	/**
	 * The mime type of the rendered file.
	 */
	public String mimeType;
	/**
	 * The extension of the rendered file.
	 */
	public String extension;

	public JabrefLayoutDefinition(String name) {
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
	public String getDirectory() {
		return directory;
	}
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	public String getBaseFileName() {
		return baseFileName;
	}
	public void setBaseFileName(String baseFileName) {
		this.baseFileName = baseFileName;
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
		directory + "/" + 
		baseFileName + ", " + 
		mimeType + ", " + 
		extension + ")";
	}

}

