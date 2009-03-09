package org.bibsonomy.scrapingservice.writers;

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

	private static final String[] depths = new String[] {
		"", 
		"\t", 
		"\t\t", 
		"\t\t\t", 
		"\t\t\t\t", 
		"\t\t\t\t\t", 
		"\t\t\t\t\t\t", 
		"\t\t\t\t\t\t\t",
		"\t\t\t\t\t\t\t\t\t",
		"\t\t\t\t\t\t\t\t\t\t",
		"\t\t\t\t\t\t\t\t\t\t\t",
	};
	
	private final OutputStream outputStream;
	
	public JSONWriter (final OutputStream outputStream) {
		super();
		this.outputStream = outputStream;
	}
	
	public void write(int depth, final Collection<Tuple<Pattern, Pattern>> patterns) throws UnsupportedEncodingException, IOException {
		int ctr = 0;
		int max = patterns.size();
		write(depth, "[\n");
		for (final Tuple<Pattern, Pattern> tuple : patterns) {
			ctr++;
			depth++;
			write(depth, "{\n");
			
			depth++;
			write(depth, "\"host\" : \"" + tuple.getFirst()  + "\",\n");
			write(depth, "\"path\" : \"" + tuple.getSecond() + "\"\n");
			depth--;
			
			write(depth, "}");
			if (ctr < max) {
				/*
				 * not the last element: print delimiter
				 */
				write(",");
			}
			write("\n");
			depth--;
		}
		write(depth, "]\n");
	}
	
	public void write(final String s) throws UnsupportedEncodingException, IOException {
		outputStream.write(s.getBytes("UTF-8"));
	}
	
	public void write(final int depth, final String s) throws UnsupportedEncodingException, IOException {
		outputStream.write((getDepth(depth) + s).getBytes("UTF-8"));
	}
	
	
	private static String getDepth(final int depth) {
		if (depth >= depths.length) return depths[depths.length - 1];
		return depths[depth];
	}
	
	
}

