package org.bibsonomy.database.common.typehandler;

import java.sql.SQLException;
import java.util.ArrayList;

import org.bibsonomy.common.enums.FavouriteLayouts;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;

/**
 * TODO: add documentation to this class
 *
 * @author pfister
 */
public class FavouriteLayoutsTypeHandlerCallback extends AbstractTypeHandlerCallback {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#setParameter(com.
	 * ibatis.sqlmap.client.extensions.ParameterSetter, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setParameter(ParameterSetter setter, Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setString(null);
		} else {
			String saveString = ((ArrayList<FavouriteLayouts>) parameter).toString();	
			saveString = saveString.replace('[', ' ');
			saveString = saveString.replace(']', ' ');
			saveString = saveString.trim();
			setter.setString(saveString);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#valueOf(java.lang
	 * .String)
	 */
	@Override
	public Object valueOf(String str) {
		ArrayList<FavouriteLayouts> returner = new ArrayList<FavouriteLayouts>();
		ArrayList<String> cleanedStr = new ArrayList<String>();
		String[] strInLines = str.split(",");

		for (String string : strInLines) {
			string = string.trim();
			cleanedStr.add(string);
		}
		for (String element : cleanedStr) {
			returner.add(Enum.valueOf(FavouriteLayouts.class, element));
		}
		return returner;
	}
}
