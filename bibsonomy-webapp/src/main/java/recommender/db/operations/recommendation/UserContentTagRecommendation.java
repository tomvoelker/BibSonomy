/*
 * Created on 20.01.2006
 */
package recommender.db.operations.recommendation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseAction;
import recommender.db.operations.mostsimtags.GetExpandedTags;
import recommender.db.operations.tagvector.AbstractGetVectorEntries;
import recommender.db.operations.tagvector.GetVectorEntries;
import recommender.logic.InvertedTagVectorIterator;
import recommender.model.InvertedTagVector;
import recommender.model.InvertedTagVectorHashImpl;
import recommender.model.SimilarityCategory;
import recommender.model.SimpleMapEntry;
import recommender.model.TagVector;


/**
 * Misst Precision und Recall Werte für das Vorhersagen der Verwendung persönlich neuer Tags durch User.
 * 
 * @author Jens Illig
 */
public class UserContentTagRecommendation extends DatabaseAction<UserContentTagRecommendation.Recommendation> {
	private static final Logger log = Logger.getLogger(UserContentTagRecommendation.class);
	private final String userName;
	private final String hash;
	private final SimilarityCategory contentExpansionCategory;
	private final SimilarityCategory userExpansionCategory;
	private InvertedTagVector userItv = null;
	
	public UserContentTagRecommendation(final String userName, final String hash) {
		this(userName,hash, SimilarityCategory.CONTENT, SimilarityCategory.USER);
	}
	
	public UserContentTagRecommendation(final String userName, final String hash, SimilarityCategory contentExpansionCategory, SimilarityCategory userExpansionCategory) {
		this.hash = hash;
		this.userName = userName;
		this.contentExpansionCategory = contentExpansionCategory;
		this.userExpansionCategory = userExpansionCategory;
	}
	
	public UserContentTagRecommendation(final InvertedTagVector userItv, final String hash, SimilarityCategory contentExpansionCategory, SimilarityCategory userExpansionCategory) {
		this(userItv.getKey(), hash, contentExpansionCategory, userExpansionCategory);
		this.userItv = userItv;
	}
	
	private void initUserItv() {
		if (userItv == null) {
			GetVectorEntries gve = new GetVectorEntries(userName, AbstractGetVectorEntries.Category.USER, true);
			try {
				runDBOperation(gve);
				Iterator<InvertedTagVector> itvit = new InvertedTagVectorIterator(gve, InvertedTagVectorHashImpl.FACTORY);
				if (itvit.hasNext()) {
					userItv = itvit.next();
				}
			} finally {
				gve.close();
			}
			if (userItv == null) {
				log.fatal("unable to fetch InvertedTagVector of user");
			}
		}
	}
	
	@Override
	protected UserContentTagRecommendation.Recommendation action() {
		initUserItv();
		
		final Map<Integer,Double> userExp = new HashMap<Integer,Double>();;
		final Map<Integer,Double> contentExp = new HashMap<Integer,Double>();
		
		for (TagVector.Entry itve : userItv) {
			userExp.put( itve.getTagId(), (double) itve.getValue() );
		}
		double userTagsWithoutExpansion = userExp.size();
		if (userTagsWithoutExpansion == 0d) {
			userTagsWithoutExpansion = 1d;
		}
		if (userExpansionCategory != null) {
			GetExpandedTags getUserExp = new GetExpandedTags(userExpansionCategory, AbstractGetVectorEntries.Category.USER, userName, null);
			try {
				runDBOperation(getUserExp);
				if (getUserExp.hasNext()) {
					Map<Integer,Double> exp = getUserExp.next();
					for (Map.Entry<Integer,Double> entry : exp.entrySet()) {
						Double old = userExp.get(entry.getKey());
						if (old != null) {
							userExp.put( entry.getKey(), entry.getValue() * 0.2 + old);
						} else {
							userExp.put( entry.getKey(), entry.getValue() * 0.2);
						}
					}
				}
			} finally {
				getUserExp.close();
			}
		}
		log.debug("expanded user '" + userName + "'-tags by " + ((userExpansionCategory != null) ? userExpansionCategory.getMostSimTableName() : "null" ) + " to " + userExp.size() + " different tags");
		
		
		GetVectorEntries contentGve = new GetVectorEntries(hash, AbstractGetVectorEntries.Category.CONTENT, true);
		try {
			runDBOperation(contentGve);	
			for (TagVector.Entry entry : contentGve) {
				contentExp.put( entry.getTagId(), (double) entry.getValue() );
			}
		} finally {
			contentGve.close();
		}
		double contentTagsWithoutExpansion = contentExp.size();
		if (contentTagsWithoutExpansion == 0d) {
			contentTagsWithoutExpansion = 1d;
		}
		GetExpandedTags getExp = new GetExpandedTags(contentExpansionCategory, AbstractGetVectorEntries.Category.CONTENT, hash, null);
		try {
			runDBOperation(getExp);
			if (getExp.hasNext()) {
				Map<Integer,Double> exp = getExp.next();
				for (Map.Entry<Integer,Double> entry : exp.entrySet()) {
					Double old = contentExp.get(entry.getKey());
					if (old != null) {
						contentExp.put( entry.getKey(), entry.getValue() + old);
					} else {
						contentExp.put( entry.getKey(), entry.getValue());
					}
				}
			}
		} finally {
			getExp.close();
		}
		log.debug("expanded content '" + hash + "'-tags by " + ((contentExpansionCategory != null) ? contentExpansionCategory.getMostSimTableName() : "null" ) + " to " + contentExp.size() + " different tags");
		
		if (contentExp.size() > 0) {
			Map<Integer,Double> tagScores = new HashMap<Integer,Double>();
			for (Map.Entry<Integer,Double> contentExpEntry : contentExp.entrySet()) {
				Double userScore = userExp.get(contentExpEntry.getKey());
				if (userScore != null) {
					tagScores.put( contentExpEntry.getKey(), (1d + 16.0 * userScore / userTagsWithoutExpansion) * contentExpEntry.getValue());
				} else {
					tagScores.put( contentExpEntry.getKey(), 0.5 * contentExpEntry.getValue());
				} // TODO: bei expansion als liste anstatt als map muss addiert statt ersetzt werden sofern nicht sonst irgenwie gruppiert wurde
			}
			return new Recommendation(tagScores,true);
		} else {
			// TODO: wenn der User auch noch keine Tags hat, dann meistverwendete tags nehmen oder die vermutlich peinlich schlechten Ergebnisse lieber ganz weglassen.
			return new Recommendation(userExp,false);
		}
	}
	
	
		// alle book_url_hash werte eines users zwischen trainingEnd und testEnd aus tabelle bookmark holen
		// für jedes:
		// 	alle tagids holen, die der user im testzeitraum an den content geschrieben hat (aus tas join tags)
		//	diejenigen, die das system bereits kennt als relevant ansehen
		//	alle tags holen, die bisher dranstehen
		//	wenn mind- ein tag dransteht evaluieren:
		//		mostsimbycontent-expansion und werte davon NICHT durch contentitv.getTagFrequency teilen(bringt nur was für outer join faktoren, weil dadurch nur ganzer vektor skaliert wird und rightouterjoin auf usertag-expansion wird eh nicht gemacht)
		//		gleiches für mostsimcombivectoroverall und mostsimoverall
		//		für jede dieser expansionsmengen:
		//			bestenliste mit leftouterjoin(hash-lookups) auf usertags evaluieren (mismatches * 1/tagFrequency * 0.5)  (hier mismatch-anteil zählen => wie wichtig ist usertag-expansion)
		//			bestenliste mit leftouterjoin(hash-lookups) auf content-expansion der usertags evaluieren
		//			bestenliste mit leftouterjoin(hash-lookups) auf user-expansion der usertags evaluieren
		//			bestenliste mit leftouterjoin(hash-lookups) auf combivector-expansion der usertags evaluieren
		//			bestenliste mit leftouterjoin(hash-lookups) auf fullouter* user und content-expansion-kombination evaluieren
		//	wenn unbekannter content (<=> kein tag dransteht)
		//		diesen fall separat evaluieren auf beliebtesten tags des users
	
	public static class Recommendation {
		private final Set<Map.Entry<Integer,Double>> orderedScore;
		private boolean knownContent;
		
		public Recommendation(Map<Integer,Double> tagScores, boolean knownContent) {
			this.knownContent = knownContent;
			this.orderedScore = new TreeSet<Map.Entry<Integer,Double>>(new SimpleMapEntry.ValueComparator<Double>());
			for (Map.Entry<Integer,Double> entry : tagScores.entrySet()) {
				orderedScore.add(entry);
			}
		}
		
		public boolean isKnownContent() {
			return knownContent;
		}
		
		public Set<Map.Entry<Integer, Double>> getOrderedScore() {
			return orderedScore;
		}

	}

}
