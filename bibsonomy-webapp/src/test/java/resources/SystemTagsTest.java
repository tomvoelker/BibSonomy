package resources;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * JUnit test for SystemTags class 
 *
 * @author Stefan Stuetzer
 * @version $Id$
 */
public class SystemTagsTest {

    @Test
    public void testBibtexYearSingle() {
        SystemTags st = new SystemTags("Stumme Hotho sys:year:2005");        
        String testSQL = " AND CAST(bibtex.year AS SIGNED) = 2005";                
        assertEquals(st.generateSqlQuery(SystemTags.BIBTEX_YEAR,"bibtex"), testSQL);
    }   
    
    @Test
    public void testBibtexYearRange() {
        SystemTags st = new SystemTags("Stumme !Schmitz sys:year:2002-2005");        
        String testSQL = " AND CAST(bibtex.year AS SIGNED) BETWEEN 2002 AND 2005";                 
        assertEquals(st.generateSqlQuery(SystemTags.BIBTEX_YEAR,"bibtex"), testSQL);
    }   
    
    @Test
    public void testBibtexYearUpperRange() {
        SystemTags st = new SystemTags("Stumme sys:year:-2005 Hotho");       
        String testSQL = " AND CAST(bibtex.year AS SIGNED) <= 2005";               
        assertEquals(st.generateSqlQuery(SystemTags.BIBTEX_YEAR,"bibtex"), testSQL);
    }   
    
    @Test
    public void testBibtexYearLowerRange() {
        SystemTags st = new SystemTags("Stumme sys:year:1998-");     
        String testSQL = " AND CAST(bibtex.year AS SIGNED) >= 1998";               
        assertEquals(st.generateSqlQuery(SystemTags.BIBTEX_YEAR,"bibtex"), testSQL);
    }
    
    @Test
    public void testUserName() {
        SystemTags st = new SystemTags("Wille82 sys:user:stumme");     
        String testSQL = " AND user.user_name = ?";               
        assertEquals(st.generateSqlQuery(SystemTags.USER_NAME,"user"), testSQL);
    }
}
