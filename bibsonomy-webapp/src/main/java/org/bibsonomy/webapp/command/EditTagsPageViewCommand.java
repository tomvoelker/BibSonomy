package org.bibsonomy.webapp.command;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bibsonomy.model.User;


/**
 * @author hba
 * @version $Id$
 */
public class EditTagsPageViewCommand extends ResourceViewCommand {

	private User user;
	private String userName = "test";
	private final Date date = new Date();
	private static final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * the group whose resources are requested 
	 * FIXME: a group? This is a ConceptsCommand!
	 */
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
	 * @return The name of the user.
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
