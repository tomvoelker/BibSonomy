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
package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.PostLogicInterface;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.MySearchCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;

/**
 * This controller retrieve all publication informations of a currently logged in
 * user and builds relation tables between several publication information fields
 * like author, title and tags:
 * 
 *   - /mysearch
 *   - /mysearch/GROUP
 * 
 * @author Christian Voigtmann
 */
public class MySearchController extends SingleResourceListControllerWithTags implements MinimalisticController<MySearchCommand> {

	@Override
	public View workOn(final MySearchCommand command) {
		/*
		 * only users which are logged in might post bookmarks -> send them to
		 * login page
		 */
		if (!command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}
		
		final String format = command.getFormat();
		this.startTiming(format);
		final User user = command.getContext().getLoginUser();

		// set grouping entity, grouping name, tags
		String groupingName = command.getRequGroup();
		GroupingEntity groupingEntity = GroupingEntity.GROUP;

		if (groupingName == null) {
			groupingName = user.getName();
			groupingEntity = GroupingEntity.USER;
		}

		// retrieve and set the requested resource lists, along with total counts
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(command)) {
			/* 
			 * FIXME: we should deliver items dynamically via ajax,
			 * displaying a 'wheel of fortune' until all items are loaded
			 */ 
			// FIXME: load all publications
			this.setList(command, resourceType, groupingEntity, groupingName, null, null, null, null, null, null, null, PostLogicInterface.MAX_QUERY_SIZE);
			this.postProcessAndSortList(command, resourceType);
		}

		/*
		 * retrieve all bibtex from current user
		 */
		final ListCommand<Post<BibTex>> bibtex = command.getBibtex();

		final SortedSet<String> titles = new TreeSet<String>();
		final SortedSet<String> authors = new TreeSet<String>();
		final SortedSet<String> tags = new TreeSet<String>();

		/*
		 * read title, author and tag information form bibtex
		 */
		for (final Post<BibTex> post : bibtex.getList()) {
			final List<PersonName> postAuthors = post.getResource().getAuthor();
			final List<PersonName> postEditors = post.getResource().getEditor();

			titles.add(post.getResource().getTitle().replaceAll("\\n|\\r", ""));

			if (present(postAuthors)) {
				for (final PersonName name: postAuthors) {
					authors.add(name.getLastName());
				}
			}
			if (present(postEditors)) {
				for (final PersonName name: postEditors) {
					authors.add(name.getLastName());
				}
			}
			
			for (final Tag tag : post.getTags()) {
				tags.add(tag.getName());
			}
		}

		command.setTitles(new LinkedList<String>(titles));
		command.setAuthors(new LinkedList<String>(authors));
		command.setTags(new LinkedList<String>(tags));

		buildRelationTables(bibtex, command);

		// set page title
		command.setPageTitle("user :: " + groupingName); // TODO: i18n

		this.endTiming();

		/*
		 * return view to show the mySearch.jspx site
		 */
		return Views.MYSEARCH;
	}

	private void buildRelationTables(final ListCommand<Post<BibTex>> bibtex, final MySearchCommand command) {
		/*
		 * containers for relation tables
		 */
		final LinkedList<String> titleList = command.getTitles();
		final LinkedList<String> tagList = command.getTags();
		final LinkedList<String> authorList = command.getAuthors();

		/*
		 * sorted lists for several relations
		 */
		final SortedSet<Integer>[] tagTitle = new TreeSet[tagList.size()];
		final SortedSet<Integer>[] authorTitle = new TreeSet[authorList.size()];
		final SortedSet<Integer>[] tagAuthor = new TreeSet[tagList.size()];
		final SortedSet<Integer>[] titleAuthor = new TreeSet[titleList.size()];

		/*
		 * string arrays for hash and url informations for the several bibtex
		 */
		final String[] bibtexHashs = new String[titleList.size()];
		final String[] bibtexUrls = new String[titleList.size()];

		/*
		 * build the relations from the bibtex informations
		 */
		for (final Post<BibTex> post : bibtex.getList()) {
			// read values from resultset
			final BibTex publication = post.getResource();
			final String title = publication.getTitle().replaceAll("\\n|\\r", "");
			final Set<Tag> tags = post.getTags();
			final String hash = publication.getSimHash2();
			final String url = publication.getUrl();

			// tag --> title relation
			for (final Tag tag : tags) {
				if (tagTitle[tagList.indexOf(tag.getName())] == null) {
					SortedSet<Integer> v = new TreeSet<Integer>();
					v.add(titleList.indexOf(title));
					tagTitle[tagList.indexOf(tag.getName())] = v;
				} else {
					tagTitle[tagList.indexOf(tag.getName())].add(titleList.indexOf(title));
				}
			}

			// author --> title relation
			final List<PersonName> author = publication.getAuthor();
			final List<PersonName> persons = new LinkedList<PersonName>();
			if (present(author)) {
				persons.addAll(author);
			}
			if (present(publication.getEditor())) {
				persons.addAll(publication.getEditor());
			}
			for (final PersonName name : persons) {
				final int indexOfAuthor = authorList.indexOf(name.getLastName()); // FIXME: indexOf is inefficient!
				if (authorTitle[indexOfAuthor] == null) { 
					final SortedSet<Integer> v = new TreeSet<Integer>();
					v.add(titleList.indexOf(title));
					authorTitle[indexOfAuthor] = v;
				} else {
					authorTitle[indexOfAuthor].add(titleList.indexOf(title));
				}
			}

			// tag --> author relation
			for (final Tag tag : tags) {
				if (tagAuthor[tagList.indexOf(tag.getName())] == null) {
					final SortedSet<Integer> v = new TreeSet<Integer>();
					for (final PersonName name : persons) {
						v.add(authorList.indexOf(name.getLastName()));
					}
					tagAuthor[tagList.indexOf(tag.getName())] = v;
				} else {
					for (final PersonName name : persons) {
						tagAuthor[tagList.indexOf(tag.getName())].add(authorList.indexOf(name.getLastName()));
					}
				}
			}

			// title --> author relation
			if (titleAuthor[titleList.indexOf(title)] == null) {
				SortedSet<Integer> v = new TreeSet<Integer>();
				for (final PersonName name : persons) {
					v.add(authorList.indexOf(name.getLastName()));
				}
				titleAuthor[titleList.indexOf(title)] = v;
			} else {
				for (final PersonName name : persons) {
					titleAuthor[titleList.indexOf(title)].add(authorList.indexOf(name.getLastName()));
				}
			}

			// BibTeX-Hashtable
			bibtexHashs[titleList.indexOf(title)] = hash;

			// Urls
			bibtexUrls[titleList.indexOf(title)] = url;

		}

		/*
		 * store relation tables in the command object
		 */
		command.setTagTitle(tagTitle);
		command.setAuthorTitle(authorTitle);
		command.setTagAuthor(tagAuthor);
		command.setTitleAuthor(titleAuthor);
		command.setBibtexHash(bibtexHashs);
		command.setBibtexUrls(bibtexUrls);

		/*
		 * simhash is needed by the javascript code in the mySearch.jspx side to
		 * complete the bibtex hash string
		 */
		command.setSimHash(HashID.getSimHash(2).getId());
	}

	@Override
	public MySearchCommand instantiateCommand() {
		return new MySearchCommand();
	}
}
