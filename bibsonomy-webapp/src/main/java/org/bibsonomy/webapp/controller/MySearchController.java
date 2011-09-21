package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.MySearchCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

/**
 * This controller retrieve all bibtex informations of a currently logged in
 * user and builds relation tables between several bibtex information fields
 * like author, title and tags.
 * 
 * @author Christian Voigtmann
 * @version $Id$
 */
public class MySearchController extends SingleResourceListControllerWithTags implements MinimalisticController<MySearchCommand> {
	private static final Log log = LogFactory.getLog(MySearchController.class);

	@Override
	public View workOn(final MySearchCommand command) {
		/*
		 * FIXME: implement this for a group!
		 */
		log.debug(this.getClass().getSimpleName());
		final String format = command.getFormat();
		this.startTiming(this.getClass(), format);

		/*
		 * only users which are logged in might post bookmarks -> send them to
		 * login page
		 */
		if (!command.getContext().isUserLoggedIn()) {
			/*
			 * FIXME: We need to add the ?referer= parameter such that the user
			 * is send back to this controller after login. This is not so
			 * simple, because we cannot access the query path and for POST
			 * requests we would need to build the parameters by ourselves.
			 */
			return new ExtendedRedirectView("/login");
		}

		final User user = command.getContext().getLoginUser();

		// set grouping entity, grouping name, tags
		// final GroupingEntity groupingEntity = GroupingEntity.USER;
		// final String groupingName = user.getName();

		String groupingName = command.getRequGroup();
		GroupingEntity groupingEntity = GroupingEntity.GROUP;

		if (groupingName == null) {
			groupingName = user.getName();
			groupingEntity = GroupingEntity.USER;
		}

		// retrieve and set the requested resource lists, along with total
		// counts
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(format, command.getResourcetype())) {
			// FIXME: we should deliver items dynamically via ajax,
			// displaying a 'wheel of fortune' until all items are loaded
			this.setList(command, resourceType, groupingEntity, groupingName, null, null, null, null, null, Integer.MAX_VALUE);
			this.postProcessAndSortList(command, resourceType);
		}

		/**
		 * retrieve all bibtex from current user
		 */
		final ListCommand<Post<BibTex>> bibtex = command.getBibtex();

		final SortedSet<String> titles = new TreeSet<String>();
		final SortedSet<String> authors = new TreeSet<String>();
		final SortedSet<String> tags = new TreeSet<String>();

		/**
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

		/**
		 * return view to show the mySearch.jspx side
		 */
		return Views.MYSEARCH;
	}

	private void buildRelationTables(final ListCommand<Post<BibTex>> bibtex, final MySearchCommand command) {

		/**
		 * containers for relation tables
		 */
		final LinkedList<String> titleList = command.getTitles();
		final LinkedList<String> tagList = command.getTags();
		final LinkedList<String> authorList = command.getAuthors();

		/**
		 * sorted lists for several relations
		 */
		final SortedSet<Integer>[] tagTitle = new TreeSet[tagList.size()];
		final SortedSet<Integer>[] authorTitle = new TreeSet[authorList.size()];
		final SortedSet<Integer>[] tagAuthor = new TreeSet[tagList.size()];
		final SortedSet<Integer>[] titleAuthor = new TreeSet[titleList.size()];

		/**
		 * string arrays for hash and url informations for the several bibtex
		 */
		final String[] bibtexHashs = new String[titleList.size()];
		final String[] bibtexUrls = new String[titleList.size()];

		/**
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

		/**
		 * store relation tables in the command object
		 */
		command.setTagTitle(tagTitle);
		command.setAuthorTitle(authorTitle);
		command.setTagAuthor(tagAuthor);
		command.setTitleAuthor(titleAuthor);
		command.setBibtexHash(bibtexHashs);
		command.setBibtexUrls(bibtexUrls);

		/**
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
