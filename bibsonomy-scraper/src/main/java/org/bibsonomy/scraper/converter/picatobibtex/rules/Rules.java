/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.converter.picatobibtex.rules;

import org.bibsonomy.scraper.converter.picatobibtex.PicaRecord;
import org.bibsonomy.scraper.converter.picatobibtex.PicaUtils;

/**
 * @author daill
 */
public abstract class Rules {
	
	protected static final String DEFAULT_SUB_CATEGORY = "$a";
	protected final PicaRecord pica;
	protected final String category;
	
	protected Rules(final PicaRecord pica, final String category) {
		this.pica = pica;
		this.category = category;
	}
	
	
	/**
	 * Checks if the requested field is available
	 * 
	 * @return boolean
	 */
	public boolean isAvailable() {
		return this.pica.isExisting(this.category);
	}
	
	/**
	 * Gets the bibtex string part
	 * 
	 * @return string
	 */
	public String getContent() {
		final String abstr = PicaUtils.getSubCategory(this.pica, this.category, DEFAULT_SUB_CATEGORY);
		
		return PicaUtils.cleanString(abstr);
	}
}
