package org.bibsonomy.email;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.util.TagUtils;

/**
 * Parses a stream (providing the raw email) into an {@link Email}.
 * Currently, only bookmark emails (i.e., one URL per line) are supported.
 * 
 * <p>The format of such e-mails is:</p>
 * 
 * <pre>
 * Date: Fri, 30 Apr 2010 14:17:58 +0200
 * From: John Doe &lt;jd@cs.uni-kassel.de&gt;
 * User-Agent: Mozilla-Thunderbird 2.0.0.22 (X11/20090707)
 * To: username-299cafad8ce2afb5879c6c85c14cc5259@api.bibsonomy.org
 * Subject: economy patent free software fsf
 *
 * http://patentabsurdity.com/
 *
 * </pre>
 * 
 * <p>The <tt>To:</tt> header contains the authentication information
 * and optionally the group the post should be visible for (default:
 * public).</p>
 * 
 * <p>The subject contains the tags for the post.</p>
 * 
 * <p>Multiple URLs (only http/https) per e-mail are supported. 
 * Everything after the first line not starting with "http" 
 * is ignored.</p>
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class EmailParser {
	private static final Log log = LogFactory.getLog(EmailParser.class);
	
	private static final String HEADER_TO = "To:";
	private static final String HEADER_FROM = "From:";
	private static final String HEADER_SUBJECT = "Subject:";
	private static final String BODY_HTTP = "http";
	
	private static final Pattern QUOTED_PRINTABLE_PATTERN = Pattern.compile("=\\?(.*?)\\?Q\\?(.*?)\\?=");

	
	private ToFieldParser toFieldParser;
	
	/**
	 * Parses the email from the reader into an {@link Email}.
	 * Closes the reader after reading.
	 * 
	 * @param reader
	 * @return
	 * @throws IOException 
	 */
	public Email parseEmail(final BufferedReader reader) throws IOException {
		final Email email = new Email();
		String line;
		while ((line = reader.readLine()) != null) {
			if (!present(email.getTo()) && line.startsWith(HEADER_TO)) {
				/*
				 * To: username-APIKEY+group@api.bibsonomy.org
				 */
				email.setTo(toFieldParser.parseToField(line.substring(HEADER_TO.length()).trim()));
			} else if (!present(email.getFrom()) && line.startsWith(HEADER_FROM)) {
				/*
				 * From: John Doe <johndoe@example.com>
				 */
				email.setFrom(line.substring(HEADER_FROM.length()).trim());
			} else if (!present(email.getTags()) && line.startsWith(HEADER_SUBJECT)) {
				/*
				 * Subject: free software fsf
				 * 
				 * TODO: Another variante might be that the subject contains
				 * the title and tags can be supplied after the string "Tags:"
				 * e.g., 
				 * 
				 * Subject: This is the title Tags: cool tags title
				 * 
				 */
				try {
					/*
					 * decode quoted printable
					 */
					final String subject = decodeSubject(line.substring(HEADER_SUBJECT.length()).trim());
					/*
					 * parse tags
					 */
					email.setTags(TagUtils.parse(subject));
				} catch (RecognitionException e) {
					email.setTags(Collections.singleton(TagUtils.getEmptyTag()));
				}
			} else if (line.trim().isEmpty()) {
				/*
				 * http://www.fsf.org/
				 */
				email.setUrls(parseEmailBody(reader));
				break;
			}
		}
		reader.close();
		return email;
	}

	/**
	 * 
	 * Decodes "=?ISO-8859-15?Q?sch=F6n?=" to "schön"
	 * 
	 * @param s
	 * @return
	 */
	protected String decodeSubject(final String s) {
		final Matcher matcher = QUOTED_PRINTABLE_PATTERN.matcher(s);
		if (matcher.find()) {
			try {
				return matcher.replaceAll(new String(QuotedPrintableCodec.decodeQuotedPrintable(matcher.group(2).replace("_", " ").getBytes()), matcher.group(1)));
			} catch (UnsupportedEncodingException e) {
				log.warn("Could not decode subject", e);
			} catch (DecoderException e) {
				log.warn("Could not decode subject", e);
			}
		}
		return s;
	}
	
	/**
	 * Parses the body of an email.
	 * Everything after the first line that does not start with 
	 * {@link #BODY_HTTP} is ignored. 
	 * 
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	private List<String> parseEmailBody(final BufferedReader reader) throws IOException {
		final List<String> urls = new LinkedList<String>();
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith(BODY_HTTP)) {
				urls.add(line.trim());
			} else {
				/*
				 * if we have found at least one URL, we stop after
				 * this first line not starting with http
				 */
				if (!urls.isEmpty()) break;
			}
		}
		return urls;
	}

	public ToFieldParser getToFieldParser() {
		return toFieldParser;
	}

	public void setToFieldParser(ToFieldParser toFieldParser) {
		this.toFieldParser = toFieldParser;
	}
}