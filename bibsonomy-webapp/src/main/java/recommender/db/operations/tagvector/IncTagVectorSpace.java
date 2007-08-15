/*
 * Created on 20.01.2006
 */
package recommender.db.operations.tagvector;

import java.util.Collection;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseAction;
import resources.Bibtex;
import resources.Bookmark;
import resources.Resource;


/**
 * Zählt die Verwendungsanzahlen eines Tags bzgl. Nutzer und Inhalt hoch
 * 
 * @author Jens Illig
 */
public class IncTagVectorSpace extends DatabaseAction<Object> {
	private static final Logger log = Logger.getLogger(IncTagVectorSpace.class);
	private final Collection<Integer> tagIds;
	private final Resource res;
	
	public IncTagVectorSpace(final Collection<Integer> tagIds, final Resource res) {
		this.tagIds = tagIds;
		this.res = res;
	}
	
	@Override
	protected Object action() {
		String contentKey = res.getContentType() + res.getHash();
		StringBuilder tuData = new StringBuilder();
		StringBuilder tcData = new StringBuilder();
		for (Integer i : tagIds) {
			if (tuData.length() > 0) {
				tuData.append(',');
				tcData.append(',');
			}
			tuData.append('(').append(i).append(",'").append(res.getUser()).append("',1)");
			tcData.append('(').append(i).append(",'").append(contentKey).append("',1)");
		}
		runDBOperation( new IncTagVectorEntry( tuData.toString(), AbstractGetVectorEntries.Category.USER) );
		if ((res instanceof Bookmark) || (res instanceof Bibtex)) {
			runDBOperation( new IncTagVectorEntry( tcData.toString(), AbstractGetVectorEntries.Category.CONTENT) );
		} else {
			log.error("unknown ContentType");
			return null;
		}
		
		return null;
	}

}
