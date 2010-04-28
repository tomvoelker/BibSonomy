package org.bibsonomy.webapp.command;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author hba
 * @version $Id$
 */
public class EditTagsPageViewCommand extends ResourceViewCommand {

	private final Date date = new Date();
	private static final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * the group whose resources are requested 
	 * FIXME: a group? This is a ConceptsCommand!
	 */
	private ConceptsCommand concepts;

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
	public void setConcepts(final ConceptsCommand concepts) {
		this.concepts = concepts;
	}
	
	/**
	 * called by the view
	 * @return date as string (formated by the dateformator)
	 */
	public String getDate(){
		return dateformat.format(date);
	}

}
