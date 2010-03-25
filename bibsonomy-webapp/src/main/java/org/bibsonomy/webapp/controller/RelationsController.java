package org.bibsonomy.webapp.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.RelationsCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian Kramer
 * @version $Id$
 */
public class RelationsController extends SingleResourceListControllerWithTags implements MinimalisticController<RelationsCommand>{
	private static final Log LOGGER = LogFactory.getLog(RelationsController.class);
	private LogicInterface logic;

	/*
	 * the following concepts are unwanted on the relations page
	 * XXX: inject them using Spring?
	 */ 
	private static final String[] tagsToRemove = new String[]{
		"bookmarks_toolbar",
		"bookmarks_toolbar_folder",
		"forex",
		"from_internet_explorer",
		"how",
		"lesezeichen-symbolleiste",
		"personal_toolbar_folder",
		"the",
		"what"
	};
	
	@Override
	public RelationsCommand instantiateCommand() {	
		return new RelationsCommand();
	}

	@Override
	public View workOn(RelationsCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());

		// html format - retrieve tags and return HTML view
		if (command.getFormat().equals("html")) {
			/*
			 * request the concepts
			 */
			final List<Tag> tags = logic.getConcepts(Resource.class, GroupingEntity.ALL, null, null, null, ConceptStatus.ALL, 0, 50);

			for (final String string : tagsToRemove) {
				tags.remove(new Tag(string));
			}

			command.setTagRelations(tags);
		}

		this.endTiming();
		return Views.RELATIONS;
	}

	@Override
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

}
