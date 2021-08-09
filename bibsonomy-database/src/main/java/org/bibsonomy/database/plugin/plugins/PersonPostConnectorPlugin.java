/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.plugin.plugins;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.information.JobInformation;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.CRISLinkDatabaseManager;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.markup.MyOwnSystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.services.information.PersonResourceLinkInformationAdded;

/**
 * connects publications with persons when the posting user has his/her account connected to a person
 * and the post is tagged with the {@link MyOwnSystemTag} system tag
 * 
 * @author dzo
 */
public class PersonPostConnectorPlugin extends AbstractDatabasePlugin {
	private static final Log log = LogFactory.getLog(PersonPostConnectorPlugin.class);
	
	private final PersonDatabaseManager personDatabaseManager;
	private final GroupDatabaseManager groupDatabaseManager;
	private final CRISLinkDatabaseManager crisLinkDatabaseManager;

	/**
	 * constructor with all required fields
	 *
	 * @param personDatabaseManager
	 * @param groupDatabaseManager
	 * @param crisLinkDatabaseManager
	 */
	public PersonPostConnectorPlugin(final PersonDatabaseManager personDatabaseManager, final GroupDatabaseManager groupDatabaseManager, final CRISLinkDatabaseManager crisLinkDatabaseManager) {
		this.personDatabaseManager = personDatabaseManager;
		this.groupDatabaseManager = groupDatabaseManager;
		this.crisLinkDatabaseManager = crisLinkDatabaseManager;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.AbstractDatabasePlugin#onPostInsert(org.bibsonomy.model.Post, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public List<JobInformation> onPublicationInsert(final Post<? extends BibTex> post, User loggedinUser, final DBSession session) {
		final LinkedList<JobInformation> jobInformation = new LinkedList<>();
		// only link the post with the person of the post user and the post is public
		if (SystemTagsUtil.containsSystemTag(post.getTags(), MyOwnSystemTag.NAME) && GroupUtils.isPublicGroup(post.getGroups())) {
			final User user = post.getUser();

			if (present(user)) {
				/*
				 * get all persons that are connected to groups where the user is a member of
				 */
				final Set<Person> persons = this.getAllPersonsByUserGroups(user, session);

				for (final Person person : persons) {
					final BibTex publication = post.getResource();
					final Optional<JobInformation> authorInfo = this.autoInsertPersonResourceRelation(post, person, publication.getAuthor(), PersonResourceRelationType.AUTHOR, loggedinUser, session);
					authorInfo.ifPresent(jobInformation::add);

					final Optional<JobInformation> editorInfo = this.autoInsertPersonResourceRelation(post, person, publication.getEditor(), PersonResourceRelationType.EDITOR, loggedinUser, session);
					editorInfo.ifPresent(jobInformation::add);
				}
			}
		}

		return jobInformation;
	}

	private Set<Person> getAllPersonsByUserGroups(final User user, final DBSession session) {
		final Set<Person> persons = new HashSet<>();

		final String userName = user.getName();
		final Person personByUser = this.personDatabaseManager.getPersonByUser(userName, session);
		if (present(personByUser)) {
			persons.add(personByUser);
		}

		final List<Group> groupsForUser = this.groupDatabaseManager.getGroupsForUser(userName, true, session);

		/*
		 * load all persons linked with a group in common
		 */
		final Stream<CRISLink> crisList = groupsForUser.stream().map(group -> this.crisLinkDatabaseManager.loadCRISLinks(group, Collections.singletonList(Person.class), session)).flatMap(List::stream);

		final Stream<Person> groupPersons = crisList.map(CRISLink::getTarget).filter(Person.class::isInstance).map(Person.class::cast);
		persons.addAll(groupPersons.collect(Collectors.toList()));

		return persons;
	}

	/**
	 * @param post the post to connect
	 * @param person the person to connect
	 * @param personList
	 * @param relationType
	 * @param loggedinUser
	 * @param session
	 */
	private Optional<JobInformation> autoInsertPersonResourceRelation(final Post<? extends BibTex> post, final Person person, final List<PersonName> personList, final PersonResourceRelationType relationType, final User loggedinUser, final DBSession session) {
		final List<PersonName> personNames = person.getNames();
		final SortedSet<Integer> foundPersons = new TreeSet<>();
		if (present(personNames)) {
			// check if the person name can be found in the publication
			for (final PersonName personName : personNames) {
				foundPersons.addAll(PersonNameUtils.getPositionsInPersonList(personName, personList, true));
			}
		}
		
		if (foundPersons.size() == 1) {
			final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
			resourcePersonRelation.setPerson(person);
			resourcePersonRelation.setPost(post);
			resourcePersonRelation.setRelationType(relationType);
			resourcePersonRelation.setPersonIndex(foundPersons.iterator().next().intValue());

			final boolean added = this.personDatabaseManager.addResourceRelation(resourcePersonRelation, loggedinUser, session);
			if (added) {
				return Optional.of(new PersonResourceLinkInformationAdded(resourcePersonRelation));
			}
		} else if (foundPersons.size() != 0) {
			log.warn("found more than one " + relationType.toString().toLowerCase() + " that could be the person " + post.getResource().getInterHash() + " " + PersonNameUtils.serializePersonNames(personNames));
		}

		return Optional.empty();
	}
}
