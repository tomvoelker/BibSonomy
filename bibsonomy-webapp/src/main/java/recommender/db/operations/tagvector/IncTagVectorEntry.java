/*
 * Created on 20.01.2006
 */
package recommender.db.operations.tagvector;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseCommand;

public class IncTagVectorEntry extends DatabaseCommand {
	private static final Logger log = Logger.getLogger(IncTagVectorEntry.class);
	private final String mySQLEntries;
	private final AbstractGetVectorEntries.Category c;
	
	IncTagVectorEntry(String mySQLEntries, AbstractGetVectorEntries.Category c) {
		this.mySQLEntries = mySQLEntries;
		this.c = c;
	}

	@Override
	protected String getSQL() {
		switch (c) {
		case CONTENT:
			return "INSERT INTO TagContent (tag_id,hash,ctr) VALUES " + mySQLEntries + " ON DUPLICATE KEY UPDATE ctr=ctr+1";
		case USER:
			return "INSERT INTO TagUser (tag_id,user_name,ctr) VALUES " + mySQLEntries + " ON DUPLICATE KEY UPDATE ctr=ctr+1";
		}
		log.fatal("unknown category " + c);
		return null;
	}
}
