package resources;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * JUnit test for SystemTags class 
 * @version: $Id$
 * @author:  Stefan Stuetzer (sts)
 * $Author$
 */
public class SystemTagsTest {
    private static final Logger LOGGER = Logger.getLogger(SystemTagsTest.class);
    
    @Test
    public void testBibtexYearSingle() {
        SystemTags st = new SystemTags("Stumme Hotho system:year:2005");        
        String testSQL = " AND CAST(year AS SIGNED) = 2005";                
        assertEquals(st.getQuery(SystemTags.BIBTEX_YEAR), testSQL);
    }   
    
    @Test
    public void testBibtexYearRange() {
        SystemTags st = new SystemTags("Stumme !Schmitz system:year:2002-2005");        
        String testSQL = " AND CAST(year AS SIGNED) BETWEEN 2002 AND 2005";                 
        assertEquals(st.getQuery(SystemTags.BIBTEX_YEAR), testSQL);
    }   
    
    @Test
    public void testBibtexYearUpperRange() {
        SystemTags st = new SystemTags("Stumme system:year:-2005 Hotho");       
        String testSQL = " AND CAST(year AS SIGNED) <= 2005";               
        assertEquals(st.getQuery(SystemTags.BIBTEX_YEAR), testSQL);
    }   
    
    @Test
    public void testBibtexYearLowerRange() {
        SystemTags st = new SystemTags("Stumme system:year:1998-");     
        String testSQL = " AND CAST(year AS SIGNED) >= 1998";               
        assertEquals(st.getQuery(SystemTags.BIBTEX_YEAR), testSQL);
    }
}