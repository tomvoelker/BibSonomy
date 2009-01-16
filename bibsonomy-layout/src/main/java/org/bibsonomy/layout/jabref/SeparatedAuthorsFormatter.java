package org.bibsonomy.layout.jabref;


import net.sf.jabref.export.layout.LayoutFormatter;

/**
 * Create a valid author-string to get the right format to the DBLP XML
 * 
 */
public class SeparatedAuthorsFormatter implements LayoutFormatter {

	public String format(String fieldText) {
		final StringBuffer fin = new StringBuffer();

		//if the string contains an and it will be splitted else theres only one 
		//author!
        final String[] names = fieldText.split(" and ");
        for (int i=0; i<names.length; i++)
        {
          fin.append("<author>" + names[i]+ "</author>");
          if (i < names.length -1)
        	  fin.append("\n");
        }

        return fin.toString();
	}
}