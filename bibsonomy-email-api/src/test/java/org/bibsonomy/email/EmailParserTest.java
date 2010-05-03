package org.bibsonomy.email;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bibsonomy.model.Tag;
import org.junit.Test;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class EmailParserTest {

	private final Map<String, Email> emails = new HashMap<String, Email>();

	public EmailParserTest() {
		final Email email0 = new Email();
		email0.setFrom("John Doe <johndoe@cs.uni-kassel.de>");
		email0.setTags(new HashSet<Tag>(Arrays.asList(new Tag[]{
				new Tag("economy"), new Tag("patent"), new Tag("free"), new Tag("software"), new Tag("fsf")
		})));
		email0.setUrls(Collections.singletonList("http://patentabsurdity.com/"));
		emails.put("testemail0.txt", email0);

		final Email email1 = new Email();
		email1.setFrom("=?ISO-8859-15?Q?John_D=E4e?= <johndoe@cs.uni-kassel.de>");
		email1.setTags(new HashSet<Tag>(Arrays.asList(new Tag[]{
				new Tag("social"), new Tag("dagsocial"), new Tag("todo")
		})));
		email1.setUrls(Arrays.asList("http://twitris.knoesis.org/", "http://kenai.com/projects/community-equity")); 
		emails.put("testemail1.txt", email1);
	}


	@Test
	public void testParseEmail() {
		final EmailParser parser = new EmailParser();
		parser.setToFieldParser(new ToFieldParser());

		final Set<Entry<String, Email>> entries = emails.entrySet();


		for (final Entry<String, Email> entry: entries) {
			try {
				final Email parsedEmail = parser.parseEmail(getTestEmail(entry.getKey()));
				final Email correctEmail = entry.getValue();
				assertEquals(correctEmail.getFrom(), parsedEmail.getFrom());
				assertEquals(correctEmail.getTags(), parsedEmail.getTags());
				assertEquals(correctEmail.getUrls(), parsedEmail.getUrls());
			} catch (IOException e) {
				fail(e.getMessage());
			}
		}
	}

	@Test
	public void testDecodeSubject() throws Exception {
		final EmailParser ep = new EmailParser();
		assertEquals("brügge belgien fotos urlaub", ep.decodeSubject("=?ISO-8859-1?Q?br=FCgge?= belgien fotos urlaub"));
		assertEquals("schön", ep.decodeSubject("=?ISO-8859-15?Q?sch=F6n?="));
		assertEquals("schön schöner am_schönsten", ep.decodeSubject("=?ISO-8859-15?Q?sch=F6n_sch=F6ner_am=5Fsch=F6nsten?="));
		assertEquals("schön schöner am schönsten", ep.decodeSubject("=?ISO-8859-15?Q?sch=F6n_sch=F6ner_am_sch=F6nsten?="));
	}
	private static BufferedReader getTestEmail(final String filename) {
		return new BufferedReader(new InputStreamReader(EmailParser.class.getResourceAsStream(filename)));
	}

	public Map<String, Email> getEmails() {
		return emails;
	}


}
