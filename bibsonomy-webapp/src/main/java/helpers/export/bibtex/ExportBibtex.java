package helpers.export.bibtex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.Globals;
import net.sf.jabref.JabRefPreferences;
import net.sf.jabref.KeyCollisionException;
import net.sf.jabref.export.FileActions;
import net.sf.jabref.export.layout.Layout;
import net.sf.jabref.export.layout.LayoutHelper;
import net.sf.jabref.imports.BibtexParser;
import net.sf.jabref.imports.ParserResult;

import org.apache.log4j.Logger;

import resources.Bibtex;
import resources.Resource;
import servlets.DocumentUploadHandler;
import servlets.listeners.InitialConfigListener;

/**
 * This Singleton provides methods to export BibTeX entries
 * to several formats.
 * @author sre
 *
 */
public final class ExportBibtex {

	private static final Logger log = Logger.getLogger(ExportBibtex.class);

	private final static String _layoutFileExtension = ".layout";

	private static String _rootPath			         = null;
	private static String _layoutDirectory          = "layout";
	private static final String message_no_custom_layout = "You don't have a custom filter installed. Please go to the settings page and install one.";


	// this Map saves all loaded layouts (html, bibtexml, tablerefs, hash(user.username),...)
	private static HashMap<String,Layout> _layouts   = new HashMap<String, Layout>();


	/** Only on instance will be instantiated during the very first access. */
	private static final ExportBibtex instance = new ExportBibtex();

	/** Constructor collects all needed infos and data.*/
	private ExportBibtex(){
		_rootPath = InitialConfigListener.getInitParam("rootPath");//((String) ((Context) new InitialContext().lookup("java:/comp/env")).lookup("rootPath"));			

		/* 
		 * initialize JabRef preferences. This is neccessary ... because they use global 
		 * preferences and if we don't initialize them, we get NullPointerExceptions later 
		 */
		Globals.prefs = JabRefPreferences.getInstance();
		
		// load default filters 
		try {
			loadDefaultFilters();
		} catch (URISyntaxException ex) {
			log.fatal("could not load default layout files");
		}
	}

	public static ExportBibtex getInstance() {
		return instance;
	}

	public Object clone()throws CloneNotSupportedException {
		throw new CloneNotSupportedException(); 
	}

	/**
	 * Unloads layout objects adequate to deleted custom filter.
	 * @param hashedName Hash representing the deleted document.
	 */
	public void unloadCustomFilter(String hashedName){
		synchronized(_layouts) {
			_layouts.remove(hashedName);
		}
	}

	/**
	 * This is the export method for BibTeX entries to any available format. 
	 * @param bibtexList Entries to export.
	 * @param userName User to whom the passed entries belong 
	 * @param format Format to export to. If format == "custom" export with user specific export filter
	 * @return output The formatted BibTeX entries as a string.
	 * @throws Exception, IOException
	 */
	public StringBuffer exportBibtex(Collection<Bibtex> bibtexList, String userName, String format) throws IOException, Exception {
		StringBuffer output = new StringBuffer();  
		
		BibtexDatabase database = bibtex2JabrefDB(bibtexList);
		// Write database entries; entries will be sorted as they
		// appear on the screen, or sorted by author, depending on
		// Preferences.
		List<BibtexEntry> sorted = FileActions.getSortedEntries(database, null, false);
		
		if("custom".equals(format)) {

			/* *************** Printing the header ******************/
			String hash = userLayoutHash(userName, LayoutType.BEGIN);
			appendFile(output, new File((_rootPath + "bibsonomy_docs/" + hash.substring(0, 2).toLowerCase()) + "/" + hash));
				
			/* *************** Printing the entries ******************/ 

			Layout layout = getUserLayout(userName);

			for(BibtexEntry entry: sorted){	              
				output.append(layout.doLayout(entry, database));
			}	        

			/* *************** Printing the footer ******************/
			hash = userLayoutHash(userName, LayoutType.END);
			appendFile(output, new File((_rootPath + "bibsonomy_docs/" + hash.substring(0, 2).toLowerCase()) + "/" + hash));
			
		} else {
			//must be one of the default formats like html, bibtexml, docbook, tablerefs, tablerefsabsbib, openoffice, harvard or endnote


			/* *************** Printing the header ******************/
			Layout beginLayout = _layouts.get(format + ".begin" + _layoutFileExtension);
			if (beginLayout != null) {
				output.append(beginLayout.doLayout(database));
			}

			/* *************** Printing the entries ******************/ 

			// try to retrieve type-specific layouts and process output
			for(BibtexEntry entry: sorted) {

				// We try to get a type-specific layout for this entry
				Layout currLayout = _layouts.get(format + "." + entry.getType().getName().toLowerCase() + _layoutFileExtension);
				if (currLayout == null) {
					// no type-specific layout available, take default
					currLayout = _layouts.get(format + _layoutFileExtension);
				}	            

				if (currLayout != null) {
					// Write the entry
					output.append(currLayout.doLayout(entry, database));
				}
			}

			/* *************** Printing the footer ******************/
			Layout endLayout = _layouts.get(format + ".end" + _layoutFileExtension);
			if (endLayout != null) {
				output.append(endLayout.doLayout(database));
			}
		}
		return output;
	}

	/** Returns the layout for the given user
	 * 
	 * @param userName
	 * @return
	 * @throws Exception if layout is not available
	 */
	private Layout getUserLayout(String userName) throws Exception {
		String hashedName = userLayoutHash(userName, LayoutType.ITEM);
		// check if custom filter exists
		if(!_layouts.containsKey(hashedName)){
			// custom filter of current user is not loaded yet -> check if a filter exists at all
			if(!loadCustomFilter(userName)){
				// no custom filter for this user -> exception
				throw new Exception(message_no_custom_layout);    			
			}	 	
		}
		/* *************** Printing the entries ******************/
		Layout defLayout = _layouts.get(hashedName);
		if(defLayout == null){//custom filter deleted meanwhile -> exception
			throw new Exception(message_no_custom_layout);		    		
		}
		return defLayout;
	}

	/**
	 * This method converts BibSonomy BibTeX entries to JabRef entries and stores
	 * them into a JabRef specific BibtexDatabase! 
	 * @param bibtexList List of BibSonomy BibTeX objects
	 * @return BibtexDatabase
	 * @throws IOException
	 * @throws KeyCollisionException If two entries have exactly the same BibTeX key
	 */
	private BibtexDatabase bibtex2JabrefDB(Collection<Bibtex> bibtexList) {
		ParserResult result = null;
		StringBuffer bibtexStrings = new StringBuffer();

		for(Bibtex b: bibtexList){
			bibtexStrings.append(" " + b.getBibtex());
		} 

		try {
			result = BibtexParser.parse(new StringReader(bibtexStrings.toString()));
		} catch (IOException e) {
			log.fatal("error parsing bibtex objects for JabRef output: " + e);
		}

		return result.getDatabase();
	}

	/**
	 * Loads default filters (xxx.xxx.layout and xxx.layout) from BibSonomy`s default layout directory into a map.
	 * @throws URISyntaxException 
	 * @throws Exception
	 */
	private void loadDefaultFilters() throws URISyntaxException{
		
		Stack<File> dirs = new Stack<File>();
		
		/*
		 * start searching in default layout directory
		 */
		final Class<ExportBibtex> myClass = ExportBibtex.class;
		final URL url = myClass.getResource(_layoutDirectory); // URL to layout directory
		final File startdir = new File(url.toURI());

		// add first directory to stack
		if (startdir.isDirectory()) 
			dirs.push(startdir);
		
		while (dirs.size() > 0) {
			for (File file : dirs.pop().listFiles()){
				if (file.isDirectory()){	        
					dirs.push( file );
				}else{
					//check extension
					if (DocumentUploadHandler.matchExtension(file.getName(), _layoutFileExtension)){
						try{
							LayoutHelper layoutHelper = new LayoutHelper(new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8")));
							synchronized(_layouts){
								// NOTE: now case of layouts is ignored
								_layouts.put(file.getName().toLowerCase(), layoutHelper.getLayoutFromText(Globals.FORMATTER_PACKAGE));
							}
						}catch (Exception e) {
							log.fatal("error loading default filters: " + e);
						}
					}//if	
				}//else
			}//for
		}//while
	}


	/**
	 * Loads user filter from BibSonomy into a map.
	 * @param userName The user who requested a filter
	 * @return true If loading was successful. Return false, if loading failed because user does not have a custom filter.
	 */
	private boolean loadCustomFilter(String userName){
		
		String hashedName = userLayoutHash(userName, LayoutType.ITEM);		
		// build path from first two letters of file name hash
		String docPath = _rootPath + "bibsonomy_docs/" + hashedName.substring(0, 2).toLowerCase();

		//read file (docPath = /home/stud/sre/bibsonomy_docs/4c + hashedName = 4c432434rk345j33...)
		try {
			File file = new File(docPath + "/" + hashedName);
			if(file.exists()){
				LayoutHelper layoutHelper = new LayoutHelper(new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8")));
				synchronized(_layouts){
					_layouts.put(hashedName, layoutHelper.getLayoutFromText(Globals.FORMATTER_PACKAGE));
				}
				return true;
			}
		} catch (Exception e) {
			log.fatal("error loading custom filter for user " + userName + ": " + e);
		}		
		return false;
	}

	public String toString() {
		return _layouts.toString();
	}

	public static enum LayoutType {
		BEGIN, END, ITEM;
		
		private static String[] allTypes = new String[]{"begin", "item", "end"};
		
		public static LayoutType getLayoutType (String type) {
			if ("begin".equals(type)) return BEGIN;
			if ("end".equals(type)) return END;
			return ITEM;
		}
		
		private String getString (LayoutType type) {
			if (type == BEGIN) return "begin";
			if (type == END) return "end";
			return "item";
		}
		
		public String toString() {
			return getString(this);
		}
		
		public static String[] getLayoutTypes () {
			return allTypes;
		}
		
	}
	
	/** Reads file in UTF-8 format and appends the contents to the buf.
	 * 
	 * @param buf - buffer which the file contents should be appended to.
	 * @param file - text file which is assumed to be in UTF-8 format.
	 * 
	 * @throws IOException
	 */
	private static void appendFile (StringBuffer buf, File file) throws IOException {
		if (file.exists()) {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String line = null;
			while ((line = r.readLine()) != null) {
				buf.append(line).append("\n");
			}
		}
	}
	
	/** Builds the hash for the custom layout files of the user. Depending on the 
	 * layout type the hash differs.
	 * 
	 * @param user
	 * @param type
	 * @return
	 */
	public static String userLayoutHash (String user, LayoutType type) {
		return Resource.hash("user." + user + "." + type + _layoutFileExtension);
	}

}
