package org.bibsonomy.search.index.update.cris;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.search.index.update.IndexUpdateLogic;

import java.util.Date;
import java.util.List;

/**
 * {@link CRISLink} update logic
 *
 * @author dzo
 */
public class CRISLinkUpdateLogic extends AbstractDatabaseManagerWithSessionManagement implements IndexUpdateLogic<CRISLink> {

	@Override
	public List<CRISLink> getNewerEntities(long lastEntityId, Date lastLogDate, int size, int offset) {
		return null;
	}

	@Override
	public List<CRISLink> getDeletedEntities(Date lastLogDate) {
		return null;
	}
}
