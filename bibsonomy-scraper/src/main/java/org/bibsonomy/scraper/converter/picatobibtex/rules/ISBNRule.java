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
public class ISBNRule extends Rules {

	private static final String CAT_A = "004A";
	private static final String CAT_D = "004D";

	/**
	 * @param pica
	 */
	public ISBNRule(final PicaRecord pica){
		super(pica, null);
	}

	@Override
	public String getContent() {
		String res = null;
		if (this.pica.isExisting(CAT_A)) {
			res = PicaUtils.getSubCategory(this.pica, CAT_A, "$0"); // often ISBN-10
			if (!present(res)) {
				res = PicaUtils.getSubCategory(this.pica, CAT_A, "$A"); // often ISBN-13
			}
		} else if (this.pica.isExisting(CAT_D)) {
			res = PicaUtils.getSubCategory(this.pica, CAT_D, "$0");
			if (!present(res)) {
				res = PicaUtils.getSubCategory(this.pica, CAT_D, "$A");
			}
		}

		return PicaUtils.cleanString(res);
	}

	@Override
	public boolean isAvailable() {
		return this.pica.isExisting(CAT_A) || this.pica.isExisting(CAT_D);
	}

}
