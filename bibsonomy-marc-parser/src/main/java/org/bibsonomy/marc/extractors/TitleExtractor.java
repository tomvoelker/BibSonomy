package org.bibsonomy.marc.extractors;

import java.util.List;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.model.BibTex;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;

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
    	sb.append(r.getFirstFieldValue("245", 'a'));
    	return sb;
    /*	$tmp1 = $this->_getFieldArray('245', array('a'), false);
    	if (count($tmp1)> 0){
    	   $tmp = $tmp1[0];
    	}
    	
    	$tmp2 = $this->_getFieldArray('245', array('b'), false);
    	if (count($tmp2)> 0){
    		$tmp = $tmp.' : '.$tmp2[0];
    	}
    	
    	// Sortierzeichen weg
    	if (strpos($tmp, '@') !== false){
    		$occurrence = strpos($tmp, '@');
    		$tmp = substr_replace($tmp, '', $occurrence, 1);
    	}
    	
    	return $tmp;*/
    }

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) { 
		String val = getShortTitle(new StringBuilder(), src).toString();
		target.setTitle(val);
	}
}
