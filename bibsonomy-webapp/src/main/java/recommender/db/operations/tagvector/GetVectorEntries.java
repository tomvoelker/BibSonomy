/*
 * Created on 20.01.2006
 */
package recommender.db.operations.tagvector;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Liefert TagVector.Entry Objekte.zu einem bestimmten oder allen Tags aus der Datenbank
 * 
 * @author Jens Illig
 */
public class GetVectorEntries extends AbstractGetVectorEntries {
	private static final Logger log = Logger.getLogger(GetVectorEntries.class);
	private final Category c;
	private String key = null; 
	private final boolean orderByKey;
	
	public GetVectorEntries(int tagId, Category c, boolean orderByKey) {
		super(tagId);
		this.c = c;
		this.orderByKey = orderByKey;
	}
	
	public GetVectorEntries(Category c, boolean orderByKey) {
		super();
		this.c = c;
		this.orderByKey = orderByKey;
	}
	
	public GetVectorEntries(String key, Category c, boolean orderByKey) {
		this(c, orderByKey);
		this.key = key;
	}

	@Override
	protected String getSQL() {
		if (orderByKey) {
			switch (c) {
			case CONTENT:
				return (getTagId() != null) ? "SELECT tag_id,hash,ctr FROM TagContent WHERE tag_id=? ORDER BY hash" : ((key != null) ? "SELECT tag_id,hash,ctr FROM TagContent WHERE hash=?": "SELECT tag_id,hash,ctr FROM TagContent ORDER BY hash");
			case USER:
				return (getTagId() != null) ? "SELECT tag_id,user_name,ctr FROM TagUser WHERE tag_id=? ORDER BY user_name" : ((key != null) ? "SELECT tag_id,user_name,ctr FROM TagUser WHERE user_name=?": "SELECT tag_id,user_name,ctr FROM TagUser ORDER BY user_name");
			}	
		} else {
			switch (c) {
			case CONTENT:
				return (getTagId() != null) ? "SELECT tag_id,hash,ctr FROM TagContent WHERE tag_id=?" : ((key != null) ? "SELECT tag_id,hash,ctr FROM TagContent WHERE hash=?": "SELECT tag_id,hash,ctr FROM TagContent ORDER BY tag_id");
			case USER:
				return (getTagId() != null) ? "SELECT tag_id,user_name,ctr FROM TagUser WHERE tag_id=?" : ((key != null) ? "SELECT tag_id,user_name,ctr FROM TagUser WHERE user_name=?": "SELECT tag_id,user_name,ctr FROM TagUser ORDER BY tag_id");
			}
		}
		log.fatal("unknown category");
		throw new IllegalStateException("unknown category");
	}

	protected void setParams(PreparedStatement stmnt) throws SQLException {
		if (getTagId() != null) {
			super.setParams(stmnt);
		} else if (key != null) {
			stmnt.setString(1,key);
		}
	}
}
