package org.bibsonomy.layout.jabref;


/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class JabrefLayout {

	private final String name;
	public String displayName;
	public String description;
	public String directory;
	public String baseFileName;
	public String mimeType;
	public String extension;

	public JabrefLayout(String name) {
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

