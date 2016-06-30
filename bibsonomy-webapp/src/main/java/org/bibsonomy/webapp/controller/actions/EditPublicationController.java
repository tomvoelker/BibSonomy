/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.markup.MyOwnSystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.logic.exception.LogicException;
import org.bibsonomy.model.logic.exception.ResourcePersonAlreadyAssignedException;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.actions.EditPublicationCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;

import de.unikassel.puma.openaccess.sword.SwordService;

/**
 * 
 * For strange Java Generics reasons I could not implement the 
 * {@link #instantiateEditPostCommand()} method in exactly the
 * same way in the {@link AbstractEditPublicationController}. Thus I had
 * to make that controller abstract and implement the method 
 * here.
 * 
 * The underlying problem is a bit deeper: I had to parameterize
 * {@link AbstractEditPublicationController} to subclass it in
 * {@link PostPublicationController}.
 * 
 * @author rja
 */
public class EditPublicationController extends AbstractEditPublicationController<EditPublicationCommand> {
	private static final Log log = LogFactory.getLog(EditPublicationController.class);

	private SwordService swordService = null;

	@Override
	protected EditPublicationCommand instantiateEditPostCommand() {
		return new EditPublicationCommand();
	}
	
	@Override
	protected View finalRedirect(String userName, Post<BibTex> post, String referer) {
		
		/*
		 * If a SWORD service is configured and the user claims to be the creator of the 
		 * publication, we forward him to the SWORD service to allow the user to upload the
		 * publication.
		 */
		if (present(this.swordService) && SystemTagsUtil.containsSystemTag(post.getTags(), MyOwnSystemTag.NAME)) {
			String ref = UrlUtils.safeURIEncode(referer);
			String publicationUrl = this.urlGenerator.getPublicationUrlByIntraHashAndUsername(post.getResource().getIntraHash(), userName);
			return new ExtendedRedirectView(publicationUrl + "?referer=" + ref);
		}
		
		return super.finalRedirect(userName, post, referer);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.controller.actions.EditPostController#getHttpsReferrer(org.bibsonomy.webapp.command.actions.EditPostCommand)
	 */
	@Override
	protected String getHttpsReferrer(EditPublicationCommand command) {
		final String url = command.getUrl();
		if (UrlUtils.isHTTPS(url)) {
			return url;
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.controller.actions.EditPostController#createOrUpdateSuccess(org.bibsonomy.webapp.command.actions.EditPostCommand, org.bibsonomy.model.User, org.bibsonomy.model.Post)
	 */
	@Override
	protected void createOrUpdateSuccess(EditPublicationCommand command, User loginUser, Post<BibTex> post) {
		super.createOrUpdateSuccess(command, loginUser, post);
		// if a PersonId has been provided, it means that we have come from a person page ...
		if (command.getPerson() != null) {
			try {
				storePersonRelation(command, loginUser, post);
			} catch (LogicException e) {
				// should not happen
				log.error("error associating new post to person", e);
				throw new RuntimeException(e);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.controller.actions.EditPostController#validatePost(org.bibsonomy.webapp.command.actions.EditPostCommand)
	 */
	@Override
	protected void validatePost(EditPublicationCommand command) {
		super.validatePost(command);
		final List<PersonName> publicationNames = (command.getPersonRole() != null) ? command.getPost().getResource().getPersonNamesByRole(command.getPersonRole()) : null;
		if (((command.getPersonIndex() != null) && (publicationNames != null) && (command.getPersonIndex() >= publicationNames.size())) || ((command.getPersonId() != null) && (command.getPersonIndex() == null))) {
			this.errors.reject("error.field.valid.personId", "The provided person index is invalid.");
			return;
		}
	}
	
	private void storePersonRelation(final EditPublicationCommand command, final User loginUser, final Post<BibTex> pubPost) throws ResourcePersonAlreadyAssignedException {
		
		final Person person;
		if (present(command.getPerson().getPersonId())) {
			// a new publication is added to an existing person
			person = this.logic.getPersonById(PersonIdType.PERSON_ID, command.getPerson().getPersonId());
			if (command.getPersonIndex() == null) {
				final List<PersonName> publicationNames = pubPost.getResource().getPersonNamesByRole(command.getPersonRole());
				command.setPersonIndex(findPersonIndex(person, publicationNames));
			}
		} else {
			// a new person entity is created by creating a publication post and taking its author name
			// as the name of the new person (accessible via add person button on persons/genealogy page)
			final List<PersonName> publicationNames = pubPost.getResource().getPersonNamesByRole(command.getPersonRole());
			if ((command.getPersonIndex() != null) && (command.getPersonIndex() >= publicationNames.size())) {
				this.errors.reject("error.field.valid.personId", "The provided person index is invalid.");
				return;
			}
			person = command.getPerson();
			person.setMainName(publicationNames.get(command.getPersonIndex()));
			this.logic.createOrUpdatePerson(person);
		}
		
		if (person != null) {
			final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
			resourcePersonRelation.setPerson(person);
			resourcePersonRelation.setPost(pubPost);
			resourcePersonRelation.setChangedBy(loginUser.getName());
			resourcePersonRelation.setRelationType(command.getPersonRole());
			resourcePersonRelation.setPersonIndex(command.getPersonIndex());
			this.logic.addResourceRelation(resourcePersonRelation);
			
			if (!present(command.getPost().getResourcePersonRelations())) {
				command.getPost().setResourcePersonRelations(new ArrayList<ResourcePersonRelation>());
			}
			command.getPost().getResourcePersonRelations().add(resourcePersonRelation);
		}
	}
	
	private int findPersonIndex(final Person person, final List<PersonName> publicationNames) {
		int personIndex = -1;
		
		if (publicationNames != null) {
			for (int i = 0; i < publicationNames.size(); ++i) {
				final PersonName cleanPubName = PersonNameUtils.cleanAndSoftNormalizeName(publicationNames.get(i), true);
				for (PersonName perName : person.getNames()) {
					final PersonName cleanPerName = PersonNameUtils.cleanAndSoftNormalizeName(perName, true);
					final boolean lastNameMatch = checkPotentialNamePartEquality(cleanPerName.getLastName(), cleanPubName.getLastName(), false);
					final boolean firstNameMatch = checkPotentialNamePartEquality(cleanPerName.getFirstName(), cleanPubName.getFirstName(), true);
					if (firstNameMatch && lastNameMatch) {
						personIndex = i;
					}
				}
			}
		}
		return personIndex;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.controller.actions.AbstractEditPublicationController#preparePost(org.bibsonomy.webapp.command.actions.EditPublicationCommand, org.bibsonomy.model.Post)
	 */
	@Override
	protected void preparePost(EditPublicationCommand command, Post<BibTex> post) {
		super.preparePost(command, post);
		
		if (command.getPerson() != null) {
			if (present(command.getPerson().getPersonId())) {
				final Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, command.getPersonId());
				command.setPerson(person);
			}
		}
	}

	private boolean checkPotentialNamePartEquality(String namePartA, String namePartB, boolean allowAbbreviation) {
		boolean lastNameMatch = false;
		if ((namePartA == null) || (namePartB == null)) {
			lastNameMatch = (namePartB == namePartA);
		} else {
			if (namePartA.endsWith(".")) {
				namePartA = " " + namePartA.substring(0, namePartA.length() - 1);
			} else {
				namePartA = " " + namePartA + " ";
			}
			namePartB = " " + namePartB + " ";
			lastNameMatch |= namePartA.contains(namePartB);
			lastNameMatch |= namePartB.contains(namePartA);
		}
		return lastNameMatch;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.controller.actions.EditPostController#populateCommandWithPost(org.bibsonomy.webapp.command.actions.EditPostCommand, org.bibsonomy.model.Post)
	 */
	@Override
	protected void populateCommandWithPost(EditPublicationCommand command, Post<BibTex> post) {
		super.populateCommandWithPost(command, post);
		command.setPerson(null);
	}
	
	/**
	 * @param swordService the swordService to set
	 */
	public void setSwordService(SwordService swordService) {
		this.swordService = swordService;
	}
}
