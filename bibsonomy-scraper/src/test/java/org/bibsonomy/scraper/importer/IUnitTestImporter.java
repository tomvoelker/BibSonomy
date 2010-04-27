/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
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

package org.bibsonomy.scraper.importer;

import java.util.List;

import org.bibsonomy.scraper.ScraperUnitTest;


/**
 * interface which descripes all needed methods for Importer 
 * @author tst
 *
 */
public interface IUnitTestImporter {
	
	/**
	 * Reads tests from external source and generate a List which contains
	 * ScraperUnitTests.
	 * @return List with ScraperUnitTests
	 * @throws Exception
	 */
	public List<ScraperUnitTest> getUnitTests() throws Exception;

}
