package org.bibsonomy.database.common.typehandler;

import java.sql.SQLException;

import org.bibsonomy.model.sync.SynchronizationStatus;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;

/**
 * @version $Id$
 */
public class SyncStatusTypeHandlerCallback extends AbstractTypeHandlerCallback {

	@Override
	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setString(SynchronizationStatus.UNKNOWN.getSynchronizationStatus());
		} else {
			setter.setString(((SynchronizationStatus) parameter).getSynchronizationStatus());
		}
	}

	@Override
	public Object valueOf(final String str) {
		return SynchronizationStatus.valueOf(str.toUpperCase());
	}
}