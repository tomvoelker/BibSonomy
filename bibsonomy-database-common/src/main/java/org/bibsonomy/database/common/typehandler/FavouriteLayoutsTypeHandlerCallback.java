package org.bibsonomy.database.common.typehandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.bibsonomy.model.enums.FavouriteLayoutSource;
import org.bibsonomy.model.user.settings.FavouriteLayout;

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
		} else if(((ArrayList<FavouriteLayout>) parameter).isEmpty()) {
			setter.setString(null);
		} else {
			String toBeSet = "";
			for (Iterator<FavouriteLayout> iterator = ((ArrayList<FavouriteLayout>) parameter).iterator(); iterator.hasNext();) {
				FavouriteLayout fav = iterator.next();
				toBeSet += fav.toString() + ", ";
			}
			//removing the last ", "
			toBeSet = toBeSet.trim();
			toBeSet = toBeSet.substring(0, toBeSet.length());
			//
			setter.setString(toBeSet);
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
		ArrayList<FavouriteLayout> returner = new ArrayList<FavouriteLayout>();
		ArrayList<String> cleanedStr = new ArrayList<String>();
		
		if(str == null || str.trim().isEmpty()){
			return returner;
		}
		
		String[] strInLines = str.split(",");

		for (String string : strInLines) {
			string = string.trim();
			cleanedStr.add(string);
		}
		for (String element : cleanedStr) {
			String sourceAndStyle[] = element.split("/");
			returner.add(new FavouriteLayout(FavouriteLayoutSource.valueOf(sourceAndStyle[0]), sourceAndStyle[1]));
		}
		return returner;
	}
}
