package org.bibsonomy.community.importer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.importer.parser.DataInputParser;
import org.bibsonomy.community.util.Pair;
import org.bibsonomy.community.util.Triple;

public class CSVReader {
	private static final Log log = LogFactory.getLog(CSVReader.class); 
	private String delimiter = "\t";

	protected <T,U> void loadFile(String fileName, Collection<Pair<T,U>> map, DataInputParser<T> parseFirst, DataInputParser<U> parseSecond) throws IOException {
		BufferedReader input =  new BufferedReader(new FileReader(fileName));
		
		String nextLine = null;
		while( (nextLine=input.readLine())!=null ) {
			int pos = nextLine.indexOf(getDelimiter());
			if( pos >= 0 ) {
				String first  = nextLine.substring(0, pos);
				String second = nextLine.substring(pos+1);
				
				map.add(new Pair<T,U>(parseFirst.parseString(first), parseSecond.parseString(second)));
			}
		}
	}
	
	protected <T,U> void loadFile(String fileName, int srcCol, int dstCol, Collection<Pair<T,U>> map, DataInputParser<T> parseFirst, DataInputParser<U> parseSecond) throws IOException {
		BufferedReader input =  new BufferedReader(new FileReader(fileName));
		
		String nextLine = null;
		while( (nextLine=input.readLine())!=null ) {
			String[] cells = nextLine.split(getDelimiter());
			if( Math.max(srcCol,dstCol)>=cells.length ) {
				log.error("Given columns ("+srcCol+"/"+dstCol+" don't exist in input string '"+nextLine+"'");
			} else {
				map.add(new Pair<T,U>(parseFirst.parseString(cells[srcCol]), parseSecond.parseString(cells[dstCol])));
			}
		}
	}
	
	
	protected <T,U,V> void loadFile(String fileName, int fstCol, int sndCol, int trdCol, Collection<Triple<T,U,V>> map, DataInputParser<T> parseFirst, DataInputParser<U> parseSecond, DataInputParser<V> parseThird) throws IOException {
		BufferedReader input =  new BufferedReader(new FileReader(fileName));
		
		String nextLine = null;
		while( (nextLine=input.readLine())!=null ) {
			String[] cells = nextLine.split(getDelimiter());
			if( Math.max(fstCol, Math.max(sndCol,trdCol))>=cells.length ) {
				log.error("Given columns ("+fstCol+"/"+sndCol+"/"+trdCol+") don't exist in input string '"+nextLine+"'");
			} else {
				map.add(new Triple<T,U,V>(parseFirst.parseString(cells[fstCol]), parseSecond.parseString(cells[sndCol]), parseThird.parseString(cells[trdCol])));
			}
		}
		
	}	

	protected Collection<String[]> loadFile(String fileName) throws IOException {
		BufferedReader input =  new BufferedReader(new FileReader(fileName));
		Collection<String[]> retVal = new ArrayList<String[]>();
		
		String nextLine = null;
		while( (nextLine=input.readLine())!=null ) {
			retVal.add(nextLine.split(getDelimiter()));
		}
		
		return retVal;
	}
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getDelimiter() {
		return delimiter;
	}	
}
