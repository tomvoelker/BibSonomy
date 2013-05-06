package org.bibsonomy.marc;

import org.bibsonomy.scraper.converter.picatobibtex.PicaRecord;
import org.bibsonomy.scraper.converter.picatobibtex.PicaUtils;
import org.marc4j.marc.Record;

/**
 * @author jensi
 * @version $Id$
 */
public class ExtendedMarcWithPicaRecord extends ExtendedMarcRecord {

	private final PicaRecord pica;

	public ExtendedMarcWithPicaRecord(Record marc, PicaRecord pica) {
		super(marc);
		this.pica = pica;
	}

	public String getFirstPicaFieldValue(final String category, final String subCategory) {
		return PicaUtils.getSubCategory(pica, category, subCategory, null);
	}
}
