/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.scraper.converter.picatobibtex.rules;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.scraper.converter.picatobibtex.PicaRecord;
import org.bibsonomy.scraper.converter.picatobibtex.PicaUtils;

/**
 * @author daill
 * @version $Id$
 */
public class TitleRule extends Rules {
	
	private static final String CAT_2 = "036C";
	private static final String CAT_1 = "021A";

	/**
	 * @param pica
	 */
	public TitleRule(final PicaRecord pica){
		super(pica, null);
	}
	
	@Override
	public String getContent() {
		final String cat1 = PicaUtils.getSubCategory(this.pica, CAT_1, DEFAULT_SUB_CATEGORY);
		if (present(cat1)) {
			return PicaUtils.cleanString(cat1);
		}
		return PicaUtils.cleanString(PicaUtils.getSubCategory(this.pica, CAT_2, DEFAULT_SUB_CATEGORY));
	}

	@Override
	public boolean isAvailable() {
		return this.pica.isExisting(CAT_1) || this.pica.isExisting(CAT_2);
	}

}
