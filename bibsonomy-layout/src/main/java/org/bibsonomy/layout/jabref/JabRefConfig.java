package org.bibsonomy.layout.jabref;

/**
 * configuration file
 *
 * @author dzo
 */
public class JabRefConfig {
	/** Configured by the setter: the path where the user layout files are. */
	private String userLayoutFilePath;
	
	/** Can be configured by the setter: the path where the default layout files are. */
	private String defaultLayoutFilePath = "org/bibsonomy/layout/jabref";

	/**
	 * @return the userLayoutFilePath
	 */
	public String getUserLayoutFilePath() {
		return this.userLayoutFilePath;
	}

	/**
	 * @param userLayoutFilePath the userLayoutFilePath to set
	 */
	public void setUserLayoutFilePath(String userLayoutFilePath) {
		this.userLayoutFilePath = userLayoutFilePath;
	}

	/**
	 * @return the defaultLayoutFilePath
	 */
	public String getDefaultLayoutFilePath() {
		return this.defaultLayoutFilePath;
	}

	/**
	 * @param defaultLayoutFilePath the defaultLayoutFilePath to set
	 */
	public void setDefaultLayoutFilePath(String defaultLayoutFilePath) {
		this.defaultLayoutFilePath = defaultLayoutFilePath;
	}
}
