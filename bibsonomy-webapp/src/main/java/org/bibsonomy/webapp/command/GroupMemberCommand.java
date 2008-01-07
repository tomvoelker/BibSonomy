package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean for members of a specified group
 * @author Stefan Stuetzer
 * @version $Id$
 */
public class GroupMemberCommand extends BaseCommand {
	
	String group;
	
	List<String> members = new ArrayList<String>();

	public String getGroup() {
		return this.group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public List<String> getMembers() {
		return this.members;
	}

	public void setMembers(List<String> members) {
		this.members = members;
	}
	
	public void addMember(String member) {
		members.add(member);
	}
	
	public int getCount () {
		return getMembers().size();
	}
}