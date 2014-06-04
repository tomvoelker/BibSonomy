package org.bibsonomy.scraper;

/**
 * object that holds data for a scraper test
 *
 * @author dzo
 */
public class ScraperTestData {
	private String testId;
	private String scraperClassName;
	private String url;
	private String selection;
	private String expectedBibTeX;
	private boolean enabled = true;
	
	private String description;
	private String bibTeXFileName;
	
	/**
	 * @return the testId
	 */
	public String getTestId() {
		return this.testId;
	}

	/**
	 * @param testId the testId to set
	 */
	public void setTestId(String testId) {
		this.testId = testId;
	}

	/**
	 * @return the scraperClassName
	 */
	public String getScraperClassName() {
		return this.scraperClassName;
	}

	/**
	 * @param scraperClassName the scraperClassName to set
	 */
	public void setScraperClassName(String scraperClassName) {
		this.scraperClassName = scraperClassName;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}
	
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * @return the selection
	 */
	public String getSelection() {
		return this.selection;
	}
	
	/**
	 * @param selection the selection to set
	 */
	public void setSelection(String selection) {
		this.selection = selection;
	}
	
	/**
	 * @return the expectedBibTeX
	 */
	public String getExpectedBibTeX() {
		return this.expectedBibTeX;
	}
	
	/**
	 * @param expectedBibTeX the expectedBibTeX to set
	 */
	public void setExpectedBibTeX(String expectedBibTeX) {
		this.expectedBibTeX = expectedBibTeX;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the bibTeXFileName
	 */
	public String getBibTeXFileName() {
		return this.bibTeXFileName;
	}

	/**
	 * @param bibTeXFileName the bibTeXFileName to set
	 */
	public void setBibTeXFileName(String bibTeXFileName) {
		this.bibTeXFileName = bibTeXFileName;
	}
}
