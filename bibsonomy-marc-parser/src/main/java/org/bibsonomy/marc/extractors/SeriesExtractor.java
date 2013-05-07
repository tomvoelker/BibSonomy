package org.bibsonomy.marc.extractors;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.marc.ExtendedMarcWithPicaRecord;
import org.bibsonomy.model.BibTex;

/**
 * @author Lukas
 * @version $Id$
 */
public class SeriesExtractor implements AttributeExtractor {

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		
		if(src instanceof ExtendedMarcWithPicaRecord) {
			ExtendedMarcWithPicaRecord picaSrc = (ExtendedMarcWithPicaRecord) src;
			
			
			String f036A = "";
			String f036B = "";
			String f036C = "";
			String f036D = "";
			String f036F = "";
			String f036G = "";
			
			/*
			 * extract 036B
			 */
			if(picaSrc.getFirstPicaFieldValue("036B","$8") != null) {
				f036B += picaSrc.getFirstPicaFieldValue("036B","$8").replace("@","");
			}
			if(picaSrc.getFirstPicaFieldValue("036B","$9") != null) {
				f036B += picaSrc.getFirstPicaFieldValue("036B","$9");
			}
			
			/*
			 * extract 036A
			 */
			if(picaSrc.getFirstPicaFieldValue("036A","$m") != null) {
				f036A += picaSrc.getFirstPicaFieldValue("036A","$m");
			}
			if(picaSrc.getFirstPicaFieldValue("036A","$a") != null) {
				f036A += picaSrc.getFirstPicaFieldValue("036A","$a").replace("@","");
			}
			if(picaSrc.getFirstPicaFieldValue("036A","$l") != null) {
				f036A += picaSrc.getFirstPicaFieldValue("036A","$l");
			}
			
			/*
			 * extract 036D
			 */
			if(picaSrc.getFirstPicaFieldValue("036D","$8") != null) {
				f036D += picaSrc.getFirstPicaFieldValue("036D","$8").replace("@","");
			}
			if(picaSrc.getFirstPicaFieldValue("036D","$9") != null) {
				f036D += picaSrc.getFirstPicaFieldValue("036D","$9");
			}
			
			/*
			 * extract 036C
			 */
			if(picaSrc.getFirstPicaFieldValue("036C","$m") != null) {
				f036C += picaSrc.getFirstPicaFieldValue("036C","$m");
			}
			if(picaSrc.getFirstPicaFieldValue("036C","$a") != null) {
				f036C += picaSrc.getFirstPicaFieldValue("036C","$a").replace("@","");
			}
			if(picaSrc.getFirstPicaFieldValue("036C","$l") != null) {
				f036C += picaSrc.getFirstPicaFieldValue("036C","$l");
			}
			
			/*
			 * extract 036F
			 */
			if(picaSrc.getFirstPicaFieldValue("036F","$m") != null) {
				f036F += picaSrc.getFirstPicaFieldValue("036F","$m");
			}
			if(picaSrc.getFirstPicaFieldValue("036F","$a") != null) {
				f036F += picaSrc.getFirstPicaFieldValue("036F","$a").replace("@","");
			}
			if(picaSrc.getFirstPicaFieldValue("036F","$l") != null) {
				f036F += picaSrc.getFirstPicaFieldValue("036F","$l");
			}
			
			/*
			 * extract 036G
			 */
			if(picaSrc.getFirstPicaFieldValue("036G","$$a") != null) {
				f036G += picaSrc.getFirstPicaFieldValue("036G","$a");
			}
			
			String result = "[" + 
					(f036B.length() > 0 ? (f036B + ", ") : "") +
					(f036A.length() > 0 ? (f036A + ", ") : "") +
					(f036D.length() > 0 ? (f036D + ", ") : "") +
					(f036C.length() > 0 ? (f036C + ", ") : "") +
					(f036G.length() > 0 ? (f036G + ", ") : "") +
					(f036F.length() > 0 ? (f036F + "]")  : "]");
			
			target.setSeries(result);
		}
		
	}

}
