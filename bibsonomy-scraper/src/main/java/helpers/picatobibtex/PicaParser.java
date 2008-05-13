package helpers.picatobibtex;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * 
 * The bibtexkey is manufactured in accordance to the 013H category of pica+.
 * http://www.allegro-c.de/formate/f/f0528.htm
 * 
 * TODO: need to fix that nearly every category is repeatable
 * TODO: all this should have a better structure
 * TODO: fix special chars
 * 
 * @author C. Kramer
 * @version $Id$
 */
public class PicaParser{
	private static final Logger log = Logger.getLogger(PicaParser.class);
	private PicaRecord pica = null;
	private String  url = null;
	
	// String array with all regex pieces to be replaced
	private String[] cleaning = {"@", "&lt;.+?&gt;", "\\{", "\\}"};
	
	/**
	 * @param pica
	 * @param url 
	 */
	public PicaParser(final PicaRecord pica, final String url){
		this.url = url;
		this.pica = pica;
	}
	
	/**
	 * start parsing
	 * 
	 * @return String
	 * @throws Exception 
	 */
	public String getBibRes() throws Exception{		
		return parse();
	}
	
	/**
	 * method to activate the parsing methods and form the complete bibtex string
	 * 
	 * @return String
	 */
	private String parse() throws Exception{
		StringBuffer bibres = new StringBuffer();
		
		String type = getBibType();
		String author = cleanString(getAuthor());
		String title = cleanString(getTitle());
		String year = cleanString(getYear());
		String isbn = cleanString(getISBN());
		String issn = cleanString(getISSN());
		String ppn = cleanString(getPPN());
		String series = cleanString(getSeries());
		String abstr = cleanString(getAbstract());
		String tags = cleanString(getTags());
		String publisher = cleanString(getPublisher());
			
		String bibkey = createBibkey(author,year);
			
			
		bibres.append(type + bibkey + ",\n");
		bibres.append("author = {" + author + "},\n");
		bibres.append("title = {" + title + "},\n");
		bibres.append("year = {" + year + "},\n");
		bibres.append("abstract = {" + abstr + "}, \n");
		bibres.append("keywords = {" + tags + "}, \n");
		bibres.append("url = {" + url + "}, \n");
		bibres.append("ppn = {" + ppn +  "}, \n");
		bibres.append("series = {" + series + "}, \n");
		bibres.append("isbn = {" + isbn + "}, \n");
		bibres.append("issn = {" + issn + "}, \n");
		bibres.append("publisher = {" + publisher + "}, \n");
		bibres.append("}");
		
		return bibres.toString();
	}

	/**
	 * catch all authors by pica cat 028C or 028A & 028B and create the bibtex form i.e.: author 
	 * and author
	 * 
	 * @return String
	 */
	private String getAuthor(){
		HashMap<String, String> authorCat = new HashMap<String, String>();
		Vector<String> authors = new Vector<String>();
		String authorResult = "";
		
		// fill
		authorCat.put("028C", "028C");
		authorCat.put("028A", "028B");
		authorCat.put("028D", "028D");
		
		Set<String> set = authorCat.keySet();
		
		for(String s : set){
			// get the main category
			if(pica.isExisting(s)){
				String _tempAuthor = null;
				_tempAuthor = new String();
				if(pica.getRow(s).isExisting("$8")){
					_tempAuthor = getData(s, "$8");
				} else if (pica.getRow(s).isExisting("$a")){
					_tempAuthor = getData(s, "$a");
					_tempAuthor +="," + getData(s, "$d");
				}
				
				_tempAuthor += getSubAuthors(authorCat.get(s));
				
				authors.add(_tempAuthor);
			} 
		}
		
		for(String _temp : authors){
			if (authorResult.length() < 1){
				authorResult = _temp;
			} else {
				authorResult += "and " + _temp;
			}
		}

		return authorResult;
	}
	
	/**
	 * method to create the bibtex key
	 * 
	 * @return String
	 */
	private String getBibType(){
		Row r = null;
		SubField s = null;
		
		/*
		 * tests if the category 013H is existing and test for some values, 
		 * if not check if the title category 021A has a $d subfield and if "proceedings" matches
		 * if thats the case then its a proceeding. If the entry has a ISBN and NO ISSN then its a book,
		 * if it has a ISSN and NO ISBN then its an article otherwise if it has ISBN AND ISSN
		 * its usually a proceeding.
		 * 
		 * If the 013H category is set and the $0 subfield provides the value u then
		 * it will be decided between phdthesis, masterthesis and techreport
		 */
		if ((r = pica.getRow("013H")) != null){
			if ((s = r.getSubField("$0")) != null){
				
				if ("u".equals(s.getContent())){
					Row _tempRow = null;
					SubField _tempSub = null;
					
					if ((_tempRow = pica.getRow("037C")) != null){
						if ((_tempSub = _tempRow.getSubField("$c")) != null){
							String _tempCont = _tempSub.getContent();
							if (_tempCont.matches("^.*Diss.*$")){
								return "@phdthesis{";
							} else if (_tempCont.matches("^.*Master.*$")){
								return "@masterthesis{";
							} else {
								return "@techreport{";
							}
						}
					}
				}
			}
		} 
			
		if(pica.getRow("021A").isExisting("$d")){
			if (pica.getRow("021A").getSubField("$d").getContent().trim().matches("^.*proceedings.*$")){
				return "@proceedings{";
			}
		} 
		
		if((pica.isExisting("004A") || pica.isExisting("004D")) && !pica.isExisting("005A") && !pica.isExisting("005D")) {
			return "@book{";
		}
		
		if(((pica.isExisting("005A")) || pica.isExisting("005D")) && !pica.isExisting("004A") && !pica.isExisting("004D")){
			return "@article{";
		}
		
		if(((pica.isExisting("004A")) || pica.isExisting("004D")) && (pica.isExisting("005A") || pica.isExisting("005D"))){
			return "@proceedings{";
		}
		
		return "@misc{";
	}
	
	/**
	 * extract the title by pica cat 021A
	 * 
	 * @return String
	 */
	private String getTitle(){
		String res = "";
		
		res = getData("021A", "$a");
		res += " " + getData("021A", "$d");
		res += " " + getData("021A", "$h");
		
		return res;
	}
	
	/**
	 * extract the ppn
	 * 
	 * @return
	 */
	private String getPPN(){
		String res = "";
		
		res = getData("003@", "$0");
		
		return res;
	}
	
	/**
	 * extract Series
	 * 
	 * @return
	 */
	private String getSeries(){
		String res = "";
		
		res = getData("036E", "$a");
		
		return res;
	}
	
	/**
	 * retrieve the year by the pica cat 011@
	 * 
	 * @return String
	 */
	private String getYear(){
		String year = "";
		
		year = getData("011@", "$a");

		if (year.length() == 0){
			year = getData("011@", "$n");
		}
		
		return year;
	}
	
	/**
	 * extract the issn
	 * 
	 * @return issn
	 */
	private String getISSN(){
		String res = "";
		
		res = getData("005A", "$0");
		if(res.length() == 0){
			 res = getData("005A", "$A"); 
		}
		
		return res;
	}
	
	/**
	 * extract the isbn
	 * 
	 * @return isbn
	 */
	private String getISBN(){
		String res = "";
		
		res = getData("004A", "$0");
		if (res.length() == 0){
			res = getData("004A", "$A");
		}
		
		return res;
	}
	
	/**
	 * Tries to extract the abstract
	 * 
	 * @return abstract String
	 */
	private String getAbstract(){
		String abstr = "";
		abstr = getData("046M", "$a");
		return abstr;
	}
	
	/**
	 * Tries to get all tag by categories 044K and 041A
	 * 
	 * @return String of tags
	 */
	private String getTags(){
		String tags = "";
		
		LinkedList<Row> list = null;
		Row row = null;
		
		if((list = pica.getRows("044K")) != null){
			for(Row r : list){
				if(r.isExisting("$8")){
					tags += r.getSubField("$8").getContent() + " ";
				}
			}
		} else if(pica.isExisting("041A")){
			String cat = "041A";
			tags += getData(cat, "$8") + " ";
			
			int ctr = 1;
			
			row = pica.getRow(cat + "/0" + Integer.toString(ctr));
			
			while(row != null){
				String newCat = cat + "/0" + Integer.toString(ctr);
				
				if(row.isExisting("$8")){
					tags += getData(newCat, "$8") + " ";
				}
				
				ctr++;
	
				if (ctr < 10){
					row = pica.getRow(cat + "/0" + Integer.toString(ctr));
				} else {
					row = pica.getRow(cat + "/" + Integer.toString(ctr));
				}
			}
		}

		
		return tags;
	}
	
	/**
	 * extract publisher
	 * 
	 * @return
	 */
	private String getPublisher(){
		String res = "";

		res = getData("033A", "$n");
		res += " " + getData("033A", "$p");
		
		return res;
	}
	
	/**
	 * This helpmethod should create the bibtexkey be author and year
	 * 
	 * @param author
	 * @param year
	 * @return BibtexKey
	 */
	private String createBibkey(final String author, final String year){
		String key = "";
		
		if ("".equals(author) && "".equals(year)){
			return "imported";
		}
		
		if (author.matches("^(.+?),.*$")){
			Pattern p = Pattern.compile("^(.+?),.*$");
			Matcher m = p.matcher(author);
			if(m.find()){
				key += m.group(1);
			}
		}
		
		key += ":" + year;
		
		return key;
	}
	
	/**
	 * use this method to get the data out of a specific row and subfield
	 * 
	 * @param cat
	 * @param sub
	 * @return
	 */
	private String getData(final String cat, final String sub){
		Row r = null;
		SubField f = null;
		
		if ((r = pica.getRow(cat)) != null){
			if ((f = r.getSubField(sub)) != null){
				return f.getContent();
			}
		}
		
		return "";
	}
	
	/**
	 * Tries to clean the given String from i.e. internal references like @
	 * 
	 * @param toClean
	 * @return String
	 */
	private String cleanString(String toClean){
		String res = toClean;
		
		for (String s : cleaning){
			res = res.replaceAll(s, "");
		}
		
		
		return res;
	}
	
	private String getSubAuthors(final String cat){
		String author = "";
		Row row = null;
		
		// get all other author by specific category
		if (cat != null){
			int ctr = 1;
			
			row = pica.getRow(cat + "/0" + Integer.toString(ctr));
			
			while(row != null){
				String newCat = cat + "/0" + Integer.toString(ctr);
				
				if(row.isExisting("$8")){
					author += " and " + getData(newCat, "$8");
				} else {
					author += " and " + getData(newCat, "$a");
					author += "," + getData(newCat, "$d");
				}
				
				ctr++;
	
				if (ctr < 10){
					row = pica.getRow(cat + "/0" + Integer.toString(ctr));
				} else {
					row = pica.getRow(cat + "/" + Integer.toString(ctr));
				}
			}
		}
		
		return author;
	}
}
