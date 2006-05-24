/*
 * Created on 20.01.2006
 */
package org.bibsonomy.db.commands.tags;

import org.bibsonomy.db.model.Tag;
import org.bibsonomy.db.rshandlers.TagRSHandler;

import de.innofinity.dbcmd.Helper;
import de.innofinity.dbcmd.core.DBQuery;

public class GetTagNames extends DBQuery<Tag> {
	private final String tagIdsCSV;
	
	public GetTagNames(final Iterable<Integer> tagIds) {
		super(new TagRSHandler());
		this.tagIdsCSV = Helper.buildCSVList(tagIds);
	}

	@Override
	protected String getSQL() {
		return "SELECT tag_name as tag_name, tag_id as tag_id FROM tags WHERE tag_id IN (" + tagIdsCSV + ")";
	}
}
