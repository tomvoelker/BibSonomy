package org.bibsonomy.recommender.tags.simple.termprocessing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

/**
 * @author jil
 * @version $Id$
 */
public class StopWordRemover implements TermProcessor {
	private static final Log log = LogFactory.getLog(StopWordRemover.class);
	private static Collection<String> stopWords = null;
	private static final String stopWordFile = "multilangST.txt";
	private static StopWordRemover instance;
	
	public static StopWordRemover getInstance() {
		if (instance == null) {
			instance = new StopWordRemover();
		}
		return instance;
	}
	
	private StopWordRemover() {
		if (stopWords == null) {
			stopWords = new HashSet<String>();
			try {
				InputStream is = getClass().getResourceAsStream(stopWordFile);
				if (is == null) {
					throw new IOException("is == null");
				}
				BufferedReader r = new BufferedReader(new InputStreamReader(is,Charset.forName("UTF-8")));
				String sw = r.readLine();
				while (sw != null) {
					stopWords.add(sw);
					sw = r.readLine();
				}
			} catch (IOException e) {
				log.fatal("Stopwordfile could not be loaded");
				throw new RuntimeException(e);
			}
		}
	}

	public String process(String term) {
		if (stopWords.contains(term) == false) {
			log.debug("not removed word '" + term + "' with length " + term.length());
			return term;
		} else {
			log.debug("removed stopword '" + term + "' with length " + term.length());
			return null;
		}
	}

}
