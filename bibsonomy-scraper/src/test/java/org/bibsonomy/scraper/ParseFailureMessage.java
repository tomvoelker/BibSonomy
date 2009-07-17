/**
 *  
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.scraper;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;

/**
 * This class implements the standard output for occcured Exceptions outside the testing.
 * @author tst
 *
 */
public class ParseFailureMessage {
	
	private static final Logger log = Logger.getLogger(ParseFailureMessage.class);
	
	/**
	 * Standardoutput for Exceptions
	 * @param e occured Exception
	 * @param testId ID from test which might be involved in e
	 */
	public static void printParseFailureMessage(Exception e, String testId){

		// PrintWriter is used to add stacktrace to log
		StringWriter swriter = new StringWriter();
		PrintWriter pwriter = new PrintWriter(swriter, true);
		
		pwriter.println();
		pwriter.println("*******************************************************************************");
		pwriter.println("Failure during parsing UnitTestData.xml at Element: " + testId);
		e.printStackTrace(pwriter);
		pwriter.println("*******************************************************************************");
		
		pwriter.flush();
		pwriter.close();
		swriter.flush();
		
		/*
		 * fatal because e is not hit during the running of the test.
		 * e occcurs befor or after testing.
		 */
		log.fatal(swriter.toString());
	}
	

}
