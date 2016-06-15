package org.bibsonomy.database.common.typehandler;

import static org.bibsonomy.util.ValidationUtils.present;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.model.user.settings.FavouriteLayout;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;

/**
 * a callback for {@link FavouriteLayout}s
 *
 * @author jp
 */
public class FavouriteLayoutsTypeHandlerCallback extends AbstractTypeHandlerCallback {
	private static final String FAV_SEP = "/";
	private static final String SEP = ",";

	/**
	 * implements a simple comparator which compares the string representation of two favLs
	 * the Strings are "source"/"style" and should therefore be unique
	 */
	private static final class favlsComparator implements Comparator<FavouriteLayout> {
		@Override
		public int compare(FavouriteLayout o1, FavouriteLayout o2) {
			int diff = o1.getStyle().compareTo(o2.getStyle());
			if (diff == 0){
				diff = o1.getSource().toString().compareTo(o2.getSource().toString());
			}
			return diff;
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
		if (parameter == null) {
			setter.setString(null);
			return;
		}
		final List<FavouriteLayout> favls = new LinkedList<FavouriteLayout>(new LinkedHashSet<FavouriteLayout>((List<FavouriteLayout>) parameter));
		if (favls.isEmpty()) {
			setter.setString(null);
		} else {
			// sort them
			Collections.sort(favls, new favlsComparator());
			
			final StringBuilder toBeSet = new StringBuilder();
			final Iterator<FavouriteLayout> iterator = favls.iterator();
			while (iterator.hasNext()) {
				final FavouriteLayout fav = iterator.next();
				toBeSet.append(toString(fav));
				if (iterator.hasNext()) {
					toBeSet.append(SEP);
				}
			}
			//sets the DB String as '"source1"/"style1", "source2","style2"'
			//ofc without the '"'
			setter.setString(toBeSet.toString().toUpperCase());
		}
	}

	/**
	 * @param fav
	 * @return
	 */
	private static String toString(FavouriteLayout fav) {
		return fav.getSource() + FAV_SEP + fav.getStyle();
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
		final List<FavouriteLayout> favouriteLayouts = new LinkedList<FavouriteLayout>();
		if (!present(str)) {
			return favouriteLayouts;
		}
		
		// split at SEP
		// so for each index in strInLines there should be exactly one style
		final String[] strInLines = str.split(SEP);
		
		for (String favLayoutString : strInLines) {
			favLayoutString = favLayoutString.trim();
			// splitting at FAV_SEP. Now sourceAndStyle[0] = source and sourceAndStyle[1] = style
			final String sourceAndStyle[] = favLayoutString.split(FAV_SEP);
			if (sourceAndStyle.length != 2){
				throw new IllegalArgumentException("Format has to be 'source/style', but was: " + sourceAndStyle);
			}
			
			final FavouriteLayout favouriteLayout;
			//setting the source and style
			final String source = sourceAndStyle[0];
			final String style = sourceAndStyle[1];
			favouriteLayout = new FavouriteLayout(source,style);
			favouriteLayouts.add(favouriteLayout);
		}
		Collections.sort(favouriteLayouts, new favlsComparator());
		return favouriteLayouts;
	}
}
