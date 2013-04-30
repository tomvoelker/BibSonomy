package org.bibsonomy.scraper.converter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;

import org.bibsonomy.scraper.converter.picatobibtex.PicaParser;
import org.bibsonomy.scraper.converter.picatobibtex.PicaRecord;
import org.junit.Test;

/**
 * @author jensi
 * @version $Id$
 */
public class PicaPlusToBibtexTest {
	/**
	 * more like a main
	 */
	@Test
	public void readPicaPlus() {
		try {
			PicaPlusReader reader = new PicaPlusReader();
			BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("dwl20100116-01p.txt"), "UTF-8"));
			Collection<PicaRecord> picas = reader.parseRawPicaPlus(br);
			br.close();
			for (PicaRecord p : picas) {
				System.out.println(PicaParser.getBibRes(p, "bla"));
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
