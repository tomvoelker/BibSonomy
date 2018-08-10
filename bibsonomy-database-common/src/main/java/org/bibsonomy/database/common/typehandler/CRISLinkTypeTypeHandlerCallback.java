package org.bibsonomy.database.common.typehandler;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import org.bibsonomy.database.common.typehandler.crislinktype.CRISLinkTypeTypeHandlerCallbackDelegate;
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

	private List<CRISLinkTypeTypeHandlerCallbackDelegate> delegates = Arrays.asList(new ProjectPersonLinkTypeDelegate());

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

			final Class<?> clazz = CRISLinkTypeClassTypeHandlerCallback.ID_LINK_TYPE_CLASS_MAP.get(typeId);

			final CRISLinkTypeTypeHandlerCallbackDelegate delegate = this.getDelegate(clazz);
			return delegate.getParameter(1); // FIXME
		} catch (NumberFormatException e) {
			// ignore
		}

		return null;
	}

	@Override
	public Object valueOf(String s) {
		throw new UnsupportedOperationException();
	}
}
