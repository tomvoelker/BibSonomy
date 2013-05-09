/**
 *
 *  BibSonomy-MARC-Parser - Marc Parser for Bibsonomy
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.marc.extractors;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.marc.ExtendedMarcWithPicaRecord;
import org.bibsonomy.model.BibTex;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Subfield;

import java.util.HashMap;
import java.util.List;
/**
 * @author mve
 * @version $Id$
 */

public class TypeExtractor implements AttributeExtractor {
		private static final HashMap<String, String> map = getMap();
	    private static HashMap<String, String> getMap(){
	    	HashMap<String, String> typeMap = new HashMap<String, String>();
	    	typeMap.put("amxxx","book");
	    	typeMap.put("amco","dvd");
	    	typeMap.put("amcocd","cd");
	    	typeMap.put("amc ","cd");
	    	typeMap.put("amcr","ebook");
	    	typeMap.put("amh","microfilm");
	    	typeMap.put("amf","braille");
	    	typeMap.put("amo","kit");
	    	typeMap.put("asxxx","journal");
	    	typeMap.put("ast","journal");
	    	typeMap.put("ash","journal");
	    	typeMap.put("asco","journal");
	    	typeMap.put("ascocd","journal");
	    	typeMap.put("ascr","electronic");
	    	typeMap.put("asf","braille");
	    	typeMap.put("cmq","musicalscore");
	    	typeMap.put("csq","musicalscore");
	    	typeMap.put("ema","map");
	    	typeMap.put("esa","map");
	    	typeMap.put("gmm","video");
	    	typeMap.put("gmxxx","video");
	    	typeMap.put("gsm","video");
	    	typeMap.put("gsxxx","video");
	    	typeMap.put("ims","audio");
	    	typeMap.put("imcocd","cd");
	    	typeMap.put("jmxxx","audio");
	    	typeMap.put("jms","audio");
	    	typeMap.put("jmcocd","audio");
	    	typeMap.put("jsco","audio");
	    	typeMap.put("jss","audio");
	    	typeMap.put("kma","photo");
	    	typeMap.put("kmk","photo");
	    	typeMap.put("omxxx","kit");
	    	typeMap.put("omo","kit");
	    	typeMap.put("rmxxx","physicalobject");
	    	typeMap.put("rmz","physicalobject");
	    	typeMap.put("tmxxx","manuscript");
	        return typeMap;
	    }
		@Override
		public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
	    		
				if(!(src instanceof ExtendedMarcWithPicaRecord)) {
					target.setEntrytype("misc");
					return;
					// throws IllegalArgumentException
					//throw new IllegalArgumentException("record should be provided along with Pica record");
				}
				ExtendedMarcWithPicaRecord record = (ExtendedMarcWithPicaRecord)src;
		    	// format ist is detected by infos in Leader and kat 007
		    	Leader leader = src.getRecord().getLeader(); 
		    	List<DataField> fields = null;
		    	try { fields = src.getDataFields("007");   	
		    	} catch (IllegalArgumentException e) {}
		    	String tmp = src.getFirstFieldValue("300", 'a');
		    	String postfix = new String();
		    	String type = "misc";
		    	
		    	if (fields != null){
		    	   for(DataField field: fields){
		    	      Subfield subfield = field.getSubfield('c'); 
		    	      if (subfield != null){
		    	         // cd or dvd
		    	         if (subfield.getData().startsWith("co") 
		    	        		 && (tmp.toUpperCase().indexOf("DVD") == -1))
		    	      	    postfix = "cocd";
		    	      	 else {
		    	      		 subfield = (Subfield)(field.getSubfields().get(0));
		    	      	 }
		    	      }   
		    	      else { 
		    	    	  postfix = (field.getSubfields().get(0).toString());
		    	      }
		    	  }
		    	} else {
		    		postfix ="xxx";
		    	} 
		    	   	   
		    	char art = leader.getTypeOfRecord();
		    	char level = leader.getImplDefined1()[0];
		    	// now we have the three components art, level and phys. 
		    	//For some formats this is not enough and we need additional infos
		    	
		    	// preliminary solution for detection of series
		    	String s = record.getFirstPicaFieldValue("002@", "$0");
		    	
		    	if (s.indexOf("c")==1 ||
		    			s.indexOf("d")== 1 ){
		    		type="series";
		    	} else		    	
		    	// preliminary solution for articles		    	
		    	if (s.indexOf("o")==1){
		    		type="article";
		    	} else    	
		    	// preliminary solution for retro
		    	if (s.indexOf("r")==1){
		    		type="retro";
		    	} else {
		    	// return formats accourding to format arry in the beginning
		    	// of this method
		    		String value = map.get((art+""+level+postfix).trim()); 
		    		if (value!=null) type=value;
		        }
		    	// there is no format defined for the combination of art level and phys
		    	// for debugging
		    	target.setEntrytype(type);    	
		}
}