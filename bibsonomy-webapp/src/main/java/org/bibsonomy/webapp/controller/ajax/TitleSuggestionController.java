package org.bibsonomy.webapp.controller.ajax;

import java.util.List;

import org.bibsonomy.common.Pair;
import org.bibsonomy.webapp.command.ajax.TitleSuggestionCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.TitleSuggestionLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * TitleSuggestionController to generate the JSON for the SuggestTree autocompletion.
 * 
 * @author nilsraabe
 * @version $Id$
 */
public class TitleSuggestionController implements MinimalisticController<TitleSuggestionCommand> {

	private TitleSuggestionLogic titleSuggestionLogic;
		
	@Override
	public TitleSuggestionCommand instantiateCommand() {
		return new TitleSuggestionCommand();
	}

	@Override
	public View workOn(TitleSuggestionCommand command) {
		
		String postPrefix = command.getPostPrefix();
		
		List<Pair<String,Integer>> bibtexSuggestion = titleSuggestionLogic.getPostSuggestion(postPrefix);
		
		command.setPostSuggestionTitle(bibtexSuggestion);
		
		return Views.JSON;
	}

	/**
	 * @return the titleSuggestionLogic
	 */
	public TitleSuggestionLogic getTitleSuggestionLogic() {
		return this.titleSuggestionLogic;
	}

	/**
	 * @param titleSuggestionLogic the titleSuggestionLogic to set
	 */
	public void setTitleSuggestionLogic(TitleSuggestionLogic titleSuggestionLogic) {
		this.titleSuggestionLogic = titleSuggestionLogic;
	}
}
