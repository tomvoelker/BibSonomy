package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;


	/**
	 * 
	 * @author mgr
	 *
 	*/

/*
 * return a list of bookmark by a given hash.
 * Following arguments have to be given:
 * 
 * grouping:all
 * name:irrelevant
 * tags:NULL
 * hash:given
 * popular:false
 * added:false
 *   
 */
public class GetBookmarksByHash extends BookmarkChainElement {

	

	@Override
	protected List<Post<Bookmark>> handle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {

		final BookmarkParam param = new BookmarkParam();
		param.setHash(hash);
		param.setOffset(start);
		int limit=end-start;
		param.setLimit(limit);
		/**
		 * retrieve bookmark list with appropriate iBatis statement
		 */
		List<Post<Bookmark>> posts = db.getBookmarkByHash(param);
		if(posts.size()!=0){
			System.out.println("GetBookmarksByHash");
			
			
		}
		return posts;
		
		
	}
	
	/*
	 * prove arguments as mentioned above
	 */
	
	@Override
	protected boolean canHandle(String authUser,GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end){
		
		return hash!=null && hash.length()>0 &&
		grouping== GroupingEntity.ALL &&
		(tags==null||tags.size()==0)&&
		popular==false &&
		added==false;
		
	}
	
}
