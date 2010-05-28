package org.bibsonomy.webapp.command;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bibsonomy.webapp.command.actions.EditTagsCommand;
import org.bibsonomy.webapp.command.actions.RelationsEditCommand;


/**
 * @author hba
 * @version $Id$
 */
public class EditTagsPageViewCommand extends ResourceViewCommand {

	private final Date date = new Date();
	private static final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private final EditTagsCommand editTags;
	
	private final RelationsEditCommand relationsEdit;
	
	/**
	 * which action is wanted ?
	 * 0 pure edit_tags page display
	 * 1 edit the tags
	 * 2 add/del relations
	 */
	private int forcedAction = 0;
	
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
		editTags = new EditTagsCommand();
		relationsEdit = new RelationsEditCommand();
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

	/**
	 * @return the editTags
	 */
	public EditTagsCommand getEditTags() {
		return editTags;
	}

	/**
	 * @return the relationsEdit
	 */
	public RelationsEditCommand getRelationsEdit() {
		return relationsEdit;
	}

	/**
	 * @param forcedAction the forcedAction to set
	 */
	public void setForcedAction(int forcedAction) {
		this.forcedAction = forcedAction;
	}

	/**
	 * @return the forcedAction
	 */
	public int getForcedAction() {
		return forcedAction;
	}

}
