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
	public FavouriteLayout convert(String value) {
		
		//splitting at "/". Now sourceAndStyle[0] = source and sourceAndStyle[1] = style
		String sourceAndStyle[] = value.split("/");
		if(sourceAndStyle.length != 2){
			throw new IllegalArgumentException("Format has to be source/style");
		}
		FavouriteLayout favl;
		if(sourceAndStyle[1].compareToIgnoreCase("BibTeX")==0){
			favl = new FavouriteLayout(FavouriteLayoutSource.valueOf(sourceAndStyle[0]), "BibTeX");
		} else if(sourceAndStyle[1].compareToIgnoreCase("EndNote")==0){
			favl = new FavouriteLayout(FavouriteLayoutSource.valueOf(sourceAndStyle[0]), "EndNote");
		} else {
			favl = new FavouriteLayout(FavouriteLayoutSource.valueOf(sourceAndStyle[0].toUpperCase()), sourceAndStyle[1]);
		}
		return favl;
	}
	
}
