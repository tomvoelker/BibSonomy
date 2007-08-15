/*
 * Created on 20.01.2006
 */
package recommender.db.operations.tagvector;

import org.apache.log4j.Logger;

/**
 * Liefert die TagVector.Entry Objekte.zu allen Tags welche zum angegebenen
 * Tag bzgl. Content /User-verwendung benachbart (also mindestens einmal gemeinsam
 * verwendet) sind aus der Datenbank.
 * 
 * @author Jens Illig
 */
public class GetNeighbourVectorEntries extends AbstractGetVectorEntries {
	private static final Logger log = Logger.getLogger(GetVectorEntries.class);
	private Category c;
	
	public GetNeighbourVectorEntries(int tagId, Category c) {
		super(tagId);
		this.c = c;
	}

	@Override
	protected String getSQL() {
		switch (c) {
		case CONTENT:
			return "SELECT tb.tag_id, tb.hash, ctr FROM TagContent tb JOIN (SELECT hash FROM TagContent WHERE tag_id=? ORDER BY hash) tmp ON tb.hash=tmp.hash";
		case USER:
			return "SELECT tu.tag_id, tu.user_name, ctr FROM TagUser tu JOIN (SELECT user_name FROM TagUser WHERE tag_id=? ORDER BY user_name) tmp ON tu.user_name=tmp.user_name";
		}
		log.fatal("unknown category");
		throw new IllegalStateException("unknown category");
	}
}
