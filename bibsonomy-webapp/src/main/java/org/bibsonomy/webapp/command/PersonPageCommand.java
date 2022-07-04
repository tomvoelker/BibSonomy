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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.model.PersonMergeFieldConflict;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.PhDRecommendation;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.PersonPostsStyle;
import org.bibsonomy.model.extra.SearchFilterElement;
import org.bibsonomy.model.logic.exception.LogicException;

/**
 * @author Christian Pfeiffer
 */
@Setter
@Getter
public class PersonPageCommand extends BaseCommand {

	/** describes the relation between a person and a resource */
	private ResourcePersonRelation resourcePersonRelation;
	
	/** personId of the person requested */
	private String requestedPersonId;

	/** if true only persons of the configured cris system are displayed */
	private boolean limitResultsToCRISCollege;

	private List<Project> projects;
	private boolean showProjects;
	
	private Person person;
	private List<PersonName> alternativeNames;
	private Post<? extends Resource> post;

	private int personPostsPerPage;
	private PersonPostsStyle personPostsStyle;
	private String personPostsLayout;
	private List<SearchFilterElement> entrytypeFilters;

	private List<Post<BibTex>> myownPosts;
	private Map<String, String> myownPostsRendered;
	private Map<String, String> renderedPosts;
	private List<ResourcePersonRelation> thesis;
	private List<ResourcePersonRelation> advisedThesis;
	private List<ResourcePersonRelation> allPosts;
	private List<ResourcePersonRelation> otherPubs;
	private List<ResourcePersonRelation> otherAdvisedPubs;
	private List<ResourcePersonRelation> similarAuthorPubs;
	private int totalCount;

	private List<PersonMatch> personMatchList;

	private Map<Integer, PersonMergeFieldConflict[]> mergeConflicts;
	private List<PhDRecommendation> phdAdvisorRecForPerson;

	private Integer start;
	private Integer end;

	private String responseString;

	@Deprecated // TODO use posts per page as a var or use the listcommand class to get pagination for free
	private Integer prevStart;

	@Deprecated // FIXME: remove use errors handling build into spring
	private final Collection<LogicException> logicExceptions = new ArrayList<>();


	/** Properties if deleting or adding relations */
	private String type;
	private String interhash;
	private String index;

}
