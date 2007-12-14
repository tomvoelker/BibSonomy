package scraper.url;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.model.util.BibTexUtils;


public class EndnoteToBibtexConverter {
	
	private static final Logger log = Logger.getLogger(EndnoteToBibtexConverter.class);
	
	private static Map<String,String> endnoteToBibtexEntryTypeMap = new HashMap<String,String>();
	private static Map<String,String> endnoteToBibtexFieldMap     = new HashMap<String,String>();
	
	private static final Pattern _eachLinePattern       = Pattern.compile("(?s)%\\w{1}\\s{1}.+?(?=%\\w{1}\\s{1})|%\\w{1}\\s{1}.++");
	private static final Pattern _dataExtractionPattern = Pattern.compile("(?s)(?<=%\\w{1}\\s{1}).+");

	
	static {
		// fill both maps to provide the data
		buildEndnoteToBibtexFieldMap();
		buildEndnoteToBibtexEntryTypeMap();
	}
	
	public static void main (String[] args){
	}
	
	//main method
	public Reader EndnoteToBibtex(BufferedReader in){
		
		try {
		
			StringBuffer result = new StringBuffer();

			// convert the data to a string
			String _fileToString = readLines(in);
			
			// split the endnote entry by 2 blank lines
			String[] _endNoteParts = _fileToString.split("(?m)^\\n{2,}(?=%\\w{1}\\s{1})");
			
			// process each endnote entry
			for (String part: _endNoteParts){
				result.append("\n\n" + processEntry(part));
			}
			
			// convert the String back to a Reader
			return new BufferedReader(new StringReader(result.toString()));
			
		} catch (Exception e){
			log.fatal("Could not import EndNote: " + e);
		}
		return null;
	}
	
	//main method to process the data
	private String processEntry(String entry){
		
		//initialise all necessary vars
		String _tempLine = null;
		String _tempData = null;
		Map<String,String> _resultBibtexFields = new HashMap<String,String>();
		
		try {
			// need to get every line (i.e. %T Foo %K bar)
			Matcher _eachLineMatcher = _eachLinePattern.matcher(entry);
			
			//process each line
			while (_eachLineMatcher.find()){
				
				//temporarily save the line in a String var
				_tempLine = _eachLineMatcher.group(0).trim();
				
				
				/*
				 * map the reference type
				 */
				if (endnoteToBibtexEntryTypeMap.containsKey(_tempLine)) {
					_resultBibtexFields.put("type", endnoteToBibtexEntryTypeMap.get(_tempLine));
				} else {
					/*
					 * extract all other data
					 * 
					 * /(?s)(?<=%\\w{1}\\s{1}).+/
					 * 
					 */
					Matcher _dataExtractionMatcher = _dataExtractionPattern.matcher(_eachLineMatcher.group(0));
					
					if (_dataExtractionMatcher.find()){
						_tempData = _dataExtractionMatcher.group(0).trim();
						
						/*
						 * handle standard fields
						 */
						String firstWord = _tempLine.substring(0, _tempLine.indexOf(" ")); // TODO: aus Matcher holen
						if (endnoteToBibtexFieldMap.containsKey(firstWord)) {
							final String bibtexFieldName = endnoteToBibtexFieldMap.get(firstWord);
							
							if (_resultBibtexFields.containsKey(bibtexFieldName)) {
								/*
								 * field already contained: special handling!
								 */
								if ("author".equals(bibtexFieldName)) {
									final String newAuthor = _resultBibtexFields.get(bibtexFieldName) + " and " + _tempData;
									_resultBibtexFields.put(bibtexFieldName, newAuthor);
								}
							} else {
								_resultBibtexFields.put(bibtexFieldName, _tempData);
							}
						}
					}
				}
			}
						
			/*
			 * special handling for ISBN/ISSN
			 */
			if (_resultBibtexFields.containsKey("isbn")) {
				final String isbn = _resultBibtexFields.get("isbn");
				if (isbn.length() == 8 || isbn.length() == 9) {
					_resultBibtexFields.remove("isbn");
					_resultBibtexFields.put("issn", isbn);
				}
			}
			
			/*
			 * build the bibtex key
			 */
			_resultBibtexFields.put("key", BibTexUtils.generateBibtexKey(_resultBibtexFields.get("author"), _resultBibtexFields.get("editor"), _resultBibtexFields.get("year"), _resultBibtexFields.get("title")));

			
		} catch (Exception e){
			log.fatal("Could not process the data: " + e);
		}
		
		/*
		 * in the end its necessary to build the complete bibtex part 
		 * i.e. 
		 * 
		 * @article{foo{
		 * author={bar and foo},
		 * title={foobars big revenge},
		 * abstract={this is a shot example}}
		 * 
		 */
		return buildBibtex(_resultBibtexFields);
	}
	
	//method to build a String with the complete Bibtex part
	private String buildBibtex(Map<String,String> data){
		
		//the StringBuffer to store the complete String
		StringBuffer result = new StringBuffer();
		
		try {
			/*
			 * if the data map is empty return null because it cant be an
			 * endnote file.
			 */
			if (data.isEmpty()){
				return null;
			}
			
			/*
			 * if no type is available the we use the following rules to specify the reference type
			 * 
			 * 1. %J and %V 					-> article (Journal Article)
			 * 2. %B 							-> incollection (Book Section)
			 * 3. %R but not %T 				-> misc (Report)
			 * 4. %I without %B, %J, or %R 		-> book (Book)
			 * 5. Neither %B, %J, %R, nor %I 	-> article (Journal Article)
			 */
			if (!data.containsKey("type")){
				if (data.containsKey("journal") && data.containsKey("volume")) {
					data.put("type", "article");
				} else if (data.containsKey("booktitle")) {
					data.put("type", "incollection");
				} else if (data.containsKey("address") && !(data.containsKey("booktitle") || data.containsKey("journal"))) {
					data.put("type", "book"); 
				} else if (!data.containsKey("booktitle") && !data.containsKey("journal") && !data.containsKey("%address")) {
					data.put("type", "article");
				}
			}
			
			// test if some necessary items were available, if not need to fix that with dummys
			if (!data.containsKey("type")) {
				data.put("type", "misc");
			}
			
			//now the bibtex part will be build completely starting with the type 
			result.append("@" + data.get("type") + "{" + data.get("key") + ",\n");
			data.remove("type");
			data.remove("key");
			
			//add every item of the data map to the stringbuffer
			for (String key : data.keySet()){
				result.append(key + " = {" + data.get(key) + "},\n");
			}

			// remove "," before last "\n"
			result.deleteCharAt(result.length() - 2 );
			
			// and at the end close the bibtexpart
			result.append("}");
			
		} catch (Exception e){
			log.fatal("Could not build the bibtex part :" + e);
		}
		
		//return the final String
		return result.toString();
	}
	
	//method to read all line out of the file and present them as String
	private String readLines(BufferedReader in) throws IOException{
		String line = null;
		StringBuffer _temp = new StringBuffer();
		
		try {
			while ((line = in.readLine()) != null){
				_temp.append(line+"\n");
			}
		} catch (Exception e){
			log.fatal("Could not read the lines out of the file :" + e);
		}
		
		return _temp.toString();
	}
	
	
	private static void buildEndnoteToBibtexFieldMap () {
		endnoteToBibtexFieldMap.put("%A",                     "author"); // Author
		endnoteToBibtexFieldMap.put("%B",                  "booktitle"); // Secondary Title
		endnoteToBibtexFieldMap.put("%C",               "howpublished"); // Place Published
		endnoteToBibtexFieldMap.put("%D",                       "year"); // Year
		endnoteToBibtexFieldMap.put("%E",                     "editor"); // Editor
//		endnoteToBibtexFieldMap.put("%F",                      "label"); // Label
//		endnoteToBibtexFieldMap.put("%G",                   "language"); // Language
//		endnoteToBibtexFieldMap.put("%H",          "translated_author"); // Translated Author
		endnoteToBibtexFieldMap.put("%I",                    "address"); // Publisher
		endnoteToBibtexFieldMap.put("%J",                    "journal"); // Journal Name
		endnoteToBibtexFieldMap.put("%K",                   "keywords"); // Keywords
//		endnoteToBibtexFieldMap.put("%L",                "call_number"); // Call Number
//		endnoteToBibtexFieldMap.put("%M",           "accession_number"); // Accession Number
		endnoteToBibtexFieldMap.put("%N",                     "series"); // Number
		endnoteToBibtexFieldMap.put("%P",                      "pages"); // Pages
//		endnoteToBibtexFieldMap.put("%Q",           "translated_title"); // Translated Title
//		endnoteToBibtexFieldMap.put("%R", "electronic_resource_number"); // Electronic Resource Number
//		endnoteToBibtexFieldMap.put("%S",             "tertiary title"); // Tertiary Title
		endnoteToBibtexFieldMap.put("%T",                      "title"); // Title
		endnoteToBibtexFieldMap.put("%U",                        "url"); // URL
		endnoteToBibtexFieldMap.put("%V",                     "volume"); // Volume
		// %W Database Provider
		endnoteToBibtexFieldMap.put("%X",                   "abstract"); // Abstract
		// %Y Tertiary Author
		endnoteToBibtexFieldMap.put("%Z",                     "annote"); // Notes
		// TODO: fehlende Felder (u.U. auskommentiert) einsetzen
		endnoteToBibtexFieldMap.put("%7",                    "edition"); // Edition
		endnoteToBibtexFieldMap.put("%&",                    "chapter"); // Section
		endnoteToBibtexFieldMap.put("%@",                       "isbn"); // ISBN/ISSN
		
	}
	
	private static void buildEndnoteToBibtexEntryTypeMap () {
		/*
		 * -> book
		 */
		endnoteToBibtexEntryTypeMap.put("%0 Book",            "book");
		endnoteToBibtexEntryTypeMap.put("%0 Edited Book",     "book");
		endnoteToBibtexEntryTypeMap.put("%0 Electronic Book", "book");
		
		/*
		 * -> article 
		 */
		endnoteToBibtexEntryTypeMap.put("%0 Journal Article",    "article"); 
		endnoteToBibtexEntryTypeMap.put("%0 Magazine Article",   "article");
		endnoteToBibtexEntryTypeMap.put("%0 Newspaper Article",  "article");
		endnoteToBibtexEntryTypeMap.put("%0 Electronic Article", "article");

		endnoteToBibtexEntryTypeMap.put("%0 Thesis",                 "mastersthesis");
		endnoteToBibtexEntryTypeMap.put("%0 Unpublished Work",       "unpublished");
		endnoteToBibtexEntryTypeMap.put("%0 Conference Paper",       "inproceedings");
		endnoteToBibtexEntryTypeMap.put("%0 Conference Proceedings", "proceedings");
		endnoteToBibtexEntryTypeMap.put("%0 Book Section",           "incollection");
		endnoteToBibtexEntryTypeMap.put("%0 Unpublished Work",       "unpublished");
			
		/*
		 * -> misc
		 */
		endnoteToBibtexEntryTypeMap.put("%0 Generic",               "misc"); 
		endnoteToBibtexEntryTypeMap.put("%0 Artwork",               "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Audiovisual Material",  "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Bill",                  "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Case",                  "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Chart or Table",        "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Classical Work",        "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Electronic Source",     "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Equation",              "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Figure",                "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Film or Broadcast",     "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Government Document",   "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Hearing",               "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Legal Rule/Regulation", "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Manuscript",            "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Map",                   "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Online Database",       "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Online Multimedia",     "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Patent",                "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Personal Communication","misc");
		endnoteToBibtexEntryTypeMap.put("%0 Report",                "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Statute",               "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Unused 1",              "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Unused 2",              "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Unused 2",              "misc");

		
	}

	
}