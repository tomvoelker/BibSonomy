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


import org.bibsonomy.scraper.converter.picatobibtex.PicaRecord;
import org.bibsonomy.scraper.converter.picatobibtex.PicaUtils;
import org.bibsonomy.scraper.converter.picatobibtex.Row;

/**
 * @author daill
 * @version $Id$
 */
public class TagsRule extends Rules {

	private static final String CAT_1 = "044K";
	private static final String CAT_2 = "041A";

	/**
	 * @param pica
	 */
	public TagsRule(final PicaRecord pica){
		super(pica, null);
	}

	@Override
	public String getContent() {
		final StringBuilder tags = new StringBuilder();

		for (final String tag : PicaUtils.getSubCategoryAll(pica, CAT_1, "$8")) {
			tags.append(tag).append(" ");
		}
		
		for (final String tag : PicaUtils.getSubCategoryAll(pica, CAT_2, "$8")) {
			tags.append(tag).append(" ");
		}

		if (this.pica.isExisting(CAT_2)) {

			int ctr = 1;

			Row row = this.pica.getRow(CAT_2 + "/0" + Integer.toString(ctr));

			while (row != null) {
				String newCat = CAT_2 + "/0" + Integer.toString(ctr);

				if (row.isExisting("$8")) {
					tags.append(PicaUtils.getSubCategory(this.pica, newCat, "$8")).append(" ");
				}

				ctr++;

				if (ctr < 10){
					row = this.pica.getRow(CAT_2 + "/0" + Integer.toString(ctr));
				} else {
					row = this.pica.getRow(CAT_2 + "/" + Integer.toString(ctr));
				}
			}
		}

		return PicaUtils.cleanString(tags.toString());
	}

	@Override
	public boolean isAvailable() {
		return this.pica.isExisting(CAT_1) || this.pica.isExisting(CAT_2);
	}

}
