/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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

package org.bibsonomy.scraper.converter;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;

import org.bibsonomy.scraper.converter.picatobibtex.PicaRecord;
import org.bibsonomy.util.StringUtils;
import org.junit.Test;

/**
 * @author jensi
 */
public class PicaPlusToBibtexTest {
	
	/**
	 * more like a main
	 * @throws Exception 
	 */
	@Test
	public void readPicaPlus() throws Exception {
		final PicaPlusReader reader = new PicaPlusReader();
		final BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("dwl20100116-01p.txt"), StringUtils.CHARSET_UTF_8));
		final Collection<PicaRecord> picas = reader.parseRawPicaPlus(br);
		br.close();
		assertEquals(133, picas.size());
	}
}
