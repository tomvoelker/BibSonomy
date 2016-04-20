package org.bibsonomy.web.spring.converter;


import org.bibsonomy.model.enums.FavouriteLayoutSource;
import org.bibsonomy.model.user.settings.FavouriteLayout;
import org.springframework.core.convert.converter.Converter;

/**
 * TODO: add documentation to this class
 *
 * @author pfister
 */
public class StringToFavouriteLayoutConverter implements Converter<String, FavouriteLayout>{

	/* (non-Javadoc)
	 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public FavouriteLayout convert(String sourceandstyle) {		
		String splitted[] = sourceandstyle.split("/");
		if(splitted.length != 2){
			throw new IllegalArgumentException("Foramt has to be source/style");
		}
		FavouriteLayoutSource source = FavouriteLayoutSource.valueOf(sourceandstyle);
		String style = splitted[2];
		return new FavouriteLayout(source, style);
	}

}
