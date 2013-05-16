package org.bibsonomy.marc.extractors;

import java.text.Normalizer;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.marc.ExtendedMarcWithPicaRecord;
import org.bibsonomy.model.BibTex;

/**
 * @author Lukas
 * @version $Id$
 */
public class VolumeExtractor implements AttributeExtractor {

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		
		/*
		 * first try to get volume from the marc record
		 */
		String volume = src.getFirstFieldValue("490", 'v');
		
		if(volume != null) {
			target.setVolume(Normalizer.normalize(volume, Normalizer.Form.NFC));
			return;
		}
		
		/*
		 * marc record extraction failed -> check the pica record
		 */
		if(src instanceof ExtendedMarcWithPicaRecord) {
			
			ExtendedMarcWithPicaRecord record = (ExtendedMarcWithPicaRecord) src;
			
			//try to get volume on 036E first
			volume = record.getFirstPicaFieldValue("036E", "$l");
			
			if(volume != null) {
				target.setVolume(volume);
				return;
			}
			
			//try to get volume on 036E if 036E was not set
			volume = record.getFirstPicaFieldValue("036F", "$l");
			if (volume != null) {
				target.setVolume(Normalizer.normalize(volume, Normalizer.Form.NFC));
			}
			
		}
		
	}

}
