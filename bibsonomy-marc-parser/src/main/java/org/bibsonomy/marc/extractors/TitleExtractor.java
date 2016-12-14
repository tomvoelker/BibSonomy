/**
 * BibSonomy-MARC-Parser - Marc Parser for BibSonomy
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.marc.extractors;

import java.text.Normalizer;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.marc.ExtendedMarcWithPicaRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.ValidationUtils;

/**
 * @author jensi
 */
public class TitleExtractor implements AttributeExtractor {

	/**
	 * Get the short (pre-subtitle) title of the record.
	 * 
	 * @return string
	 * @access protected
	 */
	public StringBuilder getShortTitle(StringBuilder sb, ExtendedMarcRecord r) {
		// 245 $a_:_$b
		r.appendFirstFieldValueWithDelmiterIfPresent(sb, "245", 'a', "");
		StringUtils.replaceFirstOccurrence(sb, "@", "");
		return sb;
	}

	public StringBuilder getSubtitle(StringBuilder sb, ExtendedMarcRecord r) {
		int l = sb.length();

		// r.appendFirstFieldValueWithDelmiterIfPresent(sb, "245", 'h', ""); h
		// is Medium (media type)
		r.appendFirstFieldValueWithDelmiterIfPresent(sb, "245", 'b', " : ");
		if (l == sb.length()) {
			// nothing appended -> try pica
			if (r instanceof ExtendedMarcWithPicaRecord) {
				String fieldValue = ((ExtendedMarcWithPicaRecord) r).getFirstPicaFieldValue("021A", "$d", "   ");
				fieldValue = ExtendedMarcRecord.trimAndNormalize(fieldValue);
				if (ValidationUtils.present(fieldValue)) {
					if (sb.length() > 0) {
						sb.append(" : ");
						sb.append(fieldValue);
					}
				}
			}
		}
		if (l > 0) {
			int semiI = sb.indexOf(";", l - 1);
			if (semiI >= l) {
				sb.setLength(semiI);
			}
		}
		// r.appendFirstFieldValueWithDelmiterIfPresent(sb, "245", 'c', " / ");
		return sb;
	}

	private boolean isDependentPart(ExtendedMarcRecord r) {
		if (r instanceof ExtendedMarcWithPicaRecord) {
			String fieldValue = ((ExtendedMarcWithPicaRecord) r).getFirstPicaFieldValue("002@", "$0", "   ");
			return (fieldValue.charAt(1) == 'f');
		}
		return false;
	}
	
	private boolean isIndependentPart(ExtendedMarcRecord r) {
		if (r instanceof ExtendedMarcWithPicaRecord) {
			String fieldValue = ((ExtendedMarcWithPicaRecord) r).getFirstPicaFieldValue("002@", "$0", "   ");
			return (fieldValue.charAt(1) == 'F');
		}
		return false;
	}

	@Override
	public void extractAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		final StringBuilder sb = new StringBuilder();
		if (isDependentPart(src)) {
			final String seriesName = ExtendedMarcRecord.trimAndNormalize(((ExtendedMarcWithPicaRecord) src).getFirstPicaFieldValue("036C", "$a", "")).replace("@", "");
			final String seriesSubName = ExtendedMarcRecord.trimAndNormalize(((ExtendedMarcWithPicaRecord) src).getFirstPicaFieldValue("036C", "$d", "")).replace("@", "");
			final String pieceNr = ExtendedMarcRecord.trimAndNormalize(((ExtendedMarcWithPicaRecord) src).getFirstPicaFieldValue("036C", "$l", "")).replace("@", "").trim();
			final String pieceName = ExtendedMarcRecord.trimAndNormalize(((ExtendedMarcWithPicaRecord) src).getFirstPicaFieldValue("021A", "$a", "")).replace("@", "");
			
			if (ValidationUtils.present(seriesName)) {
				sb.append(seriesName);
			}
			if (ValidationUtils.present(seriesSubName)) {
				if (sb.length() > 0) {
					sb.append(" ");
				}
				sb.append(seriesSubName);
			}
			
			if (ValidationUtils.present(pieceName) || ValidationUtils.present(pieceNr)) {
				if (sb.length() > 0) {
					sb.append(" : ");
				}
				if (ValidationUtils.present(pieceNr)) {
					sb.append(pieceNr);
					if ((pieceNr.contains(".") == false) && (pieceNr.contains(" ") == false)) {
						sb.append('.');
					}
				}
				if (ValidationUtils.present(pieceName)) {
					if (sb.length() > 0) {
						sb.append(" ");
					}
					sb.append(pieceName);
				}
				
			}
			
		} else if (isIndependentPart(src)) {
			sb.append(ExtendedMarcRecord.trimAndNormalize(((ExtendedMarcWithPicaRecord) src).getFirstPicaFieldValue("021A", "$a", "").replace("@", "")));
			getSubtitle(sb, src);
		}
		if (sb.length() == 0) {
			getShortTitle(sb, src);
			getSubtitle(sb, src);
		}
		StringUtils.trimStringBuffer(sb);
		final String val = sb.toString();
		if (val != null) {
			target.setTitle(Normalizer.normalize(val, Normalizer.Form.NFC));
		}

	}

}

/*
 * Typ: Pica: 13H 0k k->Konferenz -> 090? f->festschrift Pica: 002@ 2.char b ->
 * marc? 006?
 * 
 * 002@ 2.Position c oder d -> mehrbändiges werk (mvbook)
 * http://www.hebis.de/de/
 * 1publikationen/arbeitsmaterialien/hebis-handbuch/kategorien
 * /kategorien_detail.php?we_editObject_ID=2253 002@ 2.Position a -> einbändiges
 * werk (book) 002@ 2.Position a -> teil eines mehrbändigen W. (book) f ->
 * gesamtname (036c $a) + ": " + teilname=normalname (021a $a) F -> nur
 * teiltitel (021a $a)
 * 
 * mvbook für gesammte sammlungen book für einzelne bücher aus der sammlung
 * 
 * 002@ b (Zeitschrift) -> periodical 021a als titel 029A oder F 033A 031@ $a
 * (marc 362 a) (wichtig) o (Aufsatz) ->?
 * 
 * series extractor:
 * 
 * 
 * TODO: titel bei mehrbändern (knuth)
 * 
 * 
 * PICA Format lok: <...> <ILN (library number)>
 * 
 * exp: (exemplare)?
 * 
 * 
 * 013H $0 u -> phdthesis (oder ggf book wenn publisher 33A $n vorhanden ist? -
 * zu heikel) 037C $a ins note feld)
 * 
 * 
 * 013H/0X = k-> proceedings dann (nein immer) marc 111 oder 110 als
 * organization und nicht als autor
 * 
 * organisation 
 * 029A/0X 
 * 029A/0X 
 * 029A/0X
 * 
 * 
 * kein autor sondern organisation: 
 * pica 029A $a <$c> sonst 029F $8 <$g> sonst
 * 029E $8 <$g> / $b marc 710 genauso
 * 
 * 002@ 1.char == 'O' (online resource) -> 009Q $u als url
 */
