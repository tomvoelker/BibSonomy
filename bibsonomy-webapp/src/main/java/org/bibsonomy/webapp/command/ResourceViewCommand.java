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
package org.bibsonomy.webapp.command;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.common.enums.Duplicates;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.TagsType;
import org.bibsonomy.model.Resource;

/**
 * command with fields for the resource lists.
 * 
 * is mainly a container for two list commands (bookmarks & publications), the requested username
 * and a list of tags associated with the bookmarks / publications
 * 
 * @see BaseCommand
 * @author Jens Illig
 * @author Dominik Benz
 */
@Getter
@Setter
public class ResourceViewCommand extends BaseCommand {
	/** default value for sortPage */
	public static final String DEFAULT_SORTPAGE = "date";
	/** default value for sortPageOrder */
	public static final String DEFAULT_SORTPAGEORDER = "desc";
	
	private TagCloudCommand tagcloud = new TagCloudCommand();
	
	private Date startDate;
	private Date endDate;

	/** name of the user whose resources are requested */
	private String requestedUser;
	private Set<Class<? extends Resource>> resourcetype = new HashSet<>();

	/** for queries for specific kinds of tags */
	private TagsType tagstype;
	
	private String format = "html";
	/** if format="layout", here the requested layout is stored */
	private String layout;
	private boolean formatEmbedded;
	/** @see PublicationViewCommand#isSkipDummyValues() */
	private boolean skipDummyValues;
	
	// TODO: could be a list of SortKeys
	private String sortPage = DEFAULT_SORTPAGE;
	// TODO: could be a list of SortOrders
	private String sortPageOrder = DEFAULT_SORTPAGEORDER;
	
	/** show duplicates? */
	private Duplicates duplicates = Duplicates.YES;

	private boolean notags = false;

	/** For some pages we need to store the referer to send the user back to that page. */
	private String referer;
	
	/** if true, the posts and tags of the requested user will be ranked / highlighted according to the logged-in user */
	private boolean personalized = false;
	
	/** retrieve only tags without resources */
	private boolean restrictToTags = false;

	/** callback function for JSON outputs */
	private String callback = "";	
	
	/** filter group resources  */
	private FilterEntity filter;

	/** whether the result should be presented as a download */
	private boolean download = false;
	private boolean generatedBibtexKeys;
	/** how to render person names in bibtex export (true <=> person names in "First Last" order) */
	private boolean firstLastNames;
	/** name of a spring-registered urlGenerator (for customized biburl fields from vufind) */
	private String urlGenerator = "default";


	/**
	 * @return The requested format.
	 * 
	 */
	public String getFormat() {
		if (present(this.format)) {
			return this.format;
		}
		
		/*
		 * the default is html
		 * */
		return "html";
	}

}