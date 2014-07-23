package org.bibsonomy.webapp.view.constants;

public enum ViewLayout {

	BOOTSTRAP("bsjsp"),
	CLASSIC("jsp");
	
	private final String jspPath;

	private ViewLayout(final String jspPath) {
		this.jspPath = jspPath;
	}

	public String getJspPath() {
		return jspPath;
	}
}
