/**
 * BibSonomy-MARC-Parser - Marc Parser for BibSonomy
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
import java.util.List;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.model.BibTex;

/**
 * @author jensi
 */
public class CompositeAttributeExtractor implements AttributeExtractor {
	
	private static final List<AttributeExtractor> extractors;
	static {
		extractors = new ArrayList<AttributeExtractor>();
		extractors.add(new TypeExtractor());
		extractors.add(new AbstractExtractor());
		extractors.add(new AuthorExtractor());
		extractors.add(new TitleExtractor());
		extractors.add(new EditorExtractor());
		extractors.add(new JournalExtractor());
		extractors.add(new EditionExtractor());
		extractors.add(new AddressExtractor());
		extractors.add(new PagesExtractor());
		extractors.add(new YearExtractor());
		extractors.add(new PublisherExtractor());
		extractors.add(new HebisIdExtractor());
		extractors.add(new VolumeExtractor());
		extractors.add(new SeriesExtractor());
		extractors.add(new ISBNExtractor());
		extractors.add(new URLExtractor());
		extractors.add(new OrganizationExtractor());
		extractors.add(new DayExtractor());
		extractors.add(new MonthExtractor());
		extractors.add(new NumberExtractor());
		//must be placed in the chain after TypeExtractor
		extractors.add(new NoteExtractor());
		extractors.add(new EmergencyAuthorExtractor());
		extractors.add(new EmergencyRepairingExtractor());
	}
	
	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		for (AttributeExtractor ex : extractors) {
			try {
				ex.extraxtAndSetAttribute(target, src);
			} catch (IllegalArgumentException e) {
				//System.err.println(e.toString());
			}
		}
	}

}
