/**
 * 
 */
package org.bibsonomy.webapp.view.constants;

/**
 * @author Sebastian Böttger <boettger@cs.uni-kassel.de>
 * 
 */
public enum BootstrapFeedbackSize {

	/** */
	large("-lg"),

	/** */
	defaultSize(""),

	/** */
	small("-sm"),

	/** */
	xsmall("-xs"),

	/** block level button (100% width) */
	block("btn-lg btn-block");

	/**
	 * css class name for sizing buttons in bootstrap
	 */
	public final String cssClassName;

	private BootstrapFeedbackSize(final String value) {
		this.cssClassName = value;
	}

	/**
	 * 
	 * @return css class name
	 */
	public String getCssClassName() {
		return this.cssClassName;
	}
}
