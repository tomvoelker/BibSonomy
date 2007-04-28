package org.bibsonomy.database.params;

import java.util.List;

public class UserParam extends GenericParam {

	private String groupingName;
	private String regex;
	private List<Integer> groups;

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

	public List<Integer> getGroups() {
		return this.groups;
	}

	public void setGroups(List<Integer> groups) {
		this.groups = groups;
	}
}