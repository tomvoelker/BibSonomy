package org.bibsonomy.database.common.typehandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	
	
	class favlsComparator implements Comparator<FavouriteLayout> {
	    @Override
	    public int compare(FavouriteLayout o1, FavouriteLayout o2) {
	        return o1.getStyle().compareTo(o2.getStyle());
	    }
	}
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
		
		ArrayList<FavouriteLayout> favls = (ArrayList<FavouriteLayout>) parameter;
		
		if (parameter == null) {
			setter.setString(null);
		} else if(favls.isEmpty()) {
			setter.setString(null);
		} else {
			String toBeSet = "";
			
					
			Collections.sort(favls, new favlsComparator());
			
			for (Iterator<FavouriteLayout> iterator = favls.iterator(); iterator.hasNext();) {
				FavouriteLayout fav = iterator.next();
				toBeSet += fav.toString() + ", ";
			}
			//removing the last ", "
			toBeSet = toBeSet.trim();
			toBeSet = toBeSet.substring(0, toBeSet.length()-1);
			//
			setter.setString(toBeSet.toUpperCase());
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
			FavouriteLayout favl;
			if(sourceAndStyle[1].compareToIgnoreCase("BibTeX")==0){
				favl = new FavouriteLayout(FavouriteLayoutSource.valueOf(sourceAndStyle[0]), "BibTeX");
			} else if(sourceAndStyle[1].compareToIgnoreCase("Endnote")==0){
				favl = new FavouriteLayout(FavouriteLayoutSource.valueOf(sourceAndStyle[0]), "Endnote");
			} else {
				favl = new FavouriteLayout(FavouriteLayoutSource.valueOf(sourceAndStyle[0].toUpperCase()), sourceAndStyle[1]);
			}
			returner.add(favl);
		}
		//Collections.sort(returner, new favlsComparator());
		return returner;
	}
}
