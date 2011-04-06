package org.bibsonomy.opensocial.oauth.database.typehandler;

import java.sql.SQLException;

import org.apache.shindig.social.opensocial.oauth.OAuthEntry.Type;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class TokenTypeHandler implements TypeHandlerCallback{

	public Object getResult(ResultGetter arg) throws SQLException {
		return valueOf(arg.getString());
	}

	public void setParameter(ParameterSetter param, Object value) throws SQLException {
		Type tokenType = (Type) value;
		if( value!=null ) {
			param.setInt(tokenType.ordinal());
		}
	}

	public Object valueOf(String arg) {
		if ("0".equals(arg)) {
			return Type.REQUEST;
		} else if ("1".equals(arg)) {
			return Type.ACCESS;
		} else if ("2".equals(arg)) {
			return Type.DISABLED;
		} else {
			throw new RuntimeException("Given token type ('"+arg+"') not supported.");
		}
	}

}
