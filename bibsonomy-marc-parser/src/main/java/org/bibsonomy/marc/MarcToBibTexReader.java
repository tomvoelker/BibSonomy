package org.bibsonomy.marc;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bibsonomy.marc.extractors.AuthorExtractor;
import org.bibsonomy.marc.extractors.TitleExtractor;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.util.BibTexReader;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.Record;

/**
 * @author jensi
 * @version $Id$
 */
public class MarcToBibTexReader implements BibTexReader {

	private List<AttributeExtractor> extractors;

	public MarcToBibTexReader() {
		extractors = new ArrayList<AttributeExtractor>();
		extractors.add(new TitleExtractor());
		extractors.add(new AuthorExtractor());
	}
	
	@Override
	public Collection<BibTex> read(InputStream is) {
		List<BibTex> rVal = new ArrayList<BibTex>();
		MarcReader reader = new MarcStreamReader(is, "UTF-8");
		while (reader.hasNext()) {
			Record r = reader.next();
			ExtendedMarcRecord er = new ExtendedMarcRecord(r);
			BibTex b = new BibTex();
			for (AttributeExtractor ex : extractors) {
				ex.extraxtAndSetAttribute(b, er);
			}
			rVal.add(b);

			//System.out.println(r.toString());
		}
		return rVal;
	}

}
