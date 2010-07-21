package org.bibsonomy.importer.DBLP.configuration;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;


public class ConfigurationHandler extends DefaultHandler{
	
	/*
	 * XML-tag-names
	 */
	private static final String XML_ELEMENT_HOME = "home";
	
	private static final String XML_ELEMENT_CONSTANTS = "constants";
	
	private static final String XML_ELEMENT_URL = "url";
		
	private static final String XML_ELEMENT_USER = "user";
	
	private static final String XML_ELEMENT_DB = "db";
	
	private static final String XML_ELEMENT_DBUSER = "mysqluser";
	
	private static final String XML_ELEMENT_HOST = "mysqlhost";
	
	private static final String XML_ELEMENT_DATABASE = "mysqldatbase";
	
	private static final String XML_ELEMENT_PASSWORD = "mysqlpassword";
	
	/*
	 * result object
	 */
	private Configuration conResult;
	
	/*
	 * last read XML-Element(tag)
	 */
	private String lastReadElement = null;
	
	/*
	 * db XML-Element found
	 */
	private boolean dbconstants = false;
	
	public ConfigurationHandler(){
		super();
	}

	public Configuration getConResult() {
		return conResult;
	}

	public void setConResult(Configuration conResult) {
		this.conResult = conResult;
	}

	/*
	 * Search for XML-Elements and save this starting element. If this element is
	 * constants so build new result object. If this element is db so remember that
	 * is it found and the next elements are in db context.
	 */
    public void startElement (String uri, String name, String qName, Attributes atts){
    	if(name.equals(XML_ELEMENT_CONSTANTS) && !dbconstants){
    		conResult = new Configuration();//found constants
    		lastReadElement = XML_ELEMENT_CONSTANTS;
    	}else if(name.equals(XML_ELEMENT_URL) && !dbconstants){
    		lastReadElement = XML_ELEMENT_URL;
      	}else if(name.equals(XML_ELEMENT_USER) && !dbconstants){
    		lastReadElement = XML_ELEMENT_USER;		
    	}else if(name.equals(XML_ELEMENT_DB) && !dbconstants){
    		lastReadElement = XML_ELEMENT_DB;
    		dbconstants = true;
    	}else if(name.equals(XML_ELEMENT_HOST) && dbconstants){
    		lastReadElement = XML_ELEMENT_HOST;		
    	}else if(name.equals(XML_ELEMENT_DBUSER) && dbconstants){
    		lastReadElement = XML_ELEMENT_DBUSER;		
    	}else if(name.equals(XML_ELEMENT_DATABASE) && dbconstants){
    		lastReadElement = XML_ELEMENT_DATABASE;		
    	}else if(name.equals(XML_ELEMENT_PASSWORD) && dbconstants){
    		lastReadElement = XML_ELEMENT_PASSWORD;		
    	}else if(name.equals(XML_ELEMENT_HOME)){
    		lastReadElement = XML_ELEMENT_HOME;		
    	}

    }

    /*
     * detect the end of a XML tag
     */
    public void endElement (String uri, String name, String qName){
    	if(name.equals(XML_ELEMENT_CONSTANTS) && !dbconstants){
    	}else if(name.equals(XML_ELEMENT_URL) && !dbconstants){
    		lastReadElement="";
      	}else if(name.equals(XML_ELEMENT_USER) && !dbconstants){
    		lastReadElement="";
    	}else if(name.equals(XML_ELEMENT_DB) && dbconstants){
    		lastReadElement = "";
    		dbconstants = false;
    	}else if(name.equals(XML_ELEMENT_HOST) && dbconstants){
    		lastReadElement = "";		
    	}else if(name.equals(XML_ELEMENT_DBUSER) && dbconstants){
    		lastReadElement = "";		
    	}else if(name.equals(XML_ELEMENT_DATABASE) && dbconstants){
    		lastReadElement = "";		
    	}else if(name.equals(XML_ELEMENT_PASSWORD) && dbconstants){
    		lastReadElement = "";		
    	}else if(name.equals(XML_ELEMENT_HOME)){
    		lastReadElement = "";		
    	}
    
    }

    /*
     * this method detect and save the content of a xml element
     */
    public void characters (char ch[], int start, int length){
    	if(lastReadElement.equals(XML_ELEMENT_URL) && !dbconstants){
    		String newValue = new String(ch, start, length).trim();
    		if(!newValue.equals("")){
    			conResult.setUrl(newValue);
    		}
    	}else if(lastReadElement.equals(XML_ELEMENT_USER) && !dbconstants){
    		String newValue = new String(ch, start, length).trim();
    		if(!newValue.equals("")){
    			conResult.setUser(newValue);
    		}
    	}else if(lastReadElement.equals(XML_ELEMENT_HOST) && dbconstants){
    		String newValue = new String(ch, start, length).trim();
    		if(!newValue.equals("")){
    			conResult.setDbhost(newValue);	
    		}
    	}else if(lastReadElement.equals(XML_ELEMENT_DBUSER) && dbconstants){
    		String newValue = new String(ch, start, length).trim();
    		if(!newValue.equals("")){
    			conResult.setDbuser(newValue);
    		}
    	}else if(lastReadElement.equals(XML_ELEMENT_DATABASE) && dbconstants){
    		String newValue = new String(ch, start, length).trim();
    		if(!newValue.equals("")){
    			conResult.setDbname(newValue);
    		}
    	}else if(lastReadElement.equals(XML_ELEMENT_PASSWORD) && dbconstants){
    		String newValue = new String(ch, start, length).trim();
    		if(!newValue.equals("")){
    			conResult.setDbpassword(newValue);		
    		}
    	}else if(lastReadElement.equals(XML_ELEMENT_HOME)){
    		String newValue = new String(ch, start, length).trim();
    		if(!newValue.equals("")){
    			conResult.setHome(newValue);		
    		}
    	}
	}
}
