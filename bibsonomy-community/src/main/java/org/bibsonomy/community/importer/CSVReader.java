package org.bibsonomy.community.importer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.community.importer.parser.DataInputParser;
import org.bibsonomy.community.util.Triple;

public class CSVReader {
	private static final Log log = LogFactory.getLog(CSVReader.class); 
	private String delimiter = "\t";

	protected <T,U> void loadFile(final String fileName, final Collection<Pair<T,U>> map, final DataInputParser<T> parseFirst, final DataInputParser<U> parseSecond) throws IOException {
		final BufferedReader input =  new BufferedReader(new FileReader(fileName));
		
		String nextLine = null;
		while( (nextLine=input.readLine())!=null ) {
			final int pos = nextLine.indexOf(getDelimiter());
			if( pos >= 0 ) {
				final String first  = nextLine.substring(0, pos);
				final String second = nextLine.substring(pos+1);
				
				map.add(new Pair<T,U>(parseFirst.parseString(first), parseSecond.parseString(second)));
			}
		}
	}
	
	protected <T,U> void loadFile(final String fileName, final int srcCol, final int dstCol, final Collection<Pair<T,U>> map, final DataInputParser<T> parseFirst, final DataInputParser<U> parseSecond) throws IOException {
		final BufferedReader input =  new BufferedReader(new FileReader(fileName));
		
		String nextLine = null;
		while( (nextLine=input.readLine())!=null ) {
			final String[] cells = nextLine.split(getDelimiter());
			if( Math.max(srcCol,dstCol)>=cells.length ) {
				log.error("Given columns ("+srcCol+"/"+dstCol+" don't exist in input string '"+nextLine+"'");
			} else {
				map.add(new Pair<T,U>(parseFirst.parseString(cells[srcCol]), parseSecond.parseString(cells[dstCol])));
			}
		}
	}
	
	
	protected <T,U,V> void loadFile(final String fileName, final int fstCol, final int sndCol, final int trdCol, final Collection<Triple<T,U,V>> map, final DataInputParser<T> parseFirst, final DataInputParser<U> parseSecond, final DataInputParser<V> parseThird) throws IOException {
		final BufferedReader input =  new BufferedReader(new FileReader(fileName));
		
		String nextLine = null;
		while( (nextLine=input.readLine())!=null ) {
			final String[] cells = nextLine.split(getDelimiter());
			if( Math.max(fstCol, Math.max(sndCol,trdCol))>=cells.length ) {
				log.error("Given columns ("+fstCol+"/"+sndCol+"/"+trdCol+") don't exist in input string '"+nextLine+"'");
			} else {
				map.add(new Triple<T,U,V>(parseFirst.parseString(cells[fstCol]), parseSecond.parseString(cells[sndCol]), parseThird.parseString(cells[trdCol])));
			}
		}
		
	}	

	protected Collection<String[]> loadFile(final String fileName) throws IOException {
		final BufferedReader input =  new BufferedReader(new FileReader(fileName));
		final Collection<String[]> retVal = new ArrayList<String[]>();
		
		String nextLine = null;
		while( (nextLine=input.readLine())!=null ) {
			retVal.add(nextLine.split(getDelimiter()));
		}
		
		return retVal;
	}
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------

	public void setDelimiter(final String delimiter) {
		this.delimiter = delimiter;
	}

	public String getDelimiter() {
		return delimiter;
	}	
}
