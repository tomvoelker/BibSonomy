package org.bibsonomy.opensocial.oauth.database.typehandler;

import java.sql.SQLException;

import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret.KeyType;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class KeyTypeHandler implements TypeHandlerCallback{

	public Object getResult(ResultGetter arg) throws SQLException {
		return valueOf(arg.getString());
	}

	public void setParameter(ParameterSetter param, Object value) throws SQLException {
		if( value!=null && value instanceof KeyType) {
			param.setInt(((KeyType)value).ordinal());
		}
	}

	public Object valueOf(String arg) {
		if ("0".equals(arg)) {
			return KeyType.values()[0];
		} else if ("1".equals(arg)) {
			return KeyType.values()[1];
		} else {
			throw new RuntimeException("Given key type ('"+arg+"') not supported.");
		}
	}

}
