/**
 * BibSonomy-Database-Common - Helper classes for database interaction
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
package org.bibsonomy.database.common.typehandler;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.common.typehandler.crislinktype.CRISLinkTypeTypeHandlerCallbackDelegate;
import org.bibsonomy.database.common.typehandler.crislinktype.GroupPersonLinktypeDelegate;
import org.bibsonomy.database.common.typehandler.crislinktype.ProjectPersonLinkTypeDelegate;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * typehandler callback for {@link org.bibsonomy.model.cris.CRISLinkType}
 *
 * @author dzo
 */
public class CRISLinkTypeTypeHandlerCallback extends AbstractTypeHandlerCallback {
	private static final Log LOG = LogFactory.getLog(CRISLinkTypeTypeHandlerCallback.class);

	private List<CRISLinkTypeTypeHandlerCallbackDelegate> delegates = Arrays.asList(new ProjectPersonLinkTypeDelegate(), new GroupPersonLinktypeDelegate());

	@Override
	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		final CRISLinkTypeTypeHandlerCallbackDelegate delegate = this.getDelegate(parameter.getClass());
		delegate.setParameter(setter, parameter);
	}

	private CRISLinkTypeTypeHandlerCallbackDelegate getDelegate(final Class<?> crisLinkClass) {
		for (final CRISLinkTypeTypeHandlerCallbackDelegate delegate : this.delegates) {
			if (delegate.canHandle(crisLinkClass)) {
				return delegate;
			}
		}

		return null;
	}

	@Override
	public Object getResult(ResultGetter getter) throws SQLException {
		final String type = getter.getResultSet().getString("linktype_type");
		try {
			final int typeId = Integer.parseInt(type);

			final Class<?> clazz = CRISLinkTypeClassTypeHandlerCallback.LINK_TYPE_CLASS_ID_MAP.getKeyByValue(typeId);

			final CRISLinkTypeTypeHandlerCallbackDelegate delegate = this.getDelegate(clazz);
			return delegate.getParameter(Integer.parseInt(getter.getString()));
		} catch (final NumberFormatException e) {
			LOG.error("error converting crislinktype", e);
		}

		return null;
	}

	@Override
	public Object valueOf(String s) {
		throw new UnsupportedOperationException();
	}
}
