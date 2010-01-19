package org.bibsonomy.database.typehandler;

import java.sql.SQLException;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;

/**
 * An iBATIS type handler callback for {@link Privlevel}es that are mapped to
 * Strings in the database. If the {@link Privlevel} cannot be constructed based
 * on the String, then the Privlevel will be set to <code>MEMBERS</code>.<br/>
 * 
 * Almost copied from <a
 * href="http://opensource.atlassian.com/confluence/oss/display/IBATIS/Type+Handler+Callbacks">Atlassian -
 * Type Handler Callbacks</a>
 * 
 * @author Ken Weiner
 * @author Christian Schenk
 * @author Robert Jaeschke
 * @version $Id$
 */
public class ResourceTypeHandlerCallback extends AbstractTypeHandlerCallback {

	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (parameter == null) {
			throw new IllegalArgumentException("given resource is null");		
		}
		if (parameter instanceof Bookmark) {
			setter.setInt(ConstantID.BOOKMARK_CONTENT_TYPE.getId());
		} else if (parameter instanceof BibTex) {
			setter.setInt(ConstantID.BIBTEX_CONTENT_TYPE.getId());
		} else {
			throw new IllegalArgumentException("unknown content type " + parameter.getClass());
		}
	}

	public Object valueOf(final String str) {
		try {
			final int value = Integer.parseInt(str);
			if (value == ConstantID.BOOKMARK_CONTENT_TYPE.getId()) {
				return new Bookmark();
			} else if (value == ConstantID.BIBTEX_CONTENT_TYPE.getId()) {
				return new BibTex();
			}
		} catch (NumberFormatException ex) {
			
		}
		throw new IllegalArgumentException("unknown content type " + str);
	}
}