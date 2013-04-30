package org.bibsonomy.marc;

import java.util.List;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;

/**
 * @author jensi
 * @version $Id$
 */
public class TitleExtractor {
	public String getTitle(Record r) {
		StringBuilder sb = new StringBuilder();
		getShortTitle(sb, r);
		return sb.toString();
	}
	
    /**
     * Get the short (pre-subtitle) title of the record.
     *
     * @return string
     * @access protected
     */
    public void getShortTitle(StringBuilder sb, Record r)
    {
    	// 245 $a_:_$b
    	List<DataField> tmp1 = (List<DataField>) r.getVariableFields("245");
    	sb.append(tmp1.get(0). find("a"));
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
}
