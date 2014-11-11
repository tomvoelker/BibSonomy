/**
 * 
 */
package org.bibsonomy.webapp.view.constants;

/**
 * @author Sebastian Böttger <boettger@cs.uni-kassel.de>
 */
public enum BootstrapAlertStyle {

	/** green */
	defaultStyle("alert-success"),
	
	/** green */
	success("alert-success"),
	
	/** blue */
	info("alert-info"),
	
	/** yellow */
	warning("alert-warning"),
	
	/** red */
	danger("alert-danger");
	
	/**
	 * css class name, which can be used to style alerts
	 */
	public final String cssClassName;
	

	private BootstrapAlertStyle(String value) {
		this.cssClassName = value;
	}

	/**
	 * 
	 * @return cssClassName
	 */
	public String getCssClassName() {
		return this.cssClassName;
	}
	
}