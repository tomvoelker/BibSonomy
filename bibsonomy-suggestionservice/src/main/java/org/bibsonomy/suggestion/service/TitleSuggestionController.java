/**
 * BibSonomy - A blue social bookmark and publication sharing system.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
