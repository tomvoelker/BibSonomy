/**
 *
 *  BibSonomy-Scrapingservice - Web application to test the BibSonomy web page scrapers (see
 * 		bibsonomy-scraper)
 *
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group,
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

package org.bibsonomy.scrapingservice.writers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.bibsonomy.scraper.KDEUrlCompositeScraper;
import org.bibsonomy.scraper.UrlCompositeScraper;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class JSONWriterTest {

	@Test
	public void testWrite() {
		
		final JSONWriter writer = new JSONWriter(System.out);
		final UrlCompositeScraper scraper = new KDEUrlCompositeScraper();
		
		System.out.println("------------------------------------------------------");
		try {
			writer.write(0, "{\n");
			writer.write(1, "\"patterns\" : ");
			writer.write(1, scraper.getUrlPatterns());
			writer.write(0, "}\n");
		} catch (UnsupportedEncodingException e) {
			Assert.fail(e.getMessage());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		System.out.println("------------------------------------------------------");
	}
	
	
	
}

