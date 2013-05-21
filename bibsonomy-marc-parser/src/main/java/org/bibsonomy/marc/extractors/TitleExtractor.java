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
 * @version $Id$
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
		r.appendFirstFieldValueWithDelmiterIfPresent(sb, "245", 'b', ": ");
		if (l > 0) {
			int semiI = sb.indexOf(";", l - 1);
			if (semiI >= l) {
				sb.setLength(semiI);
			}
		}
		// r.appendFirstFieldValueWithDelmiterIfPresent(sb, "245", 'c', " / ");
		return sb;
	}

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		StringBuilder sb = new StringBuilder();
		getShortTitle(sb, src);
		getSubtitle(sb, src);
		StringUtils.trimStringBuffer(sb);
		String val = sb.toString();
		if (val != null) {
			target.setTitle(Normalizer.normalize(val, Normalizer.Form.NFC));
		}

		if (src instanceof ExtendedMarcWithPicaRecord) {
			//bookseries -> mvbook
			getMVBook(target, (ExtendedMarcWithPicaRecord) src);
			
			setNoteForPhd(target, (ExtendedMarcWithPicaRecord) src);
			//organization instead of author
			setOrganizationForConference(target,
					(ExtendedMarcWithPicaRecord) src);
		}

	}

	/**
	 * set the entrytype to mvbook if it's an anthology
	 * 
	 * @param target
	 * @param src
	 */
	private void getMVBook(BibTex target, ExtendedMarcWithPicaRecord src) {
		String mvType = src.getFirstPicaFieldValue("002@", "$0");
		if ( mvType != null ) {
			if(target.getEntrytype().equals("book") && 
					(mvType.charAt(1) == 'c' || mvType.charAt(1) == 'd')) {
				target.setEntrytype("mvbook");
			}
		}
	}

	/**
	 * set note field for phdthesis
	 * 
	 * @param target
	 * @param src
	 */
	private void setNoteForPhd(BibTex target, ExtendedMarcWithPicaRecord src) {
		if (target.getEntrytype().equals("phdthesis")) {
			target.setNote(src.getFirstPicaFieldValue("037C", "$a"));
		}
	}

	/**
	 * delete author and set organization if publication is of type conference
	 * report
	 * 
	 * @param target
	 * @param src
	 */
	private void setOrganizationForConference(BibTex target,
			ExtendedMarcWithPicaRecord src) {
		
		String check = src.getFirstPicaFieldValue("013H", "$0");

		if (ValidationUtils.present(check) && check.contains("k")) {
			
			//fields which possibly contain information
			String[][] marcFields = { { "110:a", "111:a", "710:a" },
					{ "110:c", "111:c", "710:c" } };
			String[][] picaFields = { { "029A:$a", "029F:$8", "029E:$8" },
					{ "029A:$c", "029F:$g", "029E:$g" } };
			
			String conference = "";
			String location = "";
			
			//try to find marc information
			for (int i = 0; i < marcFields.length
					&& (!ValidationUtils.present(conference) || !ValidationUtils
							.present(location)); i++) {
				if(!ValidationUtils.present(conference)) {
					conference = src.getFirstFieldValue(marcFields[0][i].split(":")[0], marcFields[0][i].split(":")[1].charAt(0));
				}
				
				if(!ValidationUtils.present(location)) {
					location = src.getFirstFieldValue(marcFields[1][i].split(":")[0], marcFields[1][i].split(":")[1].charAt(0));
				}
			}

			//get pica information if marc was not available
			for (int i = 0; i < picaFields.length
					&& (!ValidationUtils.present(conference) || !ValidationUtils
							.present(location)); i++) {
				if(!ValidationUtils.present(conference)) {
					conference = src.getFirstPicaFieldValue(picaFields[0][i].split(":")[0], picaFields[0][i].split(":")[1]);
				}
				
				if(!ValidationUtils.present(location)) {
					location = src.getFirstPicaFieldValue(picaFields[1][i].split(":")[0], picaFields[1][i].split(":")[1]);
				}
				
			}

			//set the results
			target.setAuthor(null);
			target.setOrganization((conference != null ? conference : "NoOrganization") + 
					(location != null ? "<" + location + ">" : "<NoLocation>"));

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
 * organisation 029A/0X 029A/0X 029A/0X
 * 
 * 
 * kein autor sondern organisation: pica 029A $a <$c> sonst 029F $8 <$g> sonst
 * 029E $8 <$g> / $b marc 710 genauso
 * 
 * 002@ 1.char == 'O' (online resource) -> 009Q $u als url
 */
