/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
 */
public class PicaPlusReader {
	private static final String PPN = "PPN:";

	private static final Log log = LogFactory.getLog(PicaPlusReader.class);
	
	private static final Pattern CATEGORY_PATTERN = Pattern.compile("^\\d{3}+.*");
	private static final String DEFAULT_SUBFIELD_SEPARATOR = "\u0192";

	/**
	 * @param r reader to read data from
	 * @return list of PicaRecords in the data
	 * @throws IOException if some io stuff in the reader goes wrong
	 */
	public Collection<PicaRecord> parseRawPicaPlus(final BufferedReader r) throws IOException {
		final List<PicaRecord> picaRecords = new ArrayList<PicaRecord>();
		PicaRecord openPicaRecord = null;
		String s;
		final Collection<Row> rowsBeforeFirstPPN = new ArrayList<Row>();
		while ((s = r.readLine()) != null) {
			if (s.length() == 0) {
				continue;
			}
			if (s.contains(PPN)) {
				if (rowsBeforeFirstPPN.size() > 0) {
					log.warn("rows before first ppn row: " + rowsBeforeFirstPPN);
					rowsBeforeFirstPPN.clear();
				}
				//a new record begins
				if (openPicaRecord != null) {
					picaRecords.add(openPicaRecord);
				}
				final int ppnIndex = s.indexOf(PPN) + 5; // TODO: comment why + 5
				final String ppn = s.substring(ppnIndex, ppnIndex + 9); // TODO: comment why + 9
				final Row ppnRow = readRow("003@ \u01920" + ppn);
				openPicaRecord = new PicaRecord();
				openPicaRecord.addRow(ppnRow);
			} else if (CATEGORY_PATTERN.matcher(s).matches()) {
				final Row row = readRow(s);
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
			for (final Row row : rowsBeforeFirstPPN) {
				openPicaRecord.addRow(row);
			}
			picaRecords.add(openPicaRecord);
		}
		return picaRecords;
	}
	
	private Row readRow(String line) {
		// get Field
		final int pos = line.indexOf(" ");
		final String[] data = line.substring(0, pos).split("/");
		final String tag = data[0];
		final Row rVal = new Row(tag);
		if (data.length > 1) {
			// TODO: remove?!
			//rVal.setOccurrence(data[1]);
		}
	
		//replace possible subfield separators with default one
		line = line.replace(" $", DEFAULT_SUBFIELD_SEPARATOR);

		// get Subfields
		final String[] subfields = line.substring(pos + 1).split(DEFAULT_SUBFIELD_SEPARATOR);
		for (final String sub : subfields) {
			if (sub.length() > 1) {				
				rVal.addSubField("$" + sub.substring(0, 1), sub.substring(1));
			}
		}
		return rVal;
	}

}
