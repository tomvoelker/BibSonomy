package org.bibsonomy.web.spring.converter;


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
	public FavouriteLayout convert(String value) {
		
		//splitting at "/". Now sourceAndStyle[0] = source and sourceAndStyle[1] = style
		String sourceAndStyle[] = value.split("/");
		if(sourceAndStyle.length != 2){
			throw new IllegalArgumentException("Format has to be source/style");
		}
		FavouriteLayout favl;
		favl = new FavouriteLayout(sourceAndStyle[0], sourceAndStyle[1]);
		return favl;
	}
	
}
