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
package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.systemstags.SystemTagsExtractor;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.search.BibTexKeySystemTag;
import org.bibsonomy.database.systemstags.search.UserSystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.webapp.command.BibtexkeyCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for BibtexKey
 * - /bibtexkey/BIBKEY
 * - /bibtexkey/BIBKEY/USER
 *
 * @author Flori, Dominik Benz
 */
public class BibtexkeyPageController extends SingleResourceListController implements MinimalisticController<BibtexkeyCommand> {
	private static final Log log = LogFactory.getLog(BibtexkeyPageController.class);

	@Override
	public View workOn(BibtexkeyCommand command) {
		final String format = command.getFormat();
		this.startTiming(format);
		
		if (!present(command.getRequestedKey())) {
			throw new MalformedURLSchemeException("error.bibtexkey_no_key");
		}
		
		// add bibtexkey as the only systemtag (sys:user:USERNAME is handeled below)
		command.getRequestedTagsList().clear();
		command.getRequestedTagsList().add(SystemTagsUtil.buildSystemTagString(BibTexKeySystemTag.NAME, command.getRequestedKey()));
		
		// default grouping entity / grouping name
		GroupingEntity groupingEntity = GroupingEntity.ALL;
		String groupingName = null;
				
		// check for systemtag sys:user:USERNAME
		List<String> sysTags = SystemTagsExtractor.extractSearchSystemTagsFromString(command.getRequestedTags(), TagUtils.getDefaultListDelimiter());
		final String systemTagUser = extractSystemTagUser(sysTags);
		if (systemTagUser != null) {
			command.setRequestedUser(systemTagUser);
		}

		// check if user was given via /bibtexkey/KEY/USERNAME or systemtag
		if (present(command.getRequestedUser())) {
			groupingEntity = GroupingEntity.USER;
			groupingName = command.getRequestedUser();
		}
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(format, command.getResourcetype())) {
			setList(command, resourceType, groupingEntity, groupingName, command.getRequestedTagsList(), null, null, command.getScope(),null, null, command.getStartDate(), command.getEndDate(), command.getListCommand(resourceType).getEntriesPerPage());
			postProcessAndSortList(command, resourceType);
		}
		
		// html format - fetch tags and return HTML view
		if (format.equals("html")) {
			// tags
			setTags(command, BibTex.class, groupingEntity, groupingName, null, command.getRequestedTagsList(), null, 1000, null, command.getScope());
			if (command.getTagcloud().getTags().size() > 999) {
				log.error("Found bibtex entries by bibtex keys with more than 1000 tags assigned!!");
			}
			// pagetitle
			String pageTitle = "bibtexkey :: " + command.getRequestedKey();
			if (GroupingEntity.USER.equals(groupingEntity)) {
				pageTitle += " :: " + command.getRequestedUser();
			}
			command.setPageTitle(pageTitle);
			this.endTiming();
			return Views.BIBTEXKEYPAGE;	
		}
		
		// export - return the appropriate view
		this.endTiming();
		return Views.getViewByFormat(format);
	}

	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	@Override
	public BibtexkeyCommand instantiateCommand() {
		return new BibtexkeyCommand();
	}
	
	/**
	 * Check if 
	 * @param sysTags
	 * 		- a list of system tags (strings)
	 * @return
	 * 		- the value of the user system tag, if present (i.e. USERNAME if sys:user:USERNAME is present, 
	 *        null otherwise 
	 */
	private String extractSystemTagUser(List<String> sysTags) {
		for (String sysTag : sysTags) {
			if (SystemTagsUtil.isSystemTag(sysTag, UserSystemTag.NAME)) {
				return SystemTagsUtil.extractArgument(sysTag);
			}
		}
		return null;
	}

}
