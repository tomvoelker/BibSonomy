package org.bibsonomy.database.common.typehandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.bibsonomy.model.enums.FavouriteLayoutSource;
import org.bibsonomy.model.user.settings.FavouriteLayout;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;

/**
 *
 *
 * @author jp
 */
public class FavouriteLayoutsTypeHandlerCallback extends AbstractTypeHandlerCallback {
	
	//implements a simple comparator which compares the string represantation of two favLs
	//the Strings are "source"/"style" and should therefore be unique
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
			
			//removing dupes O(n)
			favls = new ArrayList<FavouriteLayout>(new LinkedHashSet<FavouriteLayout>(favls));
			//sorting
			Collections.sort(favls, new favlsComparator());
			
			for (Iterator<FavouriteLayout> iterator = favls.iterator(); iterator.hasNext();) {
				FavouriteLayout fav = iterator.next();
				toBeSet += fav.toString() + ", ";
			}
			//removing the last ", "
			toBeSet = toBeSet.trim();
			toBeSet = toBeSet.substring(0, toBeSet.length()-1);
			//sets the DB String as '"source1"/"style1", "source2","style2"'
			//ofc without the '"'
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
		
		//no null or empty strings
		if(str == null || str.trim().isEmpty()){
			return returner;
		}
		
		//splitting at ","
		//so for each index in strInLines there should be exactly one style
		String[] strInLines = str.split(",");
		
		//bit of trimming
		for (String string : strInLines) {
			string = string.trim();
			cleanedStr.add(string);
		}
		
		//iterating for each style
		for (String element : cleanedStr) {
			//splitting at "/". Now sourceAndStyle[0] = source and sourceAndStyle[1] = style
			String sourceAndStyle[] = element.split("/");
			
			if(sourceAndStyle.length != 2){
				throw new IllegalArgumentException("Format has to be source/style");
			}
			
			FavouriteLayout favl;
			//setting the source and style
			if(sourceAndStyle[1].compareToIgnoreCase("BibTeX")==0){
				favl = new FavouriteLayout(FavouriteLayoutSource.valueOf(sourceAndStyle[0]), "BibTeX");
			} else if(sourceAndStyle[1].compareToIgnoreCase("EndNote")==0){
				favl = new FavouriteLayout(FavouriteLayoutSource.valueOf(sourceAndStyle[0]), "EndNote");
			} else {
				favl = new FavouriteLayout(FavouriteLayoutSource.valueOf(sourceAndStyle[0].toUpperCase()), sourceAndStyle[1]);
			}
			returner.add(favl);
		}
		//Collections.sort(returner, new favlsComparator());
		return returner;
	}
}
