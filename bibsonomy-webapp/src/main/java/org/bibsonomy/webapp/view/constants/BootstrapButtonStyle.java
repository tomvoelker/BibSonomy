/**
 * 
 */
package org.bibsonomy.webapp.view.constants;

/**
 * @author Sebastian Böttger <boettger@cs.uni-kassel.de>
 */
public enum BootstrapButtonStyle {

	
	defaultStyle("btn-default"),
	primary("btn-primary"),
	success("btn-success"),
	info("btn-info"),
	warning("btn-warning"),
	danger("btn-danger"),
	link("btn-link");
	
	public final String cssClassName;
	

	private BootstrapButtonStyle(String value) {
		this.cssClassName = value;
	}

	public String getCssClassName() {
		return this.cssClassName;
	}


	
}
