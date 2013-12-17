package org.bibsonomy.marc.extractors;

import junit.framework.Assert;

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
			Assert.assertEquals(new PersonName("Name", "Test"), b.getAuthor().get(0));
			//secure that organization isn't set as author in case of proceedings
			b = new BibTex();
			aExtract.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField(correspondingFields[i][1], 'a', "Test Corporation").
					withMarcField(correspondingFields[i][1], '4', "aut").withPicaField("013H", "$0", "k"));
			Assert.assertEquals(0, b.getAuthor().size());
			//author should be set, when it's a person even if entrytype proceedings
			b = new BibTex();
			aExtract.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField(correspondingFields[i][1], 'a', "Test Corporation").
					withMarcField(correspondingFields[i][1], '4', "aut").withMarcField(correspondingFields[i][0], 'a', "Test, Name").withMarcField(correspondingFields[i][0], '4', "aut").withPicaField("013H", "$0", "k"));
			Assert.assertEquals(new PersonName("Name", "Test"), b.getAuthor().get(0));
			//organization can be added as author, if entrytype is not proceedings
			b = new BibTex();
			aExtract.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField(correspondingFields[i][1], 'a', "Test Corporation").
					withMarcField(correspondingFields[i][1], '4', "aut"));
			Assert.assertEquals(new PersonName("", "Test Corporation"), b.getAuthor().get(0));
			//also meetings should be not set for proceedings as author and vice versa
			b = new BibTex();
			aExtract.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField(correspondingFields[i][2], 'a', "Annual Science Meeting").
					withMarcField(correspondingFields[i][2], '4', "aut").withPicaField("013H", "$0", "k"));
			Assert.assertEquals(0, b.getAuthor().size());
			b = new BibTex();
			aExtract.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField(correspondingFields[i][2], 'a', "Annual Science Meeting").
					withMarcField(correspondingFields[i][2], '4', "aut"));
			Assert.assertEquals(new PersonName("", "Annual Science Meeting"), b.getAuthor().get(0));
		}
		
	}
	
}
