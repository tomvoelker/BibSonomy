package org.bibsonomy.scraper.converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.scraper.converter.picatobibtex.PicaRecord;
import org.bibsonomy.scraper.converter.picatobibtex.Row;

/**
 * This is based on the PicaPlusXMLConverter from http://web10.ub.uni-rostock.de/uploads/stephan/PicaPlusXMLConverter/
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class PicaPlusReader {
	private static final Log log = LogFactory.getLog(PicaPlusReader.class);
	private static final Pattern CATEGORY_PATTERN = Pattern.compile("^\\d{3}+.*");
	private static final String DEFAULT_SUBFIELD_SEPARATOR = "\u0192";

	/**
	 * @param r reader to read data from
	 * @return list of PicaRecords in the data
	 * @throws IOException if some io stuff in the reader goes wrong
	 */
	public Collection<PicaRecord> parseRawPicaPlus(BufferedReader r) throws IOException {
		List<PicaRecord> rVal = new ArrayList<PicaRecord>();
		PicaRecord openPicaRecord = null;
		String s;
		Collection<Row> rowsBeforeFirstPPN = new ArrayList<Row>();
		while ((s = r.readLine()) != null) {
			if (s.length() == 0) {
				continue;
			}
			if (s.contains("PPN:")) {
				if (rowsBeforeFirstPPN.size() > 0) {
					log.warn("rows before first ppn row: " + rowsBeforeFirstPPN);
					rowsBeforeFirstPPN.clear();
				}
				//a new record begins
				if (openPicaRecord != null) {
					rVal.add(openPicaRecord);
				}
				String ppn = s.substring(s.indexOf("PPN:") + 5, s.indexOf("PPN:") + 5 + 9);
				Row ppnRow = readRow("003@ \u01920"+ppn);
				openPicaRecord = new PicaRecord();
				openPicaRecord.addRow(ppnRow);
			} else if (CATEGORY_PATTERN.matcher(s).matches()) {
				Row row = readRow(s);
				if ((openPicaRecord != null) && (row != null)) {
					openPicaRecord.addRow(row);
				} else {
					rowsBeforeFirstPPN.add(row);
				}
			}
		}
		if (rowsBeforeFirstPPN.size() > 0) {
			// hebis does not use PPN delimiters and describes only a single entity
			openPicaRecord = new PicaRecord();
			for (Row row : rowsBeforeFirstPPN) {
				openPicaRecord.addRow(row);
			}
			rVal.add(openPicaRecord);
		}
		return rVal;
	}
	
	private Row readRow(String line) {
		// get Field
		int pos = line.indexOf(" ");
		String[] data = line.substring(0, pos).split("/");
		String tag = data[0];
		Row rVal = new Row(tag);
		if (data.length > 1) {
			//rVal.setOccurrence(data[1]);
		}
	
		//replace possible subfield separators with default one
		line = line.replace(" $", DEFAULT_SUBFIELD_SEPARATOR);

		// get Subfields
		String[] subfields = line.substring(pos + 1).split(DEFAULT_SUBFIELD_SEPARATOR);
		for (String sub : subfields) {
			if (sub.length() > 1) {				
				rVal.addSubField("$" + sub.substring(0, 1), sub.substring(1));
			}
		}
		return rVal;
	}

}
