package org.bibsonomy.webapp.command;

import org.bibsonomy.model.User;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author hba
 *
 */
public class EditTagsPageViewCommand extends ResourceViewCommand {

	private User user;
	private String userName = "test";
	private Date date = new Date();
	private SimpleDateFormat dateformat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

	/** the group whode resources are requested */
	private ConceptsCommand concepts = null;


	/**
	 * 
	 */
	public EditTagsPageViewCommand() {
		concepts = new ConceptsCommand(this);
	}
	
	/**
	 * @return the concept
	 */
	public ConceptsCommand getConcepts() {
		return this.concepts;
	}

	/**
	 * @param concepts
	 */
	public void setConcepts(ConceptsCommand concepts) {
		this.concepts = concepts;
	}
	
	/**
	 * @return user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * @return
	 */
	public String getUserName(){
		return this.userName;
	}
	
	/**
	 * @param user
	 */
	public void setUserName(String userName){
		this.userName = userName;
	}
	
	public String getDate(){
		return dateformat.format(date);
	}

}
