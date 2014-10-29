/**
 * 
 */
package org.bibsonomy.webapp.view.constants;

/**
 * @author Sebastian Böttger <boettger@cs.uni-kassel.de>
 */
public enum BootstrapFeedbackStyle {

	
	primary("primary"),
	success("success"),
	info("info"),
	warning("warning"),
	danger("danger");
	
	public final String cssClassName;
	

	private BootstrapFeedbackStyle(String value) {
		this.cssClassName = value;
	}

	public String getCssClassName() {
		return this.cssClassName;
	}


	
}
