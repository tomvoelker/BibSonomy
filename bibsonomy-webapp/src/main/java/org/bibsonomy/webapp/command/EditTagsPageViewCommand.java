package org.bibsonomy.webapp.command;

import org.bibsonomy.webapp.command.actions.EditTagsCommand;
import org.bibsonomy.webapp.command.actions.RelationsEditCommand;


/**
 * @author hba
 * @version $Id$
 */
public class EditTagsPageViewCommand extends ResourceViewCommand {
	
	private final EditTagsCommand editTags;
	
	private final RelationsEditCommand relationsEdit;
	
	/**
	 * the concepts of the user
	 */
	private ConceptsCommand concepts;

	private int updatedRelationsCount = 0;
	
	private int updatedTagsCount = 0;
	
	/**
	 * which action is wanted ?
	 * 0 pure editTags page display
	 * 1 edit the tags
	 * 2 add/del relations
	 * FIXME: use an enum
	 */
	private int forcedAction = 0;
	
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
	public void setForcedAction(final int forcedAction) {
		this.forcedAction = forcedAction;
	}

	/**
	 * @return the forcedAction
	 */
	public int getForcedAction() {
		return forcedAction;
	}

	/**
	 * @param updatedRelationsCount the updatedRelationsCount to set
	 */
	public void setUpdatedRelationsCount(final int updatedRelationsCount) {
		this.updatedRelationsCount = updatedRelationsCount;
	}

	/**
	 * @return the updatedRelationsCount
	 */
	public int getUpdatedRelationsCount() {
		return updatedRelationsCount;
	}

	/**
	 * @param updatedTagsCount the updatedTagsCount to set
	 */
	public void setUpdatedTagsCount(final int updatedTagsCount) {
		this.updatedTagsCount = updatedTagsCount;
	}

	/**
	 * @return the updatedTagsCount
	 */
	public int getUpdatedTagsCount() {
		return updatedTagsCount;
	}

}
