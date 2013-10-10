package org.bibsonomy.suggestion.service;

import java.util.SortedSet;

import org.bibsonomy.logic.SuggestionLogic;
import org.bibsonomy.model.Suggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author dzo
 *
 */
@Controller
public class TitleSuggestionController {

	@Autowired
	private SuggestionLogic logic;
	
	/**
	 * returns the suggestions
	 * @param prefix
	 * @return the suggestions for the specified prefix
	 */
	@RequestMapping("/")
	@ResponseBody
	public SortedSet<Suggestion> getTitleSuggestion(@RequestParam("prefix") String prefix) {
		return logic.getPostSuggestion(prefix);
	}
}
