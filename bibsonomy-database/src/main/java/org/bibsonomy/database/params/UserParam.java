package org.bibsonomy.database.params;

public class UserParam extends GenericParam {

	private String groupingName;
	private String regex;

	public String getGroupingName() {
		return this.groupingName;
	}

	public void setGroupingName(String groupingName) {
		this.groupingName = groupingName;
	}

	public String getRegex() {
		return this.regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
}