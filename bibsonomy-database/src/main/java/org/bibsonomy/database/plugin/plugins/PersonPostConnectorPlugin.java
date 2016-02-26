package org.bibsonomy.database.plugin.plugins;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.markup.MyOwnSystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.util.PersonNameUtils;

/**
 * connects publications with persons
 * 
 * @author dzo
 */
public class PersonPostConnectorPlugin extends AbstractDatabasePlugin {
	private static final Log log = LogFactory.getLog(PersonPostConnectorPlugin.class);
	
	private PersonDatabaseManager personDatabaseManager;
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.AbstractDatabasePlugin#onPublicationInsert(org.bibsonomy.model.Post, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onPublicationInsert(Post<? extends BibTex> post, DBSession session) {
		if (SystemTagsUtil.containsSystemTag(post.getTags(), MyOwnSystemTag.NAME)) {
			final User user = post.getUser();
			if (present(user)) {
				final Person person = this.personDatabaseManager.getPersonByUser(user.getName(), session);
				if (present(person)) {
					final BibTex publication = post.getResource();
					autoInsertPersonResourceRelation(post, person, publication.getAuthor(), PersonResourceRelationType.AUTHOR, session);
					autoInsertPersonResourceRelation(post, person, publication.getEditor(), PersonResourceRelationType.EDITOR, session);
				}
			}
		}
		
	}

	/**
	 * @param post
	 * @param person
	 * @param personList 
	 * @param relationType 
	 * @param session
	 */
	private void autoInsertPersonResourceRelation(Post<? extends BibTex> post, final Person person, List<PersonName> personList, PersonResourceRelationType relationType, DBSession session) {
		final List<PersonName> personNames = person.getNames();
		final SortedSet<Integer> foundPersons = new TreeSet<>();
		if (present(personNames)) {
			for (final PersonName personName : personNames) {
				foundPersons.addAll(PersonNameUtils.getPositionInPersonList(personName, personList, true));
			}
		}
		
		if (foundPersons.size() == 1) {
			final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
			resourcePersonRelation.setPerson(person);
			resourcePersonRelation.setPost(post);
			resourcePersonRelation.setRelationType(relationType);
			resourcePersonRelation.setPersonIndex(foundPersons.iterator().next().intValue());
			this.personDatabaseManager.addResourceRelation(resourcePersonRelation, session);
		} else if (foundPersons.size() != 0) {
			log.warn("found more than one author that could be the person " + post.getResource().getInterHash() + " " + PersonNameUtils.serializePersonNames(personNames));
		}
	}

	/**
	 * @param personDatabaseManager the personDatabaseManager to set
	 */
	public void setPersonDatabaseManager(PersonDatabaseManager personDatabaseManager) {
		this.personDatabaseManager = personDatabaseManager;
	}
}
