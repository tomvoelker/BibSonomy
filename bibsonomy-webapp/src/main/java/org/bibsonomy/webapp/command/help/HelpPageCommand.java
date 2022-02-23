/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.webapp.command.help;

import java.util.SortedSet;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.services.help.HelpSearchResult;
import org.bibsonomy.webapp.command.actions.DownloadFileCommand;

/**
 * The command for the help pages and images.
 *
 * @author Johannes Blum
 */
@Getter
@Setter
public class HelpPageCommand extends DownloadFileCommand {

	private static final long serialVersionUID = -1480991183960187327L;

	/** The requested help page. */
	private String helpPage;

	/** the help page title */
	private String helpPageTitle;
	
	/** The main content of the help page. */
	private String content;
	
	/** The content of the sidebar. */
	private String sidebar;
	
	/** <code>true</code> if the requested help page could not be found. */
	private boolean pageNotFound = false;

	/** The project theme */
	private String theme;

	/** The help theme */
	private String helpTheme;
	
	/** the language */
	private String language;
	
	private SortedSet<HelpSearchResult> searchResults;
	
	private String search;
	
}
