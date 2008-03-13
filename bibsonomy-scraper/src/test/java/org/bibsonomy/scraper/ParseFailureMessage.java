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
