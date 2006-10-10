/*
 * Created on 19.05.2006
 */
package org.bibsonomy.db.rshandlers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.bibsonomy.db.model.Tag;

import de.innofinity.dbcmd.core.ResultSetHandler;

public class TagRSHandler extends ResultSetHandler<Tag> {
	private static final Logger log = Logger.getLogger(TagRSHandler.class);
	
	@Override
	protected Tag buildWrapper(final ResultSet rs) throws SQLException {
		return new Tag() {
			
			public String getName() {
				return extractStringOrNull(rs,"tag_name");
			}

			public Integer getOverallUsageCount() {
				return extractIntegerOrNull(rs, "tag_ctr");
			}

			public Integer getTagId() {
				Integer i = extractIntegerOrNull(rs, "tag_id");
				log.debug("extraxted " + i);
				return i;
			}

			public Integer getUserUsageCount() {
				return extractIntegerOrNull(rs, "user_tag_ctr");
			}
			
		};
	}
}
