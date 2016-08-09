/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
public class ResourceViewCommand extends BaseCommand {
	/** default value for sortPage */
	public static final String DEFAULT_SORTPAGE = "date";
	/** default value for sortPageOrder */
	public static final String DEFAULT_SORTPAGEORDER = "desc";
	
	private TagCloudCommand tagcloud = new TagCloudCommand();
	
	private Date startDate;
	private Date endDate;
	
	private String requestedUser;
	private Set<Class<? extends Resource>> resourcetype = new HashSet<Class<? extends Resource>>();
	
	private TagsType tagstype; // for queries for specific kinds of tags
	
	private String format = "html"; 
	private String layout; // if format="layout", here the requested layout is stored
	private boolean formatEmbedded; // 
	private boolean skipDummyValues; // 
	
	// TODO: could be a list of SortKeys
	private String sortPage = DEFAULT_SORTPAGE;
	// TODO: could be a list of SortOrders
	private String sortPageOrder = DEFAULT_SORTPAGEORDER;
	
	/** show duplicates? */
	private Duplicates duplicates = Duplicates.YES;

	private boolean notags = false;

	/**
	 * For some pages we need to store the referer to send the user back
	 * to that page.
	 */
	private String referer;
	
	/**
	 * if true, the posts and tags of the requested user will be ranked / highlighted
	 * according to the logged-in user 
	 */
	private boolean personalized = false;
	
	/** retrieve only tags without resources */
	private boolean restrictToTags = false;

	/** callback function for JSON outputs */
	private String callback = "";	
	
	/** filter group resources  */
	private FilterEntity filter;
	
	private boolean download = false;
	private boolean generatedBibtexKeys;
	private boolean firstLastNames;
	private String urlGenerator = "default";

	
	/**
	 * @return name of the user whose resources are requested
	 */
	public String getRequestedUser() {
		return this.requestedUser;
	}
	/**
	 * @param requestedUser name of the user whose resources are requested
	 */
	public void setRequestedUser(final String requestedUser) {
		this.requestedUser = requestedUser;
	}

	/**
	 * @return the tagcloud command
	 */
	public TagCloudCommand getTagcloud() {
		return this.tagcloud;
	}

	/**
	 * @param tagcloud the tagcloud command
	 */
	public void setTagcloud(final TagCloudCommand tagcloud) {
		this.tagcloud = tagcloud;
	}

	/**
	 * @return The requested format.
	 * 
	 */
	public String getFormat() {
		if (this.format != null && !this.format.trim().equals("")) return this.format;
		/*
		 * the default is html
		 * */
		return "html";
	}

	/** 
	 * delegated to {@link RequestWrapperContext}
	 */
	/**
	 * @param format
	 */
	public void setFormat(final String format) {
		this.format = format;
	}

	/**
	 * @return the resourcetype
	 */
	public Set<Class<? extends Resource>> getResourcetype() {
		return this.resourcetype;
	}

	/**
	 * @param resourcetype the resourcetype to set
	 */
	public void setResourcetype(final Set<Class<? extends Resource>> resourcetype) {
		this.resourcetype = resourcetype;
	}

	/**
	 * @return the sortPage
	 */
	public String getSortPage() {
		return this.sortPage;
	}

	/**
	 * @param sortPage the sortPage to set
	 */
	public void setSortPage(final String sortPage) {
		this.sortPage = sortPage;
	}

	/**
	 * @return the sortPageOrder
	 */
	public String getSortPageOrder() {
		return this.sortPageOrder;
	}

	/**
	 * @param sortPageOrder the sortPageOrder to set
	 */
	public void setSortPageOrder(final String sortPageOrder) {
		this.sortPageOrder = sortPageOrder;
	}

	/**
	 * @return the restrictToTags
	 */
	public boolean getRestrictToTags() {
		return this.restrictToTags;
	}

	/**
	 * @param restrictToTags the restrictToTags to set
	 */
	public void setRestrictToTags(final boolean restrictToTags) {
		this.restrictToTags = restrictToTags;
	}

	/**
	 * @return the callback
	 */
	public String getCallback() {
		return this.callback;
	}

	/**
	 * @param callback the callback to set
	 */
	public void setCallback(final String callback) {
		this.callback = callback;
	}

	/**
	 * @return the filter
	 */
	public FilterEntity getFilter() {
		return this.filter;
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(final FilterEntity filter) {
		this.filter = filter;
	}

	/**
	 * @return the layout
	 */
	public String getLayout() {
		return this.layout;
	}

	/**
	 * @param layout the layout to set
	 */
	public void setLayout(final String layout) {
		this.layout = layout;
	}

	/**
	 * @return the formatEmbedded
	 */
	public boolean getFormatEmbedded() {
		return this.formatEmbedded;
	}

	/**
	 * @param formatEmbedded the formatEmbedded to set
	 */
	public void setFormatEmbedded(final boolean formatEmbedded) {
		this.formatEmbedded = formatEmbedded;
	}
	
	/**
	 * @return the tagstype
	 */
	public TagsType getTagstype() {
		return this.tagstype;
	}

	/**
	 * @param tagstype the tagstype to set
	 */
	public void setTagstype(final TagsType  tagstype) {
		this.tagstype = tagstype;
	}

	/**
	 * @return the notags
	 */
	public boolean isNotags() {
		return this.notags;
	}

	/**
	 * @param notags the notags to set
	 */
	public void setNotags(final boolean notags) {
		this.notags = notags;
	}

	/**
	 * @param personalized the personalized to set
	 */
	public void setPersonalized(final boolean personalized) {
		this.personalized = personalized;
	}
	
	/**
	 * @return the personalized
	 */
	public boolean isPersonalized() {
		return personalized;
	}

	/**
	 * @return the referer
	 */
	public String getReferer() {
		return this.referer;
	}

	/**
	 * @param referer the referer to set
	 */
	public void setReferer(final String referer) {
		this.referer = referer;
	}
	
	/**
	 * @return enum how to handle duplicates
	 */
	public Duplicates getDuplicates() {
		return this.duplicates;
	}
	
	/**
	 * @param duplicates  the duplicates
	 */
	public void setDuplicates(Duplicates duplicates) {
		this.duplicates = duplicates;
	}
	
	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return this.startDate;
	}
	
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return this.endDate;
	}
	
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	/** @return whether the result should be presented as a download */
	public boolean isDownload() {
		return download;
	}

	/**
	 * @param download whether the result should be presented as a download
	 */
	public void setDownload(boolean download) {
		this.download = download;
	}

	/**
	 * @return how to render person names in bibtex export (true <=> person names in "First Last" order)
	 */
	public boolean isFirstLastNames() {
		return firstLastNames;
	}

	/**
	 * @return bibtexkey stuff
	 */
	public boolean isGeneratedBibtexKeys() {
		return generatedBibtexKeys;
	}

	/**
	 * @param generatedBibtexKeys bibtexkey stuff
	 */
	public void setGeneratedBibtexKeys(boolean generatedBibtexKeys) {
		this.generatedBibtexKeys = generatedBibtexKeys;
	}

	/**
	 * @param firstLastName how to render person names in bibtex export (true <=> person names in "First Last" order)
	 */
	public void setFirstLastNames(boolean firstLastName) {
		this.firstLastNames = firstLastName;
	}

	/**
	 * @return name of a spring-registered urlGenerator (for customized biburl fields from vufind)
	 */
	public String getUrlGenerator() {
		return urlGenerator;
	}

	/**
	 * @param urlGenerator name of a spring-registered urlGenerator (for customized biburl fields from vufind)
	 */
	public void setUrlGenerator(String urlGenerator) {
		this.urlGenerator = urlGenerator;
	}
	/**
	 * @see PublicationViewCommand#isSkipDummyValues()
	 * @return the skipDummyValues
	 */
	public boolean isSkipDummyValues() {
		return this.skipDummyValues;
	}
	/**
	 * @see PublicationViewCommand#isSkipDummyValues()
	 * @param skipDummyValues the skipDummyValues to set
	 */
	public void setSkipDummyValues(boolean skipDummyValues) {
		this.skipDummyValues = skipDummyValues;
	}
}