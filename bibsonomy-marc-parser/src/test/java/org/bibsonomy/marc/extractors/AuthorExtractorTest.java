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

import static org.junit.Assert.assertEquals;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.junit.Test;

/**
 * @author Lukas
 */
public class AuthorExtractorTest extends AbstractExtractorTest{
	
	@Test
	public void testVariousAuthors() {
		
		String[][] correspondingFields = {{"100", "110", "111"}, {"700", "710", "711"}};
		
		for(int i = 0; i < 2; i++) {
			
			BibTex b = new BibTex();
			AuthorExtractor aExtract = new AuthorExtractor();
			aExtract.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField(correspondingFields[i][0], 'a', "Test, Name").
					withMarcField((correspondingFields[i][0]), '4', "aut"));
			assertEquals(new PersonName("Name", "Test"), b.getAuthor().get(0));
			//secure that organization isn't set as author in case of proceedings
			b = new BibTex();
			aExtract.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField(correspondingFields[i][1], 'a', "Test Corporation").
					withMarcField(correspondingFields[i][1], '4', "aut").withPicaField("013H", "$0", "k"));
			assertEquals(0, b.getAuthor().size());
			//author should be set, when it's a person even if entrytype proceedings
			b = new BibTex();
			aExtract.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField(correspondingFields[i][1], 'a', "Test Corporation").
					withMarcField(correspondingFields[i][1], '4', "aut").withMarcField(correspondingFields[i][0], 'a', "Test, Name").withMarcField(correspondingFields[i][0], '4', "aut").withPicaField("013H", "$0", "k"));
			assertEquals(new PersonName("Name", "Test"), b.getAuthor().get(0));
			//organization can be added as author, if entrytype is not proceedings
			b = new BibTex();
			aExtract.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField(correspondingFields[i][1], 'a', "Test Corporation").
					withMarcField(correspondingFields[i][1], '4', "aut"));
			assertEquals(new PersonName("", "Test Corporation"), b.getAuthor().get(0));
			//also meetings should be not set for proceedings as author and vice versa
			b = new BibTex();
			aExtract.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField(correspondingFields[i][2], 'a', "Annual Science Meeting").
					withMarcField(correspondingFields[i][2], '4', "aut").withPicaField("013H", "$0", "k"));
			assertEquals(0, b.getAuthor().size());
			b = new BibTex();
			aExtract.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField(correspondingFields[i][2], 'a', "Annual Science Meeting").
					withMarcField(correspondingFields[i][2], '4', "aut"));
			assertEquals(new PersonName("", "Annual Science Meeting"), b.getAuthor().get(0));
		}
		
	}
	
}
