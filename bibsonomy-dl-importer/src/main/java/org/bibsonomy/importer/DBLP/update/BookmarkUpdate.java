package org.bibsonomy.importer.DBLP.update;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.importer.DBLP.db.DBHandler;
import org.bibsonomy.importer.DBLP.parser.DBLPEntry;
import org.bibsonomy.importer.DBLP.parser.DBLPParseResult;


public class BookmarkUpdate{


	private final static Log log = LogFactory.getLog(BookmarkUpdate.class);
	
	private HTTPBookmarkUpdate httpBookUpdate = null;
	private DBLPParseResult presult = null;
	private DBHandler handler = null;
	
	public BookmarkUpdate (HTTPBookmarkUpdate httpBookUpdate, DBLPParseResult presult, DBHandler handler) throws MalformedURLException, IOException {
		this.presult = presult;
		this.handler = handler;
		this.httpBookUpdate = httpBookUpdate;
	}
	
	private static final int maxNumberOfEntries = 10;	

	public int update(HashSet<String> db_bookmark_keys)throws Exception{
		int count = 0;
		count += handleEntries(db_bookmark_keys, presult.getWwwlist());
		count += handleEntries(db_bookmark_keys, presult.getInsert_incomplete_author_editor());
		return count;
	}

	private int handleEntries(HashSet<String> db_bookmark_keys, LinkedList<DBLPEntry> entryList) {
		final LinkedList<DBLPEntry> list = new LinkedList<DBLPEntry>();
		int count = 0;

		for(final DBLPEntry entry:entryList) {

			if(entry.getEntrydate().after(presult.getDblpdate())){
				list.add(entry);

				if(list.size() >= maxNumberOfEntries){
					count += list.size();
					try{
						httpBookUpdate.deleteOldBookmarkByEntry(list, handler, db_bookmark_keys);
						httpBookUpdate.insertNewBookmark(list, presult);
					}catch(Exception e){
						log.fatal("DBLPUpdater: BookmarkUpdate: ", e);
					}
					list.clear();
				}
			}
		}
		count += list.size();
		if(list.size()>1){//store the rest
			try{
				httpBookUpdate.deleteOldBookmarkByEntry(list, handler, db_bookmark_keys);
				httpBookUpdate.insertNewBookmark(list, presult);
			}catch(Exception e){
				log.fatal("DBLPUpdater: BookmarkUpdate: ", e);
			}
		}
		return count;
	}

}