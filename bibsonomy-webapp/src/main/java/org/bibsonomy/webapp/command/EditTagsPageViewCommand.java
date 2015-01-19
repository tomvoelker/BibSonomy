/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command;

import org.bibsonomy.webapp.command.actions.EditTagsCommand;
import org.bibsonomy.webapp.command.actions.RelationsEditCommand;


/**
 * @author hba
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
