/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.scraper.converter.picatobibtex;

import static org.bibsonomy.util.ValidationUtils.present;

import java.text.Normalizer;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author daill
 */
public class PicaUtils {
	private static final Pattern PATTERN_CLEANSING = Pattern.compile("(@|&lt;.+?&gt;|\\{|\\}|\\[|\\])");
	
	/**
	 * Returns the first data entry for the given category and sub-category.
	 * 
	 * @param pica 
	 * @param category
	 * @param subCategory
	 * @return The first data entry for the given category and sub-category.
	 */
	public static String getSubCategory(final PicaRecord pica, final String category, final String subCategory) {
		return getSubCategory(pica, category, subCategory, "");
	}
	
	/**
	 * Returns the first data entry for the given category and sub-category. Returns the given defaultValue if field is not found
	 * 
	 * @param pica 
	 * @param category
	 * @param subCategory
	 * @param defaultValue the default to return when the field is not found
	 * @return The first data entry for the given category and sub-category - or default.
	 */
	public static String getSubCategory(final PicaRecord pica, final String category, final String subCategory, String defaultValue) {
		final Row row = pica.getRow(category);
		
		if (present(row)) {
			final List<String> subField = row.getSubField(subCategory);
			if (present(subField)) {
				final String rVal = subField.get(0);
				if (rVal != null) {
					return Normalizer.normalize(rVal, Normalizer.Form.NFC);
				}
			}
		}
		return defaultValue;
	}
	
	/**
	 * Returns all data entries for the given category and sub-category.
	 * 
	 * @param pica 
	 * @param category
	 * @param subCategory
	 * @return The data entries for the given category and sub-category.
	 */
	public static List<String> getSubCategoryAll(final PicaRecord pica, final String category, final String subCategory) {
		final Row row = pica.getRow(category);
		
		if (present(row) && row.isExisting(subCategory)) {
			return row.getSubField(subCategory);
		}
		return Collections.emptyList();
	}
	
	
	
	/**
	 * Tries to clean the given String from i.e. internal references like @
	 * 
	 * @param toClean
	 * @return String
	 */
	public static String cleanString(final String toClean) {
		if (toClean != null) {
			return PATTERN_CLEANSING.matcher(toClean).replaceAll("").trim();
		}
		return "";
	}
	
	/**
	 * Replace "XML=1.0/CHARSET=UTF-8/PRS=PP" in the url
	 * 
	 * @param url
	 * @return formatted url
	 */
	public static String prepareUrl(final String url){
		return url.replaceFirst("XML=1.0/CHARSET=UTF-8/PRS=PP/", "");
	}
}
