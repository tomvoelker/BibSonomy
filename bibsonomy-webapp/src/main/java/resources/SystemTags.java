package resources;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * class for handling system tags. 
 *
 * @version: $Id$
 * @author:  Stefan Stuetzer (sts)
 * $Author$
 *
 */
public class SystemTags {
    private static final Logger LOGGER = Logger.getLogger(SystemTags.class);
    
    /**
     * prefix for each system tag
     **/
    public final String SYSTEM_PREFIX = "system:";
    
    /**
     *  requested search string
     **/
    private String requestString;   
    
    /** 
     * maps system tag ID -> value 
     * (e.g. BIBTEX_YEAR -> 2005) 
     **/
    private Map<Integer,String> paramQueries;
    
    /**
     *  maps system tag to generated SQL subquery 
     * (e.g system:year: -> CAST(year AS SIGNED) = 2005 
     **/
    private Map<String,String>  paramValues;     
    
    /**
     * array of all possible system tags
     */
    public final String[] SYSTEM_TAGS = new String[] {SYSTEM_PREFIX + "year:"    /* bibtex year */};
        
    /**
     * constants for each system tag
     */
    public static final int BIBTEX_YEAR = 0;
    
    /**
     * default constructor
     * @param requestString
     * 			the requested search string for extracting system tags 
     */
    public SystemTags(String requestString) {
        this.requestString = requestString;        
        paramValues 	= new HashMap<String, String>();
        paramQueries 	= new HashMap<Integer, String>();
                        
        parse();
        generateSqlQueries();
    }
    
    /**
     * parses the search string for all known system tags
     * and fills value map with system tag and value
     */
    private void parse() {      
        LOGGER.debug("parse systemtags");       
        
        for (String systemTag: SYSTEM_TAGS) {
            if (requestString.indexOf(systemTag) != -1) {
                String requValue = null;
                int start = requestString.indexOf(systemTag);
                int end  = requestString.indexOf(" ", start);
                if (end == -1) {end = requestString.length();} // if last word in string                    
                requValue = requestString.substring(start + systemTag.length(), end); // extract information            
                paramValues.put(systemTag, requValue);
            }
        }       
    }
    
    /**
     * generates SQL subqueries for each parsed 
     * system tag 
     */
    private void generateSqlQueries() {
        LOGGER.debug("generate SQL queries");       
        for (String systemTag: paramValues.keySet()) {
            String value = paramValues.get(systemTag).trim();
            StringBuffer sql = new StringBuffer(" AND");
            
            // BibTeX year
            if (systemTag.equals(SYSTEM_TAGS[BIBTEX_YEAR])) {
                sql.append(" CAST(year AS SIGNED)");
                
                // 1st case: only year (e.g. 2005)
                if (value.matches("[12]{1}[0-9]{3}")) {
                    sql.append(" = " + value);
                } 
                // 2nd case: range (e.g. 2001-2006)
                else if (value.matches("[12]{1}[0-9]{3}-[12]{1}[0-9]{3}")) {
                    String[] years = value.split("-");
                    sql.append(" BETWEEN " + years[0] + " AND " + years[1]);
                }
                // 3rd case: upper bound (e.g -2005) means all years before 2005 
                else if(value.matches("-[12]{1}[0-9]{3}")) {
                    sql.append(" <= " + value.substring(1,value.length()));
                }
                // 4th case: lower bound (e.g 1998-) means all years since 1998 
                else if(value.matches("[12]{1}[0-9]{3}-")) {
                    sql.append(" >= " + value.substring(0,(value.length())-1));
                }
                paramQueries.put(BIBTEX_YEAR, sql.toString());
            }   
            
        }       
    }
    
    /**
     * returns subquery for requested system tag
     * @param tag
     * 			system tag ID (e.g. BIBTEX_YEAR)
     * @return SQL subquery
     */
    public String getQuery(final int tag) {
        if (paramQueries.keySet().contains(tag) && paramQueries.get(tag) != null)
            return paramQueries.get(tag);
        else
            return "";
    }
    
    /**
     * returns map of systemtag ID and subqueries
     * @return hashmap
     */
    public Map<Integer,String> getParamQueries() {
        return paramQueries;
    }
}