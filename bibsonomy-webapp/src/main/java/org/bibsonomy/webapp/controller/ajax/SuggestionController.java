package org.bibsonomy.webapp.controller.ajax;

import java.util.List;

import org.bibsonomy.common.Pair;
import org.bibsonomy.logic.SuggestionLogic;
import org.bibsonomy.webapp.command.ajax.SuggestionCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * SuggestionController to generate the JSON for the SuggestTree autocompletion.
 * 
 * @author nilsraabe
 * @version $Id$
 */
public class SuggestionController implements MinimalisticController<SuggestionCommand> {

	private SuggestionLogic suggestionLogic;
		
	@Override
	public SuggestionCommand instantiateCommand() {
		return new SuggestionCommand();
	}

	@Override
	public View workOn(SuggestionCommand command) {
		
		String postPrefix = command.getPostPrefix();
		
		List<Pair<String,Integer>> postSuggestion = suggestionLogic.getPostSuggestion(postPrefix);
		
		command.setPostSuggestionTitle(postSuggestion);
		
		return Views.JSON;
	}

	/**
	 * @return the titleSuggestionLogic
	 */
	public SuggestionLogic getSuggestionLogic() {
		return this.suggestionLogic;
	}

	/**
	 * @param suggestionLogic the titleSuggestionLogic to set
	 */
	public void setSuggestionLogic(SuggestionLogic suggestionLogic) {
		this.suggestionLogic = suggestionLogic;
	}
}
