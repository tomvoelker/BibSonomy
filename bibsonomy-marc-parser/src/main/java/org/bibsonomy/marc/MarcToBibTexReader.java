package org.bibsonomy.marc;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bibsonomy.marc.extractors.AbstractExtractor;
import org.bibsonomy.marc.extractors.AddressExtractor;
import org.bibsonomy.marc.extractors.AuthorExtractor;
import org.bibsonomy.marc.extractors.DayExtractor;
import org.bibsonomy.marc.extractors.EditionExtractor;
import org.bibsonomy.marc.extractors.EditorExtractor;
import org.bibsonomy.marc.extractors.EmergencyRepairingExtractor;
import org.bibsonomy.marc.extractors.HebisIdExtractor;
import org.bibsonomy.marc.extractors.ISBNExtractor;
import org.bibsonomy.marc.extractors.JournalExtractor;
import org.bibsonomy.marc.extractors.MonthExtractor;
import org.bibsonomy.marc.extractors.NoteExtractor;
import org.bibsonomy.marc.extractors.NumberExtractor;
import org.bibsonomy.marc.extractors.OrganizationExtractor;
import org.bibsonomy.marc.extractors.PagesExtractor;
import org.bibsonomy.marc.extractors.PublisherExtractor;
import org.bibsonomy.marc.extractors.SeriesExtractor;
import org.bibsonomy.marc.extractors.TitleExtractor;
import org.bibsonomy.marc.extractors.TypeExtractor;
import org.bibsonomy.marc.extractors.URLExtractor;
import org.bibsonomy.marc.extractors.VolumeExtractor;
import org.bibsonomy.marc.extractors.YearExtractor;
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
 * @version $Id$
 */
public class MarcToBibTexReader implements BibTexReader {

	private List<AttributeExtractor> extractors;
	private static final PicaPlusReader picaReader = new PicaPlusReader();

	public MarcToBibTexReader() {
		extractors = new ArrayList<AttributeExtractor>();
		extractors.add(new TypeExtractor());
		extractors.add(new AbstractExtractor());
		extractors.add(new AuthorExtractor());
		extractors.add(new TitleExtractor());
		extractors.add(new EditorExtractor());
		extractors.add(new JournalExtractor());
		extractors.add(new EditionExtractor());
		extractors.add(new AddressExtractor());
		extractors.add(new PagesExtractor());
		extractors.add(new YearExtractor());
		extractors.add(new PublisherExtractor());
		extractors.add(new HebisIdExtractor());
		extractors.add(new VolumeExtractor());
		extractors.add(new SeriesExtractor());
		extractors.add(new ISBNExtractor());
		extractors.add(new URLExtractor());
		extractors.add(new OrganizationExtractor());
		extractors.add(new DayExtractor());
		extractors.add(new MonthExtractor());
		extractors.add(new NumberExtractor());
		//must be placed in the chain after TypeExtractor
		extractors.add(new NoteExtractor());
		extractors.add(new EmergencyRepairingExtractor());
	}
	
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
			for (AttributeExtractor ex : extractors) {
				try {
					ex.extraxtAndSetAttribute(b, er);
				} catch (IllegalArgumentException e) {
					//System.err.println(e.toString());
				}
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
