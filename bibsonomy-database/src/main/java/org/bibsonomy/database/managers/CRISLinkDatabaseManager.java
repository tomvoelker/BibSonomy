package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.ErrorAwareResult;
import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.MissingObjectErrorMessage;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.CRISEntityType;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.managers.util.cris.LinkDirectionChecker;
import org.bibsonomy.database.params.CRISLinkParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.Linkable;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * database manager that handles cris links
 *
 * @author dzo
 */
public class CRISLinkDatabaseManager extends AbstractDatabaseManager {

	/**
	 * the general database manager
	 */
	private GeneralDatabaseManager generalDatabaseManager;

	/**
	 * all cris managers
	 */
	private Map<Class<? extends Linkable>, LinkableDatabaseManager<? extends Linkable>> crisManagers;

	/**
	 * to notify others about cris link changes
	 */
	private DatabasePluginRegistry plugins;

	private List<LinkDirectionChecker> checkers;

	/**
	 * creates a link between two cris entities
	 *
	 * @param link
	 * @param loginUser
	 * @param session
	 * @return
	 */
	public JobResult createCRISLink(final CRISLink link, final User loginUser, final DBSession session) {
		try {
			session.beginTransaction();

			// ensure that the correct "direction" is saved into the database
			this.ensureLinkDirection(link);

			final ErrorAwareResult<CRISLinkParam> result = createParam(link, loginUser, session);
			CRISLinkParam param = result.getResult();
			final List<ErrorMessage> errors = result.getErrors();

			/*
			 * there are errors; we cannot create the link
			 */
			if (present(errors)) {
				return JobResult.buildFailure(errors);
			}

			param.setLink(link);

			final Integer newID = this.generalDatabaseManager.getNewId(ConstantID.LINKABLE_ID, session);
			param.setLinkableId(newID);


			// insert the cris link
			this.insert("insertCRISLink", param, session);

			session.commitTransaction();
		} finally {
			session.endTransaction();
		}

		return JobResult.buildSuccess();
	}

	/**
	 * gets the cris link between the source and target
	 * @param source
	 * @param target
	 * @param session
	 * @return
	 */
	public CRISLink getCRISLink(final Linkable source, final Linkable target, final DBSession session) {
		final CRISLink template = new CRISLink();
		template.setSource(source);
		template.setTarget(target);
		this.ensureLinkDirection(template);

		final CRISLinkParam param = new CRISLinkParam();

		final Linkable correctSource = template.getSource();
		final Linkable correctTarget = template.getTarget();

		final Integer sourceId = this.getLinkEntityId(correctSource, session);
		final Integer targetId = this.getLinkEntityId(correctTarget, session);

		if (!present(sourceId) || !present(targetId)) {
			return null;
		}

		param.setSourceId(sourceId.intValue());
		param.setTargetId(targetId.intValue());

		param.setSourceType(CRISEntityType.getCRISEntityType(correctSource.getClass()));
		param.setTargetType(CRISEntityType.getCRISEntityType(correctTarget.getClass()));

		final CRISLink crisLink = this.queryForObject("getCRISLinkDetails", param, CRISLink.class, session);
		if (present(crisLink)) {
			// TODO: think about this; maybe we should query for all source and target information
			crisLink.setSource(source);
			crisLink.setTarget(target);
		}

		return crisLink;
	}

	private ErrorAwareResult<CRISLinkParam> createParam(final CRISLink link, User loginUser, final DBSession session) {
		final Linkable source = link.getSource();
		final Linkable target = link.getTarget();

		final List<ErrorMessage> errorMessages = new LinkedList<>();

		/*
		 * get the links and types
		 */
		final Integer sourceId = this.getLinkEntityId(source, session);
		final Integer targetId = this.getLinkEntityId(target, session);

		final CRISLinkParam param = new CRISLinkParam();
		if (!present(sourceId)) {
			errorMessages.add(new MissingObjectErrorMessage(source.getLinkableId(), "linkable"));
		} else {
			param.setSourceId(sourceId.intValue());
		}

		if (!present(targetId)) {
			errorMessages.add(new MissingObjectErrorMessage(target.getLinkableId(), "linkable"));
		} else {
			param.setTargetId(targetId.intValue());
		}

		param.setSourceType(CRISEntityType.getCRISEntityType(source.getClass()));
		param.setTargetType(CRISEntityType.getCRISEntityType(target.getClass()));

		/*
		 * user information
		 */
		param.setUpdatedBy(loginUser.getName());
		param.setUpdatedAt(new Date());

		return new ErrorAwareResult<>(param, errorMessages);
	}

	/**
	 * updates a crislink already in the database
	 *
	 * @param link
	 * @param loginUser
	 * @param session
	 * @return
	 */
	public JobResult updateCRISLink(final CRISLink link, final User loginUser, final DBSession session) {
		try {
			session.beginTransaction();

			this.ensureLinkDirection(link);

			final ErrorAwareResult<CRISLinkParam> result = this.createParam(link, loginUser, session);

			final CRISLinkParam param = result.getResult();
			final List<ErrorMessage> errors = result.getErrors();

			// fixme: check if link exists

			if (present(errors)) {
				return JobResult.buildFailure(errors);
			}

			// TODO: this.plugins.onCRISLinkUpdate(oldLink, link, loggedinUser, session);
			this.update("updateCRISLink", param, session);

			session.commitTransaction();
		} finally {
			session.endTransaction();
		}

		return JobResult.buildSuccess();
	}

	/**
	 * deletes the provided link between the entities
	 *
	 * @param link
	 * @param loginUser
	 * @param session
	 * @return
	 */
	public JobResult deleteCRISLink(final CRISLink link, final User loginUser, final DBSession session) {
		try {
			session.beginTransaction();

			this.ensureLinkDirection(link);

			final ErrorAwareResult<CRISLinkParam> result = this.createParam(link, loginUser, session);

			final CRISLinkParam param = result.getResult();
			final List<ErrorMessage> errors = result.getErrors();

			/*
			 * check if the crislink to delete is in the database
			 */
			final Linkable source = link.getSource();
			final Linkable target = link.getTarget();
			final CRISLink crisLink = this.getCRISLink(source, target, session);
			if (!present(crisLink)) {
				errors.add(new MissingObjectErrorMessage(String.format("%s-%s", source.getLinkableId(), target.getLinkableId()), "crislink"));
			}

			if (present(errors)) {
				return JobResult.buildFailure(errors);
			}

			// TODO: this.plugins.onCRISLinkDelete(crisLink, loggedinUser, session);
			this.delete("deleteCRISLink", param, session);

			session.commitTransaction();
		} finally {
			session.endTransaction();
		}

		return JobResult.buildSuccess();
	}

	private void ensureLinkDirection(final CRISLink link) {
		final Linkable source = link.getSource();
		final Linkable target = link.getTarget();

		// check if one of the link direction checkers requires a swap
		final boolean requiresSwap = this.checkers.stream().anyMatch(checker -> checker.requiresSwap(source, target));

		if (requiresSwap) {
			link.setSource(target);
			link.setTarget(source);
		}
	}

	private <L extends Linkable> Integer getLinkEntityId(final L linkable, DBSession session) {
		// cast is safe; map is organized in this way
		final LinkableDatabaseManager<L> manager = (LinkableDatabaseManager<L>) this.crisManagers.get(linkable.getClass());
		return manager.getIdForLinkable(linkable, session);
	}

	/**
	 * @param generalDatabaseManager the generalDatabaseManager to set
	 */
	public void setGeneralDatabaseManager(GeneralDatabaseManager generalDatabaseManager) {
		this.generalDatabaseManager = generalDatabaseManager;
	}

	/**
	 * @param crisManagers the crisManagers to set
	 */
	public void setCrisManagers(Map<Class<? extends Linkable>, LinkableDatabaseManager<? extends Linkable>> crisManagers) {
		this.crisManagers = crisManagers;
	}

	/**
	 * @param plugins the plugins to set
	 */
	public void setPlugins(DatabasePluginRegistry plugins) {
		this.plugins = plugins;
	}

	/**
	 * @param checkers the checkers to set
	 */
	public void setCheckers(List<LinkDirectionChecker> checkers) {
		this.checkers = checkers;
	}
}
