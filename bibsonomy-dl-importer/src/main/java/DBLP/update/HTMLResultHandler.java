package DBLP.update;

import java.util.LinkedList;

import org.w3c.dom.*;

import DBLP.parser.DBLPEntry;
import DBLP.parser.DBLPParseResult;

public class HTMLResultHandler{
	
	private static final String HTML_ID_WARNING    = "warning_entry";
	private static final String HTML_ID_DUPLICATE  = "duplicate_entry";
	private static final String HTML_ID_INCOMPLETE = "incomplete_entry";
	
	private static final String UPLOAD_ERROR_HEADING = "upload error";
	
	public static void searchFailureMessage(LinkedList<DBLPEntry> list, DBLPParseResult result, Document html){
		
		/*
		 * by default a uploadinfo page is given
		 */
		boolean isWarningPage = true;
		
		/*
		 * check if this site is a upload_error
		 */
		NodeList h1list = html.getElementsByTagName("h1");
		for(int i=0; i<h1list.getLength();i++){
			Node h1 = h1list.item(i);
			String heading = h1.getChildNodes().item(0).getNodeValue();
			/*
			 * start position of UPLOAD_ERROR_HEADING if this h1 element is the right element
			 */
			int index = heading.length()-13;
			if(index>0){
				heading = heading.substring(index);
				if(heading.equals(UPLOAD_ERROR_HEADING))
					isWarningPage=false;//found upload_error
			}
		}
		
		if(isWarningPage){
			NodeList ullist = html.getElementsByTagName("ul");
			for( int x=0; x<ullist.getLength(); x++){
				Node ul = ullist.item(x);
				Node id = ul.getAttributes().getNamedItem("id");
				if (id == null) continue;
				String text = id.getNodeValue();// type of the warning
				/*
				 * search for warnings
				 */
				if(HTML_ID_WARNING.equals(text)){
					NodeList warningNodes = ul.getChildNodes();
					for(int i=0; i<warningNodes.getLength(); i++){
						if(warningNodes.item(i).getNodeName().equals("li")){//get only the li elements of the warning
							Node node = warningNodes.item(i);
							String warning = node.getChildNodes().item(0).getNodeValue();
							
							if(warning.indexOf('{')+1 > 0 && warning.indexOf(",") > 0){
								warning = warning.substring(warning.indexOf('{')+1, warning.indexOf(","));
								for(DBLPEntry entry: list){
									if(entry.getDblpKey().equals(warning)){
										result.getInsert_warning().add(entry.getDblpKey());
										result.getInsert_warning().add(node.getChildNodes().item(0).getNodeValue());
										result.getEval().incInsert_warning_count();
										break;
									}
								}
							}
															
						}
					}
				}
				
				/*
				 * search for duplicate warnings
				 */
				if(HTML_ID_DUPLICATE.equals(text)){
					NodeList duplicateNodes = ul.getChildNodes();
					for(int i=0; i<duplicateNodes.getLength(); i++){
						if(duplicateNodes.item(i).getNodeName().equals("li")){//get only the li elements of the warning
							Node node = duplicateNodes.item(i);
							String warning = node.getChildNodes().item(0).getNodeValue();
							
							if(warning.indexOf('{')+1 > 0 && warning.indexOf(",") > 0){
								warning = warning.substring(warning.indexOf('{')+1, warning.indexOf(","));
								for(DBLPEntry entry: list){
									if(entry.getDblpKey().equals(warning)){
										result.getInsert_duplicate().add(entry.getDblpKey());
										result.getEval().incInsert_warning_count();
										break;
									}
								}
							}
															
						}
					}
				}
				
				/*
				 * search for incomplete warnings
				 */
				if(HTML_ID_INCOMPLETE.equals(text)){
					NodeList incompleteNodes = ul.getChildNodes();
					for(int i=0; i<incompleteNodes.getLength(); i++){
						if(incompleteNodes.item(i).getNodeName().equals("li")){//get only the li elements of the warning
							Node node = incompleteNodes.item(i);
							String warning = node.getChildNodes().item(0).getNodeValue();
							
							if(warning.indexOf('{')+1 > 0 && warning.indexOf(",") > 0){
								warning = warning.substring(warning.indexOf('{')+1, warning.indexOf(","));
								for(DBLPEntry entry: list){
									if(entry.getDblpKey().equals(warning)){
										if(entry.getAuthor() == null && entry.getEditor() == null && entry.getTitle() != null){
											result.getInsert_incomplete_author_editor().add(entry);
										}
										result.getInsert_incomplete().add(entry.getDblpKey());
										result.getEval().incInsert_incomplete_count();
										break;
									}
								}
							}
						}
					}
				}
			}
		} else {
			StringBuffer buffer = new StringBuffer("upload_error: entries=");
			for(DBLPEntry entry: list){
				buffer.append(entry.generateSnippet() + " ");
			}
			result.getUpload_error().add(buffer.toString());
			result.getEval().incUpload_error_count();
		}
	}	
	
}