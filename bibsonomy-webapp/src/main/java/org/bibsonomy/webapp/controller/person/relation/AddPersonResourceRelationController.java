package org.bibsonomy.webapp.controller.person.relation;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.exception.ResourcePersonAlreadyAssignedException;
import org.bibsonomy.model.util.PersonUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.person.relation.PersonResourceRelationCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectViewWithAttributes;
import org.bibsonomy.webapp.view.Views;
import org.json.simple.JSONObject;
import org.springframework.validation.Errors;

import java.util.List;

/**
 * this controller creates a new person resource relation
 *
 * - /addPersonResourceRelation
 *
 * @author dzo, mho
 */
public class AddPersonResourceRelationController implements MinimalisticController<PersonResourceRelationCommand>, ErrorAware {
	private LogicInterface logic;
	private URLGenerator urlGenerator;
	private Errors errors;

	@Override
	public PersonResourceRelationCommand instantiateCommand() {
		return new PersonResourceRelationCommand();
	}

	@Override
	public View workOn(PersonResourceRelationCommand command) {
		final String interhash = command.getInterhash();
		final int index = command.getIndex();
		final PersonResourceRelationType type = command.getType();
		final List<Post<BibTex>> posts = this.logic.getPosts(BibTex.class, GroupingEntity.ALL, null, null, interhash, null, null, null, null, null, null, 0, 100);

		/*
		 * check the ckey
		 */
		if (!command.getContext().isValidCkey()) {
			errors.reject("error.field.valid.ckey");
		}

		// TODO: this results in an exception; maybe add error and handle it in the database
		if (!present(posts)) {
			throw new ObjectNotFoundException(interhash);
		}

		final Post<BibTex> post = posts.get(0);

		final String personId = command.getPerson().getPersonId();
		final Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, personId);

		if (!present(type)) {
			PersonUtils.getRelationType(person, post.getResource());
		}

		// TODO: what should we do when the person was not found?

		try {
			final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
			resourcePersonRelation.setPerson(person);
			resourcePersonRelation.setRelationType(type);
			resourcePersonRelation.setPersonIndex(index);
			resourcePersonRelation.setPost(post);
			this.logic.addResourceRelation(resourcePersonRelation);
		} catch (ResourcePersonAlreadyAssignedException e) {
			errors.reject("person.error.addRelation");
		}
		final ExtendedRedirectViewWithAttributes redirect = new ExtendedRedirectViewWithAttributes(this.urlGenerator.getPersonUrl(personId));
		if (this.errors.hasErrors()) {
			redirect.addAttribute(ExtendedRedirectViewWithAttributes.ERRORS_KEY, this.errors);
		} else {
			redirect.addAttribute(ExtendedRedirectViewWithAttributes.SUCCESS_MESSAGE_KEY, "person.success.addRelation");
		}
		return redirect;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param urlGenerator
	 */
	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}
}
