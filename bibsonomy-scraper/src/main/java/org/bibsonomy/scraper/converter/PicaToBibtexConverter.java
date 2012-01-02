/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
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

package org.bibsonomy.scraper.converter;


import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.scraper.converter.picatobibtex.PicaParser;
import org.bibsonomy.scraper.converter.picatobibtex.PicaRecord;
import org.bibsonomy.scraper.converter.picatobibtex.Row;
import org.bibsonomy.scraper.converter.picatobibtex.SubField;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * @author C. Kramer
 * @version $Id$
 */
public class PicaToBibtexConverter {
	private static final Log log = LogFactory.getLog(PicaToBibtexConverter.class);
	
	private final static Pattern PATTERN_LONGTITLE = Pattern.compile("(?s)<LONGTITLE.*?>(.*)</LONGTITLE>");
	private final static Pattern PATTERN_PICA_CATEGORY = Pattern.compile("^(\\d{3}[A-Z@]{1}/\\d{2}|\\d{3}[A-Z@]{1}).*$");
	private final static Pattern PATTERN_PICA_CATEGORY_SUBFIELD = Pattern.compile("(\\$[0-9a-zA-Z]{1})([^\\$]+)");

	private final PicaRecord pica;
	private final String url;
	
	/**
	 * Convert the pica content to the pica object structure
	 * 
	 * @param sc 
	 * @param type
	 * @param url
	 */
	public PicaToBibtexConverter(final String sc, final String type, final String url){
		this.pica = new PicaRecord();
		this.url = url;
		
		parseContent(sc);
	}
	
	/**
	 * Creates an PicaRecord object out of the given content
	 * 
	 * @param sc
	 */
	private void parseContent(String sc){
		try {
			
			// get the content of the XML tag <longtitle></longtitle>
			final Matcher matcher = PATTERN_LONGTITLE.matcher(sc);
			
			// if there is content save it, if not throw exception
			if (!matcher.find()){
				throw new ScrapingException("Could not extract content");
			}
			
			final String formattedCont = (matcher.group(1));
			
			// divide content by newlines
			final StringTokenizer token = new StringTokenizer(formattedCont, "\n");
			
			// finally create the PICA objects
			while (token.hasMoreTokens()) {
				final String row = token.nextToken();
				if (!"<br />".equals(row)){
					processRow(row);
				}
			}
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	/**
	 * This method should extract the necessary content with regex and put the information
	 * to the PicaRecord object
	 * 
	 * @param cont
	 */
	private void processRow(String cont){
		// pattern to extract the pica category
		final Matcher matcher = PATTERN_PICA_CATEGORY.matcher(cont);
		
		if (matcher.matches()){
			final Row _tmpRow = new Row(matcher.group(1));
			final String _cont = cont.replaceFirst(matcher.group(1), "");
			
			// etract the subfield of each category
			final Matcher matcherSub = PATTERN_PICA_CATEGORY_SUBFIELD.matcher(_cont);
			
			// put it to the row object
			while (matcherSub.find()){
				_tmpRow.addSubField(new SubField(matcherSub.group(1),matcherSub.group(2)));
			}
			
			// and finally put the row to the picarecord
			pica.addRow(_tmpRow);
		}
	}
	
	/**
	 * @return PicaRecord
	 */
	public PicaRecord getActualPicaRecord(){
		return pica;
	}
	
	/**
	 * @return Bibtex String
	 * @throws Exception 
	 */
	public String getBibResult() throws Exception{
		return new PicaParser(pica, url).getBibRes();
	}
}
