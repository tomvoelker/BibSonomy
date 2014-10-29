package org.bibsonomy.database.common.typehandler;

import java.sql.SQLException;

import org.bibsonomy.model.enums.GoldStandardRelation;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;

/**
 * typehandler for relations
 *
 * @author dzo
 */
public class GoldStandardRelationTypeHandlerCallback extends AbstractTypeHandlerCallback {

	@Override
	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setInt(GoldStandardRelation.REFERENCE.getValue());
		} else {
			final GoldStandardRelation relation = (GoldStandardRelation) parameter;
			setter.setInt(relation.getValue());
		}
	}

	@Override
	public Object valueOf(final String str) {
		try {
			return GoldStandardRelation.getGoldStandardRelation(Integer.parseInt(str));
		} catch (NumberFormatException ex) {
			return GoldStandardRelation.REFERENCE;
		}
	}

}
