package org.bibsonomy.marc.extractors;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.util.StringUtils;

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
    public StringBuilder getShortTitle(StringBuilder sb, ExtendedMarcRecord r)
    {
    	// 245 $a_:_$b
    	r.appendFirstFieldValueWithDelmiterIfPresent(sb, "245", 'a', "");
    	StringUtils.replaceFirstOccurrence(sb, "@", "");
    	return sb;
    }
    
    public StringBuilder getSubtitle(StringBuilder sb, ExtendedMarcRecord r) {
    	r.appendFirstFieldValueWithDelmiterIfPresent(sb, "245", 'h', "");
    	r.appendFirstFieldValueWithDelmiterIfPresent(sb, "245", 'b', " : ");
    	r.appendFirstFieldValueWithDelmiterIfPresent(sb, "245", 'c', " / ");
    	return sb;
    }

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		StringBuilder sb = new StringBuilder();
		getShortTitle(sb, src);
		int l = sb.length();
		getSubtitle(sb, src);
		if (sb.length() > l) {
			sb.insert(l, " ");
		}
		String val = sb.toString();
		target.setTitle(val);
	}
}
/*
 * Typ:
 *  Pica: 13H 0k   k->Konferenz   -> 090? f->festschrift
 *  Pica: 002@ 2.char b    -> marc? 006?
 *  
 *  
 *  TODO: titel bei mehrbändern (knuth)
 */
