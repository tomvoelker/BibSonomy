package org.bibsonomy.scraper.converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

	/**
	 * @param r reader to read data from
	 * @return list of PicaRecords in the data
	 * @throws IOException if some io stuff in the reader goes wrong
	 */
	public Collection<PicaRecord> parseRawPicaPlus(BufferedReader r) throws IOException {
		List<PicaRecord> rVal = new ArrayList<PicaRecord>();
		PicaRecord openPicaRecord = null;
		String s;
		while ((s = r.readLine()) != null) {
			if (s.length() == 0) {
				continue;
			}
			if (s.contains("PPN:")) {
				//a new record begins
				if (openPicaRecord != null) {
					rVal.add(openPicaRecord);
				}
				String ppn = s.substring(s.indexOf("PPN:") + 5, s.indexOf("PPN:") + 5 + 9);
				Row ppnRow = readRow("003@ \u01920"+ppn);
				openPicaRecord = new PicaRecord();
				openPicaRecord.addRow(ppnRow);
			} else {
				Row row = readRow(s);
				if ((openPicaRecord != null) && (row != null)) {
					openPicaRecord.addRow(row);
				} else {
					log.warn("row before first ppn row: " + s);
				}
			}
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
	
		// get Subfields
		String[] subfields = line.substring(pos + 1).split("\u0192");
		for (String sub : subfields) {
			if (sub.length() > 1) {				
				rVal.addSubField("$" + sub.substring(0, 1), sub.substring(1));
			}
		}
		return rVal;
	}

}
