/**
 * BibSonomy-MARC-Parser - Marc Parser for BibSonomy
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.marc;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bibsonomy.marc.extractors.CompositeAttributeExtractor;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.ImportResource;
import org.bibsonomy.model.util.BibTexReader;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.data.Data;
import org.bibsonomy.model.util.data.DualData;
import org.bibsonomy.scraper.converter.PicaPlusReader;
import org.bibsonomy.scraper.converter.picatobibtex.PicaRecord;
import org.bibsonomy.util.ValidationUtils;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.Record;

/**
 * @author jensi
 */
public class MarcToBibTexReader implements BibTexReader {

	private static AttributeExtractor ex = new CompositeAttributeExtractor();
	private static final PicaPlusReader picaReader = new PicaPlusReader();
	
	@Override
	public Collection<ImportResource> read(ImportResource importRes) {
		Data data = importRes.getData();
		List<ImportResource> rVal = new ArrayList<ImportResource>();
		
		MarcReader reader;
		Iterator<PicaRecord> picaRecords;
		
		if (data instanceof DualData) {
			String[] mimeTypes = data.getMimeType().split(":",2);
			if (mimeTypes.length != 2) {
				throw new IllegalArgumentException("DualData with strange nr of mimeTypes");
			}
			Data[] datas = new Data[] { data, ((DualData) data).getData2() };
			reader = null;
			picaRecords = null;
			for (int i = 0; i < 2; ++i) {
				if ("application/marc".equals(mimeTypes[i])) {
					reader = new MarcStreamReader(datas[i].getInputStream());
				} else if ("application/pica".equals(mimeTypes[i])) {
					try {
						picaRecords = picaReader.parseRawPicaPlus(new BufferedReader(datas[i].getReader())).iterator();
					} catch (IOException ex) {
						throw new RuntimeException("error while parsing pica data", ex);
					}
				} else {
					throw new IllegalArgumentException("unknown format with mimetype '" + mimeTypes[i] + "'");
				}
			}
			ValidationUtils.assertNotNull(reader);
			ValidationUtils.assertNotNull(picaRecords);
		} else {
			reader = new MarcStreamReader(data.getInputStream());
			picaRecords = null;
		}
		
		while (reader.hasNext()) {
			final Record r = reader.next();
			ExtendedMarcRecord er;
			if (picaRecords != null &&
					picaRecords.hasNext()) {
				PicaRecord picaRecord = picaRecords.next();
				er = new ExtendedMarcWithPicaRecord(r, picaRecord);
			} else {
				er = new ExtendedMarcRecord(r);
			}
			ImportResource b = new ImportResource();
			initialize(b, importRes.getResource());

			try {
				ex.extraxtAndSetAttribute(b, er);
			} catch (IllegalArgumentException e) {
				//System.err.println(e.toString());
			}
			
			b.setBibtexKey(BibTexUtils.generateBibtexKey(b));
			rVal.add(b);

			//System.out.println(r.toString());
		}
		return rVal;
	}

	private void initialize(ImportResource b, BibTex resource) {
		if (resource == null) {
			return;
		}
		b.setPrivnote(resource.getPrivnote());
		b.setMisc(resource.getMisc());
	}

}
