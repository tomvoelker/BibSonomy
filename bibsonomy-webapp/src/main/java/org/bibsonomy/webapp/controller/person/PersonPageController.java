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
package org.bibsonomy.webapp.controller.person;

import static org.bibsonomy.util.ValidationUtils.present;

import static org.bibsonomy.model.BibTex.ENTRYTYPE_FIELD_NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.bibsonomy.common.Pair;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.layout.citeproc.renderer.AdhocRenderer;
import org.bibsonomy.layout.csl.CSLFilesManager;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonPostsStyle;
import org.bibsonomy.model.enums.PersonResourceRelationOrder;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.extra.SearchFilterElement;
import org.bibsonomy.model.logic.query.statistics.meta.DistinctFieldQuery;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.services.searcher.PostSearchQuery;
import org.bibsonomy.util.Sets;
import org.bibsonomy.util.object.FieldDescriptor;
import org.bibsonomy.webapp.command.PersonPageCommand;
import org.bibsonomy.webapp.controller.SingleResourceListController;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * Controller for a single person details page
 * paths:
 * - /person/PERSON_ID
 *
 * e.g.
 * /person/a.hotho
 *
 * @author Christian Pfeiffer
 */
public class PersonPageController extends SingleResourceListController implements MinimalisticController<PersonPageCommand>, ErrorAware {

	public static final Set<PersonResourceRelationType> PUBLICATION_RELATED_RELATION_TYPES = Sets.asSet(PersonResourceRelationType.AUTHOR, PersonResourceRelationType.EDITOR);
	public static final String NO_THESIS_SEARCH = "NOT entrytype:*thesis*";

	private Errors errors;
	private Map<Class<?>, Function<String, FieldDescriptor<?, ?>>> mappers;

	private boolean crisEnabled;

	// TMP TEST
	private AdhocRenderer renderer;
	private CSLFilesManager cslFilesManager;

	public CSLFilesManager getCslFilesManager() {
		return cslFilesManager;
	}

	public void setCslFilesManager(CSLFilesManager cslFilesManager) {
		this.cslFilesManager = cslFilesManager;
	}

	public AdhocRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(AdhocRenderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public View workOn(PersonPageCommand command) {
		final RequestWrapperContext context = command.getContext();
		final String personId = command.getRequestedPersonId();
		if (!present(personId)) {
			throw new MalformedURLSchemeException("The person page was requested without a person in the request.");
		}

		/*
		 * get the person; if person with the requested id was merged with another person, this method
		 * throws a ObjectMovedException and the wrapper will render the redirect
		 */
		final Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, personId);
		if (!present(person)) {
			return Views.ERROR404;
		}

		command.setPerson(person);

		// set alternative names
		List<PersonName> alternativeNames = new ArrayList<>();
		for (PersonName name : person.getNames()) {
			if(!name.isMain()) {
				alternativeNames.add(name);
			}
		}
		command.setAlternativeNames(alternativeNames);

		// set thesis relations
		final ResourcePersonRelationQueryBuilder queryBuilder = new ResourcePersonRelationQueryBuilder()
				.byPersonId(personId)
				.withPosts(true)
				.withPersonsOfPosts(true)
				.onlyTheses(true)
				.groupByInterhash(true)
				.orderBy(PersonResourceRelationOrder.PublicationYear)
				.fromTo(0, Integer.MAX_VALUE);

		final List<ResourcePersonRelation> thesesRelations = logic.getResourceRelations(queryBuilder.build());
		final List<ResourcePersonRelation> authorEditorRelations = new ArrayList<>();
		final List<ResourcePersonRelation> advisorRelations = new ArrayList<>();

		for (ResourcePersonRelation thesis : thesesRelations) {
			final boolean isAuthorEditor = PUBLICATION_RELATED_RELATION_TYPES.contains(thesis.getRelationType());
			if (isAuthorEditor) {
				authorEditorRelations.add(thesis);
			} else {
				advisorRelations.add(thesis);
			}
		}

		command.setThesis(authorEditorRelations);
		command.setAdvisedThesis(advisorRelations);

		// extract user settings
		// Get the linked user's person posts style settings
		final User user = this.logic.getUserDetails(person.getUser());
		if (present(user)) {
			final PersonPostsStyle personPostsStyle = user.getSettings().getPersonPostsStyle();
			final String personPostsLayout = user.getSettings().getPersonPostsLayout();
			command.setPersonPostsStyle(personPostsStyle);
			command.setPersonPostsLayout(personPostsLayout);
		}

		switch(command.getPersonPostsStyle()) {
			case MYOWN:
				this.setMyOwnPosts(command);
				break;
			case GOLDSTANDARD:
			default:
				this.setGoldStandards(command);
				break;
		}

		return Views.PERSON_SHOW;
	}

	private void setGoldStandards(PersonPageCommand command) {
		// build query for person ID to aggregate for counts
		PostSearchQuery<GoldStandardPublication> postsQuery = new PostSearchQuery<>(GoldStandardPublication.class);
		postsQuery.setGrouping(GroupingEntity.PERSON);
		postsQuery.setGroupingName(command.getPerson().getPersonId());
		if (!crisEnabled) {
			// exclude theses, when CRIS disabled
			postsQuery.setSearch(NO_THESIS_SEARCH);
		}

		final ResultList<Post<GoldStandardPublication>> posts = (ResultList<Post<GoldStandardPublication>>) this.logic.getPosts(postsQuery);
		command.setTotalCount(posts.getTotalCount());

		DistinctFieldQuery<GoldStandardPublication, ?> distinctFieldQuery = new DistinctFieldQuery<>(GoldStandardPublication.class,
				(FieldDescriptor<GoldStandardPublication, ?>) mappers.get(GoldStandardPublication.class).apply(ENTRYTYPE_FIELD_NAME));
		distinctFieldQuery.setPostQuery(postsQuery);
		distinctFieldQuery.setSize(20);

		command.setEntrytypeFilters(generateEntrytypeFilters(command, distinctFieldQuery));
	}

	private void setMyOwnPosts(PersonPageCommand command) {
		// build query for 'myown' posts to aggregate for counts
		PostSearchQuery<BibTex> postsQuery = new PostSearchQuery<>();
		postsQuery.setGrouping(GroupingEntity.USER);
		postsQuery.setGroupingName(command.getPerson().getUser());
		postsQuery.setTags(Collections.singletonList("myown"));
		if (!crisEnabled) {
			// exclude theses, when CRIS disabled
			postsQuery.setSearch(NO_THESIS_SEARCH);
		}

		final ResultList<Post<BibTex>> posts = (ResultList<Post<BibTex>>) this.logic.getPosts(postsQuery);
		command.setTotalCount(posts.getTotalCount());

		DistinctFieldQuery<BibTex, ?> distinctFieldQuery = new DistinctFieldQuery<>(BibTex.class,
				(FieldDescriptor<BibTex, ?>) mappers.get(BibTex.class).apply(ENTRYTYPE_FIELD_NAME));
		distinctFieldQuery.setPostQuery(postsQuery);
		distinctFieldQuery.setSize(20);

		command.setEntrytypeFilters(generateEntrytypeFilters(command, distinctFieldQuery));
	}

	/**
	 * Generate a list of entrytype filter elements of distinct field query.
	 *
	 * @param command the person page command
	 * @return
	 */
	private List<SearchFilterElement> generateEntrytypeFilters(PersonPageCommand command, DistinctFieldQuery<? extends BibTex, ?> distinctFieldQuery) {
		final Set<?> distinctFieldCounts = this.logic.getMetaData(command.getContext().getLoginUser(), distinctFieldQuery);

		List<SearchFilterElement> filters = new ArrayList<>();
		for (Pair<String, Long> filter : (Set<Pair<String, Long>>) distinctFieldCounts) {
			SearchFilterElement filterElement = new SearchFilterElement(filter.getFirst(), filter.getSecond());
			filterElement.setField(ENTRYTYPE_FIELD_NAME);
			filterElement.setMessageKey(String.format("post.resource.entrytype.%s.title", filterElement.getName()));
			filters.add(filterElement);
		}
		Collections.sort(filters);

		return filters;
	}

	@Override
	public PersonPageCommand instantiateCommand() {
		return new PersonPageCommand();
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	public void setMappers(Map<Class<?>, Function<String, FieldDescriptor<?, ?>>> mappers) {
		this.mappers = mappers;
	}

	public void setCrisEnabled(boolean crisEnabled) {
		this.crisEnabled = crisEnabled;
	}
}
