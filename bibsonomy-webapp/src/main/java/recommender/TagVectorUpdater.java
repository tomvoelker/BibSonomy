/*
 * Created on 09.04.2006
 */
package recommender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import recommender.db.backend.Database;
import recommender.db.backend.DatabaseAction;
import recommender.db.operations.tags.GetTagIds;
import recommender.db.operations.tagvector.AbstractGetVectorEntries;
import recommender.db.operations.tagvector.DecTagVectorSpace;
import recommender.db.operations.tagvector.IncTagVectorSpace;
import recommender.db.operations.tagvector.MarkModified;
import resources.Resource;

public class TagVectorUpdater {
	private static final Logger log = Logger.getLogger(TagVectorUpdater.class);
	
	public static void doUpdate(final Collection<String> oldTags, final Resource r) {
		log.debug("oldTags=" + oldTags + " newTags=" + r.getTags());
		log.debug("oldHash=" + r.getOldHash() + " newHash=" + r.getHash());
		final Collection<String> newTags = r.getTags();
		final Collection<String> toDec;
		final Collection<String> toInc;
		if ((r.getHash().equals(r.getOldHash()) == true) && (newTags != null)) {
			toDec = new ArrayList<String>();
			for (String s : oldTags) {
				if (newTags.contains(s) == false) {
					toDec.add(s);
				}
			}
		} else {
			toDec = oldTags;
		}
		if ((r.getHash().equals(r.getOldHash()) == true) && (oldTags != null)) {
			toInc = new ArrayList<String>();
			for (String s : newTags) {
				if (oldTags.contains(s) == false) {
					toInc.add(s);
				}
			}
		} else {
			toInc = newTags;
		}
		
		new Database(false,new DatabaseAction<Object>() {

			@Override
			protected Object action() {
				Map<String,Integer> incTagIdMap = null;
				Map<String,Integer> decTagIdMap = null;
				if ((toInc != null) && (toInc.isEmpty() == false)) {
					GetTagIds incGtis = new GetTagIds(toInc);
					try {
						runDBOperation(incGtis);
						if (incGtis.hasNext() == true) {
							incTagIdMap = incGtis.next();
						}
					} finally {
						incGtis.close();
					}
				}
				if ((toDec != null) && (toDec.isEmpty() == false)) {
					GetTagIds decGtis = new GetTagIds(toDec);
					try {
						runDBOperation(decGtis);
						if (decGtis.hasNext() == true) {
							decTagIdMap = decGtis.next();
						}
					} finally {
						decGtis.close();
					}
				}
				if (decTagIdMap != null) {
					runDBOperation(new MarkModified(decTagIdMap.values(), AbstractGetVectorEntries.Category.CONTENT, false));
					runDBOperation(new DecTagVectorSpace(decTagIdMap.values(), r));
				}
				if (incTagIdMap != null) {
					runDBOperation(new MarkModified(incTagIdMap.values(), AbstractGetVectorEntries.Category.CONTENT, false));
					runDBOperation(new IncTagVectorSpace(incTagIdMap.values(), r));
				}
				
				return null;
			}
			
		});
	}
	
}
