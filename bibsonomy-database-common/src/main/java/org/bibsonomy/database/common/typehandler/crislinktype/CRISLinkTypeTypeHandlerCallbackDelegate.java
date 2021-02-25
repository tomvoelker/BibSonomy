package org.bibsonomy.database.common.typehandler.crislinktype;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import org.bibsonomy.model.cris.CRISLinkType;

import java.sql.SQLException;

/**
 * @author dzo
 */
public interface CRISLinkTypeTypeHandlerCallbackDelegate {

	/**
	 * @param typeClass
	 * @return <code>true</code> iff the delegate can handle the link type
	 */
	public boolean canHandle(final Class<?> typeClass);

	/**
	 * sets the parameter provided to the sql setter
	 * @param setter
	 * @param parameter
	 * @throws SQLException
	 */
	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException;

	/**
	 * converts back to the crislinkType
	 * @param value
	 * @return
	 */
	public CRISLinkType getParameter(int value);
}
