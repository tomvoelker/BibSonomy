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
import org.bibsonomy.webapp.command.person.relation.PersonResourceRelationCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * this controller creates a new person resource relation
 *
 * - /addPersonResourceRelation
 *
 * @author dzo, mho
 */
public class AddPersonResourceRelationController implements MinimalisticController<PersonResourceRelationCommand> {

	private LogicInterface logic;

	@Override
	public PersonResourceRelationCommand instantiateCommand() {
		return new PersonResourceRelationCommand();
	}

	@Override
	public View workOn(PersonResourceRelationCommand command) {
		final JSONObject jsonResponse = new JSONObject();

		final String interhash = command.getInterHash();
		final int index = command.getIndex();
		final PersonResourceRelationType type = command.getType();

		final List<Post<BibTex>> posts = this.logic.getPosts(BibTex.class, GroupingEntity.ALL, null, null, interhash, null, null, null, null, null, null, 0, 100);

		// TODO: this results in an exception; maybe add error and handle it in the database
		if (!present(posts)) {
			throw new ObjectNotFoundException(interhash);
		}

		final Post<BibTex> post = posts.get(0);

		final String personId = command.getPersonId();
		final Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, personId);

		// TODO: what should we do when the person was not found?

		try {
			final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
			resourcePersonRelation.setPerson(person);
			resourcePersonRelation.setPost(post);
			resourcePersonRelation.setRelationType(type);
			resourcePersonRelation.setPersonIndex(index);
			this.logic.addResourceRelation(resourcePersonRelation);
		} catch (Exception e) {
			jsonResponse.put("status", false);
			// TODO: set proper error message
			//jsonResponse.put("message", "Some error occured");
			command.setResponseString(jsonResponse.toString());
			return Views.AJAX_JSON;
		}
		jsonResponse.put("status", true);
		command.setResponseString(jsonResponse.toString());
		return Views.AJAX_JSON;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
