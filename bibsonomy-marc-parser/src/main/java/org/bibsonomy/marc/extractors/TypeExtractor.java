/**
 * BibSonomy-MARC-Parser - Marc Parser for BibSonomy
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
package org.bibsonomy.marc.extractors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.marc.ExtendedMarcWithPicaRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.util.ValidationUtils;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.Leader;

/**
 * @author mve
 */
public class TypeExtractor implements AttributeExtractor {
	private static final Map<String, String> map = getMap();

	private static Map<String, String> getMap() {
		HashMap<String, String> typeMap = new HashMap<String, String>();
		typeMap.put("amxxx", "book");
		typeMap.put("amco", "dvd");
		typeMap.put("amcocd", "cd");
		typeMap.put("amc ", "cd");
		typeMap.put("amcr", "ebook");
		typeMap.put("amh", "microfilm");
		typeMap.put("amf", "braille");
		typeMap.put("amo", "kit");
		typeMap.put("asxxx", "journal");
		typeMap.put("ast", "journal");
		typeMap.put("ash", "journal");
		typeMap.put("asco", "journal");
		typeMap.put("ascocd", "journal");
		typeMap.put("ascr", "electronic");
		typeMap.put("asf", "braille");
		typeMap.put("cmq", "musicalscore");
		typeMap.put("csq", "musicalscore");
		typeMap.put("ema", "map");
		typeMap.put("esa", "map");
		typeMap.put("gmm", "video");
		typeMap.put("gmxxx", "video");
		typeMap.put("gsm", "video");
		typeMap.put("gsxxx", "video");
		typeMap.put("ims", "audio");
		typeMap.put("imcocd", "cd");
		typeMap.put("jmxxx", "audio");
		typeMap.put("jms", "audio");
		typeMap.put("jmcocd", "audio");
		typeMap.put("jsco", "audio");
		typeMap.put("jss", "audio");
		typeMap.put("kma", "photo");
		typeMap.put("kmk", "photo");
		typeMap.put("omxxx", "kit");
		typeMap.put("omo", "kit");
		typeMap.put("rmxxx", "physicalobject");
		typeMap.put("rmz", "physicalobject");
		typeMap.put("tmxxx", "manuscript");
		return typeMap;
	}

	private static final Map<String, String> map2bibtex = new HashMap<String, String>();
	static {
		map2bibtex.put("manuscript", "unpublished");
		map2bibtex.put("ebook", "electronic");
		map2bibtex.put("book", "book");
		map2bibtex.put("journal", "periodical");
		map2bibtex.put("newspaper", "periodical");
		map2bibtex.put("dvd", "electronic");
		map2bibtex.put("electronic", "electronic");
		map2bibtex.put("slide", "presentation");
		map2bibtex.put("conference", "proceedings");
		map2bibtex.put("phdthesis", "phdthesis");
		map2bibtex.put("series", "mvbook");
		map2bibtex.put("article", "article");
		map2bibtex.put("audio", "audio");
		map2bibtex.put("cd", "audio"); // or misc with type=audiocd?
		map2bibtex.put("video", "video");
	}

	@Override
	public void extractAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		if (!(src instanceof ExtendedMarcWithPicaRecord)) {
			target.setEntrytype("misc");
			return;
		}

		final ExtendedMarcWithPicaRecord record = (ExtendedMarcWithPicaRecord) src;
		// format ist is detected by info in Leader and kat 007
		final Leader leader = src.getRecord().getLeader();
		List<ControlField> fields = null;
		try {
			fields = src.getControlFields("007");
		} catch (IllegalArgumentException e) {
			// ignore
		}
		
		final List<String> phys = new ArrayList<String>();
		String type = "misc";

		if (ValidationUtils.present(fields)) {
			for (ControlField field : fields) {
				final String data = field.getData();
				if (ValidationUtils.present(data)) {
					if (data.charAt(0) == 'c') {
						// cd or dvd
						String tmp = src.getFirstFieldValue("300", 'a');
						if (data.startsWith("co") && (tmp != null) && (tmp.toUpperCase().indexOf("DVD") == -1)) {
							phys.add("cocd");
						} else {
							if (data.length() > 2) {
								phys.add(data.substring(0,2));
							} else {
								phys.add(data);
							}
						}
					} else {
						phys.add(data.substring(0,1));
					}
				}
			}
		} else {
			phys.add("xxx");
		}

		char art = leader.getTypeOfRecord();
		char level = leader.getImplDefined1()[0];
		// now we have the three components art, level and phys.
		// For some formats this is not enough and we need additional infos

		// preliminary solution for detection of series
		final String s = record.getFirstPicaFieldValue("002@", "$0", "");
		// detect conference logs
		final String conf = record.getFirstPicaFieldValue("013H", "$0", "");
		
		if ("u".equals(conf.trim())) {
			type = "phdthesis";
		} else if (s.indexOf("c") == 1 || s.indexOf("d") == 1) {
			type = "series";
		} else if (s.indexOf("o") == 1) {
			// preliminary solution for articles
			type = "article";
		} else if (s.indexOf("r") == 1) {
			// preliminary solution for retro
			type = "retro";
		} else if (conf != null && conf.indexOf("k") == 0){
			//get proceedings
			type = "conference";
		} else {
			// return formats accourding to format array in the beginning
			// of this method
			for (final String p : phys) {
				final String value = map.get(("" + art + level + p).trim());
				if (value != null) {
					type = value;
					break;
				}
			}
		}
		// there is no format defined for the combination of art level and phys
		// for debugging
		target.setEntrytype(toBibtexType(type));

		// TODO: why is getMVBook disabled?
		//set type to mvbook for book series
		//getMVBook(target, record);
	}

	private String toBibtexType(String type) {
		final String bibtexType = map2bibtex.get(type);
		if (bibtexType == null) {
			return "misc";
		}
		return bibtexType;
	}

	/**
	 * set the entrytype to mvbook if it's an anthology
	 *
	 * @param target
	 * @param src
	 */
	private void getMVBook(BibTex target, ExtendedMarcWithPicaRecord src) {
		String mvType = src.getFirstPicaFieldValue("002@", "$0");
		if ( mvType != null ) {
			if ((mvType.charAt(1) == 'c') || (mvType.charAt(1) == 'd')) {
				if (target.getEntrytype().equals("book")) {
					target.setEntrytype("mvbook");
				} else {
					target.setEntrytype("mvbook");
				}
			}
		}
	}
}