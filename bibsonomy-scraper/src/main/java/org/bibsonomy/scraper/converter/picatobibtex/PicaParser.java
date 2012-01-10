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

package org.bibsonomy.scraper.converter.picatobibtex;


import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.converter.picatobibtex.rules.AbstractRule;
import org.bibsonomy.scraper.converter.picatobibtex.rules.AddressRule;
import org.bibsonomy.scraper.converter.picatobibtex.rules.AuthorRule;
import org.bibsonomy.scraper.converter.picatobibtex.rules.ISBNRule;
import org.bibsonomy.scraper.converter.picatobibtex.rules.ISSNRule;
import org.bibsonomy.scraper.converter.picatobibtex.rules.PublisherRule;
import org.bibsonomy.scraper.converter.picatobibtex.rules.Rules;
import org.bibsonomy.scraper.converter.picatobibtex.rules.SeriesRule;
import org.bibsonomy.scraper.converter.picatobibtex.rules.TagsRule;
import org.bibsonomy.scraper.converter.picatobibtex.rules.TitleRule;
import org.bibsonomy.scraper.converter.picatobibtex.rules.URNRule;
import org.bibsonomy.scraper.converter.picatobibtex.rules.VolumeRule;
import org.bibsonomy.scraper.converter.picatobibtex.rules.YearRule;

/**
 * 
 * The bibtexkey is manufactured in accordance to the 013H category of pica+.
 * http://www.allegro-c.de/formate/f/f0528.htm
 * 
 * TODO: need to fix that nearly every category is repeatable
 * TODO: all this should have a better structure
 * TODO: fix special chars
 * 
 * @author C. Kramer
 * @version $Id$
 */
public class PicaParser{

	/**
	 * Parses a PICA record and returns a BibTeX string
	 * 
	 * @param pica 
	 * @param url 
	 * 
	 * @return String
	 */
	public static String getBibRes(final PicaRecord pica, final String url) {
		final StringBuffer bibtex = new StringBuffer();

		final String type = getBibType(pica);
		final String author = new AuthorRule(pica).getContent();
		final String title = new TitleRule(pica).getContent();
		final String year = new YearRule(pica).getContent();
		final String isbn = new ISBNRule(pica).getContent();
		final String issn = new ISSNRule(pica).getContent();
		final String series = new SeriesRule(pica).getContent();
		final String abstr = new AbstractRule(pica).getContent();
		final String tags = new TagsRule(pica).getContent();
		final String publisher = new PublisherRule(pica).getContent();
		final String address = new AddressRule(pica).getContent();
		final String volume = new VolumeRule(pica).getContent();



		final Rules urn = new URNRule(pica);

		final String opac;
		if (urn.isAvailable()){
			opac = "http://nbn-resolving.org/urn/resolver.pl?urn=" + urn.getContent();
		} else {
			opac = PicaUtils.prepareUrl(url);
		}

		final String bibtexKey = BibTexUtils.generateBibtexKey(author, null, year, title);

		bibtex.append(type + bibtexKey + ",\n");
		bibtex.append("  author = {" + author + "},\n");
		bibtex.append("  title = {" + title + "},\n");
		bibtex.append("  year = {" + year + "},\n");
		bibtex.append("  abstract = {" + abstr + "}, \n");
		bibtex.append("  keywords = {" + tags + "}, \n");
		bibtex.append("  url = {" + opac + "}, \n");
		bibtex.append("  series = {" + series + "}, \n");
		bibtex.append("  isbn = {" + isbn + "}, \n");
		bibtex.append("  issn = {" + issn + "}, \n");
		bibtex.append("  publisher = {" + publisher + "}, \n");
		bibtex.append("  address = {" + address + "}, \n");
		bibtex.append("  volume = {" + volume + "}, \n");
		bibtex.append("}");

		return bibtex.toString();
	}


	private static String getBibType(final PicaRecord pica){
		/*
		 * tests if the category 013H is existing and test for some values, 
		 * if not check if the title category 021A has a $d subfield and if "proceedings" matches
		 * if thats the case then its a proceeding. If the entry has a ISBN and NO ISSN then its a book,
		 * if it has a ISSN and NO ISBN then its an article otherwise if it has ISBN AND ISSN
		 * its usually a proceeding.
		 * 
		 * If the 013H category is set and the $0 subfield provides the value u then
		 * it will be decided between phdthesis, masterthesis and techreport
		 */

		final String cat013H0 = PicaUtils.getSubCategory(pica, "013H", "$0");

		if (present(cat013H0) && "u".equals(cat013H0)) {
			final String _tempSub = PicaUtils.getSubCategory(pica, "037C", "$c");

			if (present(_tempSub)) {
				if (_tempSub.matches("^.*Diss.*$")){
					return "@phdthesis{";
				} else if (_tempSub.matches("^.*Master.*$")){
					return "@mastersthesis{";
				} else {
					return "@techreport{";
				}
			}
		} 

		final String cat021Ad = PicaUtils.getSubCategory(pica, "021A", "$d");
		if (present(cat021Ad) && cat021Ad.trim().matches("^.*proceedings.*$")){
				return "@proceedings{";
		} 

		if ((pica.isExisting("004A") || pica.isExisting("004D")) && !pica.isExisting("005A") && !pica.isExisting("005D")) {
			return "@book{";
		}

		if (((pica.isExisting("005A")) || pica.isExisting("005D")) && !pica.isExisting("004A") && !pica.isExisting("004D")){
			return "@article{";
		}

		if (((pica.isExisting("004A")) || pica.isExisting("004D")) && (pica.isExisting("005A") || pica.isExisting("005D"))){
			return "@proceedings{";
		}

		return "@misc{";
	}
}
