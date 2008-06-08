package helpers.picatobibtex.rules;

import helpers.picatobibtex.PicaRecord;
import helpers.picatobibtex.PicaUtils;
import helpers.picatobibtex.Row;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

/**
 * @author daill
 * @version $Id$
 */
public class AuthorRule implements Rules {
	private PicaRecord pica = null;
	private PicaUtils utils = null;
	private HashMap<String, String> authorCat = new HashMap<String, String>();
	
	/**
	 * @param pica
	 * @param utils
	 */
	public AuthorRule(PicaRecord pica, PicaUtils utils){
		this.pica = pica;
		this.utils = utils;
	}
	
	public String getContent() {
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
				if (pica.getRow(s).isExisting("$a")){
					_tempAuthor = utils.getData(s, "$a");
					_tempAuthor +="," + utils.getData(s, "$d");
				} else if(pica.getRow(s).isExisting("$8")){
					_tempAuthor = utils.getData(s, "$8");
					_tempAuthor = _tempAuthor.replaceAll("\\*.*\\*", "");
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

		return utils.cleanString(authorResult);
	}

	public boolean isAvailable() {
		Set<String> set = authorCat.keySet();
		
		for(String s : set){
			if(pica.isExisting(s)){
				return true;
			}
		}
		
		return false;
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
					author += " and " + utils.getData(newCat, "$8");
				} else {
					author += " and " + utils.getData(newCat, "$a");
					author += "," + utils.getData(newCat, "$d");
				}
				
				ctr++;
	
				if (ctr < 10){
					row = pica.getRow(cat + "/0" + Integer.toString(ctr));
				} else {
					row = pica.getRow(cat + "/" + Integer.toString(ctr));
				}
			}
		}
		
		return utils.cleanString(author);
	}

}
