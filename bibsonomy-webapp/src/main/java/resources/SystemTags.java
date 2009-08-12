package resources;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for handling system tags. 
 *
 * @author Stefan Stuetzer
 * @version $Id$
 */
public class SystemTags {
    private static final Log LOGGER = LogFactory.getLog(SystemTags.class);
    
    /** prefix for each system tag */
    public static final String SYSTEM_PREFIX = "sys:";
    
    /** requested search string */
    private String requestString;   
    
    /**
     *  maps system tag to generated SQL subquery 
     * (e.g system:year: -> CAST(year AS SIGNED) = 2005 )
     **/
    private Map<String,String>  paramValues;     
    
    /** array of all possible system tags */
    private static final String[] SYSTEM_TAGS = new String[] {	SYSTEM_PREFIX + "year:",  /* bibtex year */
    															SYSTEM_PREFIX + "user:",  /* user name */ 
    															SYSTEM_PREFIX + "group:"  /* group name */};

    /** constants for system tag: bibtex year */
    public static final int BIBTEX_YEAR = 0;
    /** constants for system tag: username */
    public static final int USER_NAME 	= 1;
    /** constants for system tag: groupname */
    public static final int GROUP_NAME	= 2;

    /**
     * default constructor
     * 
     * @param requestString
     * 			the requested search string for extracting system tags 
     */
    public SystemTags(final String requestString) {
        this.requestString = requestString;        
        this.paramValues   = new HashMap<String, String>();                           
        this.parse();
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
     * returnes if specified tag is used in the request
     * @param tag
     * 			the id of the tag
     * @return
     * 			<code>true</code> if tag was used <br>
     * 			<code>false</code> if not
     */
    public boolean isUsed(final int tag) {
    	return paramValues.keySet().contains(SYSTEM_TAGS[tag]);
    }
    
    /**
     * generates the sql subquery for the specified tag 
     * @param tag
     * 			the id of the tag
     * @param tableName
     * 			the tablename
     * @return
     * 			the sql string
     */
    public String generateSqlQuery(final int tag, String tableName) {
        LOGGER.debug("generate SQL query for " + tag);          
        final String systemTag = SYSTEM_TAGS[tag];
        
        if (paramValues.keySet().contains(systemTag)) {
	        String value = paramValues.get(systemTag).trim();
	        StringBuffer sql = new StringBuffer(" AND ");
	        
	        // BibTeX year
	        if (SYSTEM_TAGS[BIBTEX_YEAR].equals(systemTag)) {
	            sql.append("CAST(" + tableName + ".year AS SIGNED)");
	            
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
	            
	            return sql.toString();
	        }  
	        
	        // User Name
	        if (SYSTEM_TAGS[USER_NAME].equals(systemTag)) {
	        	sql.append(tableName + ".user_name = ?");        	
	        	return sql.toString();
	        }
	        
	        // group name
	        if (SYSTEM_TAGS[GROUP_NAME].equals(systemTag)) {
	        	sql.append(tableName + ".user_name IN ("
	        			+ " SELECT user_name FROM groupids g JOIN groups gs ON (g.group = gs.group) "
	        			+ " WHERE g.group_name = ?)");	        		        	
	        	return sql.toString();
	        }
        }        
		return "";              
    }
    
    /**
     * returns the value of the specified system tag
     * @param tag
     * 			the id of the tag
     * @return value of the system tag
     */
    public String getValue(final int tag) {
    	if (isUsed(tag)) return paramValues.get(SYSTEM_TAGS[tag]);
    	return "";
    }

    /**
     * returns a cleaned request string. 
     * this means all systemtags are removed.
     * 
     * @return clean string
     */
    public String getCleanedString() {    		
		final Scanner scan = new Scanner(requestString);
		final StringBuffer buf = new StringBuffer();

		while(scan.hasNext()) {
            final String token = scan.next();           

            if (token.indexOf(SystemTags.SYSTEM_PREFIX) == -1)
                buf.append(token + " ");
        }

		return buf.toString().trim();
    }
}