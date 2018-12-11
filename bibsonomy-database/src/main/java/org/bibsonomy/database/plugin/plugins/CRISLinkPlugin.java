package org.bibsonomy.database.plugin.plugins;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.CRISEntityType;
import org.bibsonomy.database.managers.CRISLinkDatabaseManager;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.params.CRISLinkParam;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.CRISLinkDataSource;
import org.bibsonomy.model.cris.GroupPersonLinkType;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.model.cris.Project;

import java.util.Date;

/**
 * handles updates regarding cris link
 *
 * TODO: find a nicer way to disable this plugin
 *
 * @author dzo
 */
public class CRISLinkPlugin extends AbstractDatabasePlugin {
	private static final Log LOG = LogFactory.getLog(CRISLinkPlugin.class);

	private final boolean crisEnabled;
	private final PersonDatabaseManager personDatabaseManager;
	private final CRISLinkDatabaseManager crisLinkDatabaseManager;

	/**
	 * default constructor
	 * @param crisEnabled
	 * @param personDatabaseManager
	 * @param crisLinkDatabaseManager
	 */
	public CRISLinkPlugin(boolean crisEnabled, PersonDatabaseManager personDatabaseManager, CRISLinkDatabaseManager crisLinkDatabaseManager) {
		this.crisEnabled = crisEnabled;
		this.personDatabaseManager = personDatabaseManager;
		this.crisLinkDatabaseManager = crisLinkDatabaseManager;
	}

	@Override
	public void onAddedGroupMembership(final Group group, final GroupMembership membership, final User loggedinUser, final DBSession session) {
		/*
		 * skip auto linking if the system is configured to be a cris system
		 * and the group is not an organization
		 */
		if (!this.crisEnabled && !group.isOrganization()) {
			return;
		}

		/*
		 * add a link between the person claimed by the user and the group/organization
		 */
		final String userName = membership.getUser().getName();

		final Person person = this.personDatabaseManager.getPersonByUser(userName, session);
		if (!present(person)) {
			return;
		}

		final CRISLink link = new CRISLink();
		link.setSource(group);
		link.setTarget(person);
		link.setStartDate(new Date());
		link.setDataSource(CRISLinkDataSource.SYSTEM);
		link.setLinkType(GroupPersonLinkType.LEADER);

		this.crisLinkDatabaseManager.createCRISLink(link, loggedinUser, session);
	}

	@Override
	public void onRemovedGroupMembership(Group group, String username, User loggedinUser, DBSession session) {
		/*
		 * skip auto linking if the system is configured to be a cris system
		 * and the group is not an organization
		 */
		if (!this.crisEnabled && !group.isOrganization()) {
			return;
		}

		/*
		 * "remove" the link between the claimed person of the user removed from the group
		 * by setting the end date of the link
		 */
		final Person claimedPerson = this.personDatabaseManager.getPersonByUser(username, session);
		if (!present(claimedPerson)) {
			return;
		}

		final CRISLink crisLink = this.crisLinkDatabaseManager.getCRISLink(group, claimedPerson, session);
		if (!present(crisLink)) {
			LOG.error("no cris link found between group '" + group.getName() + " ");
			return;
		}

		crisLink.setEndDate(new Date());
		this.crisLinkDatabaseManager.updateCRISLink(crisLink, loggedinUser, session);
	}

	@Override
	public void onPersonUpdate(Person oldPerson, Person newPerson, DBSession session) {
		this.updateCRISLinks(oldPerson, newPerson, session);
	}

	@Override
	public void onPersonDelete(Person person, User user, DBSession session) {
		this.deleteCRISLinks(person, session);
	}

	@Override
	public void onProjectUpdate(Project oldProject, Project newProject, User loggedinUser, DBSession session) {
		this.updateCRISLinks(oldProject, newProject, session);
	}

	@Override
	public void onProjectDelete(final Project project, final User loggedinUser, final DBSession session) {
		this.deleteCRISLinks(project, session);
	}

	private CRISLinkParam createCRISParam(Linkable oldLink) {
		final CRISLinkParam param = new CRISLinkParam();
		final CRISEntityType type = CRISEntityType.getCRISEntityType(oldLink.getClass());
		param.setSourceType(type);
		param.setTargetType(type);

		param.setSourceId(oldLink.getId());
		param.setTargetId(oldLink.getId());
		return param;
	}

	private void updateCRISLinks(final Linkable oldLink, final Linkable newLink, final DBSession session) {
		final CRISLinkParam param = createCRISParam(oldLink);

		param.setNewContentId(newLink.getId().intValue());

		// update link to project
		this.update("updateAllCRISLinkSources", param, session);
		this.update("updateAllCRISLinkTargets", param, session);
	}

	private void deleteCRISLinks(final Linkable link, final DBSession session) {
		final CRISLinkParam param = createCRISParam(link);

		this.insert("logCRISLinkDelete", param, session);
		this.delete("deleteAllCrisLinks", param, session);
	}


}
