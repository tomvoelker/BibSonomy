package org.bibsonomy.webapp.controller.ajax;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.model.Tag;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class ConceptControllerTest {
	
	/**
	 * tests {@link ConceptController#prepareResponseString(String, List)}
	 */
	@Test
	public void testPrepareResponseString() {
		final ConceptController control = new ConceptController();

		final List<Tag> pickedConcepts = new LinkedList<Tag>();

		final Tag conf = new Tag("conference");
		conf.addSubTag(new Tag("iccs"));
		conf.addSubTag(new Tag("ecmlpkdd"));
		conf.addSubTag(new Tag("www"));
		conf.addSubTag(new Tag("icm"));
		pickedConcepts.add(conf);

		final Tag loc = new Tag("location");
		loc.addSubTag(new Tag("kassel"));
		loc.addSubTag(new Tag("berlin"));
		loc.addSubTag(new Tag("bremen"));
		loc.addSubTag(new Tag("erfurt"));
		pickedConcepts.add(loc);


		final String response = control.prepareResponseString("jaeschke", pickedConcepts);

		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<relations user=\"jaeschke\"><relation><upper>conference</upper><lowers id=\"conference\">" +
				"<lower>iccs</lower><lower>ecmlpkdd</lower><lower>www</lower><lower>icm</lower></lowers></relation>" +
				"<relation><upper>location</upper><lowers id=\"location\"><lower>kassel</lower><lower>berlin</lower>" +
				"<lower>bremen</lower><lower>erfurt</lower></lowers></relation></relations>", response);

	}

}
