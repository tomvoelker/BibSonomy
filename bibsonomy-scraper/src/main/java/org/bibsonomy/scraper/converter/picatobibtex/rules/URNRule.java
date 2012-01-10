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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.converter.picatobibtex.PicaRecord;
import org.bibsonomy.scraper.converter.picatobibtex.PicaUtils;


/**
 * @author daill
 * @version $Id$
 */
public class URNRule extends Rules {
	/*
	 * to validate the URN
	 */
	private static final Pattern PATTERN_URN = Pattern.compile("^.*(urn:.*:.*)$");

	/**
	 * @param pica
	 */
	public URNRule(final PicaRecord pica) {
		super(pica, "004U");
	}

	@Override
	public String getContent() {
		String res = PicaUtils.getSubCategory(this.pica, this.category, "$0");
		
		// need to validate the urn
		final Matcher matcher = PATTERN_URN.matcher(res);
		
		if (matcher.find()) {
			res = matcher.group(1);
		}
		
		return PicaUtils.cleanString(res);
	}

}
