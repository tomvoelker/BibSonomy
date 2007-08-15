/*
 * Created on 20.01.2006
 */
package recommender.db.operations.tagvector;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseCommand;

public class SetModifiedTagsInUse extends DatabaseCommand {
	private static final Logger log = Logger.getLogger(SetModifiedTagsInUse.class);
	private final AbstractGetVectorEntries.Category c;
	
	public SetModifiedTagsInUse(AbstractGetVectorEntries.Category c) {
		this.c = c;
	}

	@Override
	protected String getSQL() {
		switch (c) {
		case CONTENT:
			return "UPDATE ContentModifiedTags SET in_use=1";
		case USER:
			return "UPDATE UserModifiedTags SET in_use=1";
		}
		log.fatal("unknown category " + c);
		return null;
	}
}
