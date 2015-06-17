/**
 * BibSonomy CV Wiki - Wiki for user and group CVs
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
package org.bibsonomy.wiki;

import info.bliki.htmlcleaner.BaseToken;
import info.bliki.wiki.filter.WikipediaParser;
import info.bliki.wiki.model.AbstractWikiModel;
import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.ITableOfContent;
import info.bliki.wiki.namespaces.INamespace;
import info.bliki.wiki.tags.WPTag;
import info.bliki.wiki.tags.util.TagStack;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Layout;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.renderer.LayoutRenderer;
import org.bibsonomy.wiki.tags.AbstractTag;
import org.bibsonomy.wiki.tags.group.GroupDescriptionTag;
import org.bibsonomy.wiki.tags.group.GroupImageTag;
import org.bibsonomy.wiki.tags.group.MembersTag;
//import org.bibsonomy.wiki.tags.shared.DesignTag;
import org.bibsonomy.wiki.tags.shared.HomepageTag;
import org.bibsonomy.wiki.tags.shared.ImageTag;
import org.bibsonomy.wiki.tags.shared.NameTag;
import org.bibsonomy.wiki.tags.shared.resource.BookmarkListTag;
import org.bibsonomy.wiki.tags.shared.resource.PublicationListTag;
import org.bibsonomy.wiki.tags.user.BirthdayTag;
import org.bibsonomy.wiki.tags.user.HobbyTag;
import org.bibsonomy.wiki.tags.user.InstitutionTag;
import org.bibsonomy.wiki.tags.user.InterestsTag;
import org.bibsonomy.wiki.tags.user.LocationTag;
import org.bibsonomy.wiki.tags.user.ProfessionTag;
import org.bibsonomy.wiki.tags.user.RegDateTag;
import org.springframework.context.MessageSource;

/**
 * @author philipp
 * @author Bernd Terbrack
 */
public class CVWikiModel extends AbstractWikiModel {

	static {
		/* About-Me Tags */
		register(new LocationTag());
		register(new BirthdayTag());
		register(new InstitutionTag());
		register(new InterestsTag());
		register(new HobbyTag());
		register(new ProfessionTag());
		
		/* Group Tags */
		register(new MembersTag());
		register(new GroupImageTag());
		register(new GroupDescriptionTag());
		
		/* Shared Tags */
		register(new HomepageTag());
		register(new NameTag());
		register(new ImageTag());
		register(new RegDateTag());
		register(new BookmarkListTag());
		register(new PublicationListTag());
//		register(new DesignTag());
		
	}

	private static void register(final AbstractTag tag) {
		Configuration.DEFAULT_CONFIGURATION.addTokenTag(tag.getName(), tag);
	}

	private User requestedUser;
	private Group requestedGroup;
	private LogicInterface logic;
	private MessageSource messageSource;

	private LayoutRenderer<Layout> layoutRenderer;

	/**
	 * Default Constructor
	 * @param locale 
	 */
	public CVWikiModel(final Locale locale) {
		super(Configuration.DEFAULT_CONFIGURATION, locale, null, null);
	}

	/*
	 * defines the look and feel of the section headlines. can be changed by the class mw-headline.
	 * 
	 * @param rawHead a pure title from the wiki syntax, without the enclosing =
	 * @param headLevel the number of =, indicating the position in the section hierarchy of this title
	 * @param noToC good question. TODO: FIX THIS! Welcher Wahnsinnige verwendet eigentlich negierte boolsche Variablen?!
	 * @param headCounter
	 * 
	 * @see info.bliki.wiki.model.AbstractWikiModel#appendHead(java.lang.String,
	 * int, boolean, int, int, int)
	 */
	@Override
	public ITableOfContent appendHead(final String rawHead, final int headLevel,
			final boolean noToC, final int headCounter, final int startPosition, final int endPosition) {
		final TagStack localStack = WikipediaParser.parseRecursive(rawHead.trim(), this, true, true);

		// This only generates a HTML node 
		final WPTag headTagNode = new WPTag("h" + headLevel);
		for (BaseToken t : localStack.getNodeList()) {
			headTagNode.addChild(t);
		}
		
		headTagNode.addAttribute("class", "mw-headline level" + headLevel, true);
		
		this.append(headTagNode);
		return this.fTableOfContentTag;
	}

	@Override
	public Set<String> getLinks() {
		return Collections.emptySet();
	}

	@Override
	public INamespace getNamespace() {
		return null;
	}

	@Override
	public void parseInternalImageLink(final String imageNamespace, final String rawImageLink) {
		// noop
	}

	/**
	 * set the LogicInterface
	 * 
	 * @param logic
	 *            the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @return the LogicInterface
	 */
	public LogicInterface getLogic() {
		return this.logic;
	}

	/**
	 * set the user
	 * 
	 * @param user
	 *            the user to add
	 */
	public void setRequestedUser(final User user) {
		this.requestedUser = user;
	}

	/**
	 * @return the user
	 */
	public User getRequestedUser() {
		return this.requestedUser;
	}

	/**
	 * @return the layoutRenderer
	 */
	public LayoutRenderer<Layout> getLayoutRenderer() {
		return this.layoutRenderer;
	}

	/**
	 * @param layoutRenderer
	 *            the layoutRenderer to set
	 */
	public void setLayoutRenderer(final LayoutRenderer<Layout> layoutRenderer) {
		this.layoutRenderer = layoutRenderer;
	}

	/**
	 * @return the requestedGroup
	 */
	public Group getRequestedGroup() {
		return this.requestedGroup;
	}

	/**
	 * @param requestedGroup
	 *            the requestedGroup to set
	 */
	public void setRequestedGroup(final Group requestedGroup) {
		this.requestedGroup = requestedGroup;
	}

	/**
	 * @return the messageSource
	 */
	public MessageSource getMessageSource() {
		return messageSource;
	}

	/**
	 * @param messageSource the messageSource to set
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
