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
package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.systemstags.markup.MyOwnSystemTag;
import org.bibsonomy.layout.citeproc.renderer.AdhocRenderer;
import org.bibsonomy.layout.csl.CSLFilesManager;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonPostsStyle;
import org.bibsonomy.model.enums.PersonResourceRelationOrder;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.extra.SearchFilterElement;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.logic.query.statistics.meta.DistinctFieldQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.model.util.PersonUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.services.person.PersonRoleRenderer;
import org.bibsonomy.services.searcher.PostSearchQuery;
import org.bibsonomy.util.Sets;
import org.bibsonomy.util.object.FieldDescriptor;
import org.bibsonomy.webapp.command.PersonPageCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.picture.PictureHandlerFactory;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

public class PersonPageController extends SingleResourceListController implements MinimalisticController<PersonPageCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(PersonMatch.class);

	public static final Set<PersonResourceRelationType> PUBLICATION_RELATED_RELATION_TYPES = Sets.asSet(PersonResourceRelationType.AUTHOR, PersonResourceRelationType.EDITOR);

	private LogicInterface adminLogic;
	private RequestLogic requestLogic;
	private URLGenerator urlGenerator;
	private Errors errors;
	private PersonRoleRenderer personRoleRenderer;
	private PictureHandlerFactory pictureHandlerFactory;
	private Map<Class<?>, Function<String, FieldDescriptor<?, ?>>> mappers;

	/** the college that the cris system is configured for */
	private String crisCollege;


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

		fillCommandWithPersonResourceRelations(this.logic, command, person, 0, command.getPersonPostsPerPage());
		fillCommandWithSimiliarAuthorPubs(command, person);

		// extract user settings
		// Get the linked user's person posts style settings
		final User user = adminLogic.getUserDetails(person.getUser());
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

	public static void fillCommandWithPersonResourceRelations(final LogicInterface logic, final PersonPageCommand command, Person person, final int defaultStart, final int defaultPostsPerPage) {

		final int postsPerPage = defaultPostsPerPage;

		// default start/end for post query
		// FIXME: use ListPageCommand!!!
		int end = defaultStart + postsPerPage;
		int start = defaultStart;

		// override when given via GET param
		final Integer commandStart = command.getStart();
		if (present(commandStart)) {
			start = commandStart;
			end = start + postsPerPage;
		}

		final Integer commandEnd = command.getEnd();
		if (present(commandEnd)) {
			end = commandEnd;
		}

		command.setEnd(end);
		command.setStart(start);

		if (start < postsPerPage) {
			command.setPrevStart(0);
		} else {
			command.setPrevStart(start - postsPerPage);
		}

		/*
		 * FIXME: currently the database does not support queries like: give me all thesis related relations
		 * so we cannot apply the pagination here, otherwise we do not get the PHD information and the other advisor
		 * infos we need to display on top of the view
		 * The current workaround is to get all the relations from the db and apply the pagination afterwards which is not
		 * efficient!
		 */
		final ResourcePersonRelationQueryBuilder queryBuilder = new ResourcePersonRelationQueryBuilder()
				.byPersonId(person.getPersonId())
				.withPosts(true)
				.withPersonsOfPosts(true)
				.groupByInterhash(true)
				.orderBy(PersonResourceRelationOrder.PublicationYear)
				.fromTo(0, Integer.MAX_VALUE);

		// TODO: maybe this should be done in the view?
		final List<ResourcePersonRelation> resourceRelations = logic.getResourceRelations(queryBuilder.build());
		final List<ResourcePersonRelation> authorRelations = new ArrayList<>();
		final List<ResourcePersonRelation> advisorRelations = new ArrayList<>();
		final List<ResourcePersonRelation> otherAuthorRelations = new ArrayList<>();
		final List<ResourcePersonRelation> otherAdvisorRelations = new ArrayList<>();

		for (final ResourcePersonRelation resourcePersonRelation : resourceRelations) {
			final Post<? extends BibTex> post = resourcePersonRelation.getPost();
			final BibTex publication = post.getResource();
			final boolean isThesis = publication.getEntrytype().toLowerCase().endsWith("thesis");
			final boolean isAuthorEditorRelation = PUBLICATION_RELATED_RELATION_TYPES.contains(resourcePersonRelation.getRelationType());

			if (isAuthorEditorRelation) {
				if (isThesis) {
					authorRelations.add(resourcePersonRelation);
				} else {
					otherAuthorRelations.add(resourcePersonRelation);
				}
			} else {
				if (isThesis) {
					advisorRelations.add(resourcePersonRelation);
				} else {
					otherAdvisorRelations.add(resourcePersonRelation);
				}
			}

			// we explicitly do not want ratings on the person pages because this might cause users of the genealogy feature
			// to hesitate putting in their dissertations
			publication.setRating(null);
			publication.setNumberOfRatings(null);
		}

		command.setThesis(authorRelations);
		command.setAdvisedThesis(advisorRelations);
		command.setOtherPubs(applyStartEnd(otherAuthorRelations, start, end));
		// FIXME: not used in the view!!
		command.setOtherAdvisedPubs(otherAdvisorRelations);
	}

	private void fillCommandWithSimiliarAuthorPubs(PersonPageCommand command, Person person) {
		/*
		 * get a list of post that could be also be written by the requested person
		 */
		final List<ResourcePersonRelation> similarAuthorRelations = new ArrayList<>();
		final List<Post<GoldStandardPublication>> similarAuthorPubs = this.getPublicationsOfSimilarAuthor(person);
		for (final Post<GoldStandardPublication> post : similarAuthorPubs) {
			final ResourcePersonRelation relation = new ResourcePersonRelation();
			relation.setPost(post);
			relation.setPersonIndex(PersonUtils.findIndexOfPerson(person, post.getResource()));
			relation.setRelationType(PersonUtils.getRelationType(person, post.getResource()));
			similarAuthorRelations.add(relation);
		}

		command.setSimilarAuthorPubs(similarAuthorRelations);
	}

	private List<Post<GoldStandardPublication>> getPublicationsOfSimilarAuthor(Person person) {
		final PostQuery<GoldStandardPublication> personNameQuery = new PostQueryBuilder()
				.setPersonNames(person.getNames())
				.setOnlyIncludeAuthorsWithoutPersonId(true)
				.end(20) // get 20 "recommendations"
				.createPostQuery(GoldStandardPublication.class);
		return this.logic.getPosts(personNameQuery);
	}

	private static List<ResourcePersonRelation> applyStartEnd(final List<ResourcePersonRelation> otherAuthorRelations,
															  final int requestedStart, final int requestedEnd) {
		final int size = otherAuthorRelations.size();
		if (requestedStart > size) {
			return Collections.emptyList();
		}

		final int end = Math.min(requestedEnd, size);
		return otherAuthorRelations.subList(requestedStart, end);
	}

	private void setGoldStandards(PersonPageCommand command) {
		// build query for person ID to aggregate for counts
		PostSearchQuery<GoldStandardPublication> postsQuery = new PostSearchQuery<>(GoldStandardPublication.class);
		postsQuery.setGrouping(GroupingEntity.PERSON);
		postsQuery.setGroupingName(command.getPerson().getPersonId());
		// TODO fix going to bibtex index rn
		command.setEntrytypeFilters(generateEntrytypeFilters(command, postsQuery));
	}

	private void setMyOwnPosts(PersonPageCommand command) {
		// build query for 'myown' posts to aggregate for counts
		PostSearchQuery<BibTex> postsQuery = new PostSearchQuery<>();
		postsQuery.setGrouping(GroupingEntity.USER);
		postsQuery.setGroupingName(command.getPerson().getUser());
		// TODO use system tag
		// postsQuery.setSystemTags(Collections.singletonList(new MyOwnSystemTag()));
		postsQuery.setTags(Collections.singletonList("myown"));

		command.setEntrytypeFilters(generateEntrytypeFilters(command, postsQuery));
	}

	/**
	 * Generate a list of entrytype filter elements of the 'myown' posts.
	 *
	 * @param command the person page command
	 * @return
	 */
	private List<SearchFilterElement> generateEntrytypeFilters(PersonPageCommand command, PostSearchQuery<? extends BibTex> postsQuery) {
		// get aggregated count by given field
		DistinctFieldQuery<BibTex, ?> distinctFieldQuery = new DistinctFieldQuery<>(BibTex.class, (FieldDescriptor<BibTex, ?>) mappers.get(BibTex.class).apply("entrytype"));
		distinctFieldQuery.setPostQuery(postsQuery);
		distinctFieldQuery.setSize(20);

		final Set<?> distinctFieldCounts = this.logic.getMetaData(command.getContext().getLoginUser(), distinctFieldQuery);

		List<SearchFilterElement> filters = new ArrayList<>();
		for (Pair<String, Long> filter : (Set<Pair<String, Long>>) distinctFieldCounts) {
			SearchFilterElement filterElement = new SearchFilterElement(filter.getFirst(), filter.getSecond());
			filterElement.setField("entrytype");
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

	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}

	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	public void setPersonRoleRenderer(PersonRoleRenderer personRoleRenderer) {
		this.personRoleRenderer = personRoleRenderer;
	}

	public void setPictureHandlerFactory(PictureHandlerFactory pictureHandlerFactory) {
		this.pictureHandlerFactory = pictureHandlerFactory;
	}

	public void setMappers(Map<Class<?>, Function<String, FieldDescriptor<?, ?>>> mappers) {
		this.mappers = mappers;
	}

	public void setCrisCollege(String crisCollege) {
		this.crisCollege = crisCollege;
	}
}
