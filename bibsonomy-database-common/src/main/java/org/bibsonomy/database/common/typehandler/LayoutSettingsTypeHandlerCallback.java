package org.bibsonomy.database.common.typehandler;

import java.sql.SQLException;

import org.bibsonomy.model.user.settings.LayoutSettings;
import org.bibsonomy.webapp.view.constants.ViewLayout;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * Converts the simple_interface field to {@link LayoutSettings} by using bitmask logic.
 *
 * @author jil
 */
public class LayoutSettingsTypeHandlerCallback implements TypeHandlerCallback {

	private static final int SIMPLE_BIT = 0b00000001;
	private static final int LAYOUT_BIT = 0b00000010;
	
	/* (non-Javadoc)
	 * @see com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#getResult(com.ibatis.sqlmap.client.extensions.ResultGetter)
	 */
	@Override
	public Object getResult(ResultGetter res) throws SQLException {
		return valueOf(res.getInt());
	}

	/* (non-Javadoc)
	 * @see com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#setParameter(com.ibatis.sqlmap.client.extensions.ParameterSetter, java.lang.Object)
	 */
	@Override
	public void setParameter(ParameterSetter ps, Object obj) throws SQLException {
		final LayoutSettings settings;
		if (obj instanceof LayoutSettings) {
			settings = (LayoutSettings) obj;
		} else {
			settings = new LayoutSettings();
		}
		ps.setInt(intValue(settings));
	}

	/**
	 * @param settings
	 * @return
	 */
	private static int intValue(LayoutSettings settings) {
		int intVal = 0b00000000;
		if (ViewLayout.BOOTSTRAP.equals(settings.getViewLayout())) {
			intVal |= LAYOUT_BIT;
		}
		if (settings.isSimpleInterface()) {
			intVal |= SIMPLE_BIT;
		}
		return intVal;
	}

	/* (non-Javadoc)
	 * @see com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#valueOf(java.lang.String)
	 */
	@Override
	public Object valueOf(final String stringRepresentation) {
		return valueOf(Integer.parseInt(stringRepresentation));
	}

	/**
	 * @param parseInt
	 * @return
	 */
	private static LayoutSettings valueOf(final int intRepresentation) {
		final LayoutSettings rVal = new LayoutSettings();
		rVal.setViewLayout(booleanToViewLayout((intRepresentation & LAYOUT_BIT) != 0));
		rVal.setSimpleInterface((intRepresentation & SIMPLE_BIT) != 0);
		return rVal;
	}

	/**
	 * @param b
	 * @return
	 */
	private static ViewLayout booleanToViewLayout(boolean useBootStrapLayout) {
		if (useBootStrapLayout) {
			return ViewLayout.BOOTSTRAP;
		}
		return ViewLayout.CLASSIC;
	}

}
