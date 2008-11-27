package org.bibsonomy.webapp.command;

import java.util.List;
import org.bibsonomy.model.Group;


/**
 * Bean for list of groups.
 * 
 * @author Folke Eisterlehner
 */
public class GroupsListCommand extends BaseCommand {
	private List<Group> list;
	
	// dirty hack: alphabet for direct access in group list
	private String strAlphabet = "#ABCDEFGHIJKLMNOPQRSTUVWXYZα"; 
	private char[] alphabet = strAlphabet.toCharArray();

	/**
	 * @return the alphabet
	 */
	public String getStrAlphabet() {
		return this.strAlphabet;
	}

	/**
	 * @return the alphabet
	 */
	public char[] getAlphabet() {
		return this.alphabet;
	}
	
	/**
	 * @return the sublistlist on the current page
	 */
	public List<Group> getList() {
		return this.list;
	}
	/**
	 * @param list the sublistlist on the current page
	 */
	public void setList(List<Group> list) {
		this.list = list;
	}	
}