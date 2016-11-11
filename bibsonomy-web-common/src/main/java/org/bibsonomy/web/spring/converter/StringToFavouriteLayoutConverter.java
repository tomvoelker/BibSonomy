package org.bibsonomy.web.spring.converter;

import org.bibsonomy.model.user.settings.FavouriteLayout;
import org.springframework.core.convert.converter.Converter;

/**
 * converts a string to a {@link FavouriteLayout}
 *
 * @author pfister
 */
public class StringToFavouriteLayoutConverter implements Converter<String, FavouriteLayout> {
	
	/* (non-Javadoc)
	 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public FavouriteLayout convert(String value) {
		// splitting at "/". Now sourceAndStyle[0] = source and sourceAndStyle[1] = style
		final String sourceAndStyle[] = value.split("/");
		if (sourceAndStyle.length != 2) {
			throw new IllegalArgumentException("Format has to be source/style");
		}
		return new FavouriteLayout(sourceAndStyle[0], sourceAndStyle[1]);
	}
}
