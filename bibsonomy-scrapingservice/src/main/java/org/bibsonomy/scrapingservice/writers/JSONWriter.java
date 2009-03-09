package org.bibsonomy.scrapingservice.writers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Tuple;

/**
 * Writes given lists in JSON format.
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class JSONWriter {

	private final BufferedOutputStream outputStream;
	
	public JSONWriter (final OutputStream outputStream) {
		super();
		this.outputStream = new BufferedOutputStream(outputStream);
	}
	
	public void write(final Collection<Tuple<Pattern, Pattern>> patterns) throws UnsupportedEncodingException, IOException {
		int ctr = 0;
		int max = patterns.size();
		outputStream.write("[\n".getBytes("UTF-8"));
		for (final Tuple<Pattern, Pattern> tuple : patterns) {
			ctr++;
			outputStream.write("\t{\n".getBytes("UTF-8"));
			
			outputStream.write(("\t\t\"host\" : \"" + tuple.getFirst()  + "\",\n").getBytes("UTF-8"));
			outputStream.write(("\t\t\"path\" : \"" + tuple.getSecond() + "\"\n").getBytes("UTF-8"));

			outputStream.write("\t}".getBytes("UTF-8"));
			if (ctr < max) {
				/*
				 * not the last element: print delimiter
				 */
				outputStream.write(",".getBytes("UTF-8"));
			}
			outputStream.write("\n".getBytes("UTF-8"));
		}
		outputStream.write("]\n".getBytes("UTF-8"));
	}
	
	public void close() throws IOException {
		outputStream.close();
	}
}

