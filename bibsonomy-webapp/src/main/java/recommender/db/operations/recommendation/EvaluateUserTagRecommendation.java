/*
 * Created on 20.01.2006
 */
package recommender.db.operations.recommendation;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import recommender.RecommenderFrontEnd;
import recommender.db.backend.DatabaseAction;
import recommender.db.backend.DatabaseQuery;
import recommender.db.operations.GetBookURLHashOfUserAfterDate;
import recommender.db.operations.GetTaggingsOnContentIdInTimeSpan;
import recommender.db.operations.tagvector.AbstractGetVectorEntries;
import recommender.db.operations.tagvector.GetUsedTagIds;
import recommender.db.operations.tagvector.GetVectorEntries;
import recommender.logic.EvaluationResults;
import recommender.logic.InvertedTagVectorIterator;
import recommender.model.InvertedTagVector;
import recommender.model.InvertedTagVectorHashImpl;
import recommender.model.RecommendedTag;
import recommender.model.SimilarityCategory;
import resources.Bookmark;


/**
 * Misst Precision und Recall Werte für das Vorhersagen der Verwendung persönlich neuer Tags durch User.
 * 
 * @author Jens Illig
 */
public class EvaluateUserTagRecommendation extends DatabaseAction<Object> {
	private static final Logger log = Logger.getLogger(EvaluateUserTagRecommendation.class);
	private static final Date trainingEnd = new Date(new GregorianCalendar(2004,12,1).getTime().getTime());
	private static final Date testEnd = new Date(new GregorianCalendar(2005,1,1).getTime().getTime());
	private final List<EvaluationResults> results = new ArrayList<EvaluationResults>();
	private final List<EvaluationResults> forKnownContentResults = new ArrayList<EvaluationResults>();
	private final List<EvaluationResults> forUnknownContentResults = new ArrayList<EvaluationResults>();
	private final List<EvaluationResults> forContentResults = new ArrayList<EvaluationResults>();
	
	private int knownNewPostContents = 0;
	private int unknownNewPostContents = 0;
	
	@Override
	protected Object action() {
		int testCount = 0;
		Set<Integer> knownTags = null;
		
		GetUsedTagIds getUsedTagIds = new GetUsedTagIds();
		try {
			runDBOperation(getUsedTagIds);
			knownTags = GetUsedTagIds.buildSet(getUsedTagIds);
		} finally {
			getUsedTagIds.close();
		}
		log.debug(knownTags.size() + " known tags");
		GetVectorEntries gve = new GetVectorEntries(AbstractGetVectorEntries.Category.USER, true);
		try {
			runDBOperation(gve);
			//int iteratedUsers = 0;
			InvertedTagVectorIterator itvi = new InvertedTagVectorIterator(gve, InvertedTagVectorHashImpl.FACTORY);
			for (InvertedTagVector itv : itvi) {
				/*if (++iteratedUsers > 100) {
					break; // TODO: nach debuglauf entfernen
				}*/

				log.debug("processing user " + itv.getKey());
				/*
				Map<SimilarityCategory,GetExpandedTags> expAndPreviouslyUnusedByCategory = new EnumMap<SimilarityCategory,GetExpandedTags>(SimilarityCategory.class);
				// für jede Ähnlichkeitsberechnungsmethode die absteigend nach Ähnlichkeit sortierte Liste von (tag_id,expansionscore) Paaren
				Map<SimilarityCategory,Set<Map.Entry<Integer,Double>>> orderedExpansionByCategory = new EnumMap<SimilarityCategory,Set<Map.Entry<Integer,Double>>>(SimilarityCategory.class);
				// für jede Ähnlichkeitsberechnungsmethode die Map tag_id->expansionscore
				Map<SimilarityCategory,Map<Integer,Double>> mapExpansionByCategory = new EnumMap<SimilarityCategory,Map<Integer,Double>>(SimilarityCategory.class);
				for (SimilarityCategory c : SimilarityCategory.values()) {
					expAndPreviouslyUnusedByCategory.put(c,new GetExpandedTags(c, AbstractGetVectorEntries.Category.USER, itv.getKey(), ((InvertedTagVectorHashImpl)itv).getVectorMap().keySet() ) );
				}
				
				for (Map.Entry<SimilarityCategory,GetExpandedTags> entry : expAndPreviouslyUnusedByCategory.entrySet()) {
					log.debug("beginning " + entry.getKey() + "-expansion");
					try {
						GetExpandedTags geuts = entry.getValue();
						runDBOperation(geuts);
						if (geuts.hasNext() == true) {
							Map<Integer,Double> expMap = geuts.next();
							Set<Map.Entry<Integer,Double>> expOrdered = (Set<Map.Entry<Integer,Double>>) GetExpandedTags.getOrderedSet(expMap);
							log.debug("expanded to " + expOrdered.size() + " entries");
							orderedExpansionByCategory.put(entry.getKey(), expOrdered);
							mapExpansionByCategory.put(entry.getKey(), expMap);
						} else {
							log.error("no " + entry.getKey() + "-Tag-Expansion for user " + itv.getKey());
						}
					} finally {
						for (GetExpandedTags geuts : expAndPreviouslyUnusedByCategory.values()) {
							geuts.close();
						}
					}
				} 
				// es wird nur als relevant gewertet, was dem system auch bereits bekannt ist und was der user noch nie verwendet hat.
				GetTagIdsOfUserAfterDate getRelevantTaggings = new GetTagIdsOfUserAfterDate( itv.getKey(), trainingEnd, testEnd, ((InvertedTagVectorHashImpl)itv).getVectorMap().keySet(), knownTags);
				try {
					runDBOperation(getRelevantTaggings);
					if (getRelevantTaggings.hasNext()) {
						Map.Entry<Map<Integer,Integer>,Integer> relevantTaggingsInfo = getRelevantTaggings.next();
						Map<Integer,Integer> relevantTaggings = relevantTaggingsInfo.getKey();
						double overallRelevantCount = relevantTaggingsInfo.getValue();
						if (overallRelevantCount > 0d) {
							++testCount;
							int resultIndex = 0;
							// jede ÄhnlichkeitsKategorie einzeln auswerten:
							for (SimilarityCategory c : SimilarityCategory.values()) {
								Iterable<Map.Entry<Integer,Double>> expansion = orderedExpansionByCategory.get(c);
								if (expansion != null) {
									evaluate(expansion, relevantTaggings, overallRelevantCount, c.toString(), resultIndex, results);
									++resultIndex;
								}
							}
							// noch ein paar Kombinationen wagen:
							Iterable<Map.Entry<Integer,Double>> userExp = orderedExpansionByCategory.get(SimilarityCategory.USER);
							Iterable<Map.Entry<Integer,Double>> contentExp = orderedExpansionByCategory.get(SimilarityCategory.CONTENT);
							Map<Integer,Double> userExpMap = mapExpansionByCategory.get(SimilarityCategory.USER);
							Map<Integer,Double> contentExpMap = mapExpansionByCategory.get(SimilarityCategory.CONTENT);
							if (userExp == null) {
								if (contentExp != null) {
									evaluate(contentExp, relevantTaggings, overallRelevantCount, "USER + CONTENT", resultIndex++, results);
									// das wird 0: evaluate(orderedScores, relevantTaggings, overallRelevantCount, "USER * CONTENT", resultIndex++);
									// hier ändert sich die Werungsreighenfolge nicht:
									evaluate(contentExp, relevantTaggings, overallRelevantCount, "USER * CONTENT", resultIndex++, results);
								}
							} else {
								if (contentExp == null) {
									evaluate(userExp, relevantTaggings, overallRelevantCount, "USER + CONTENT", resultIndex++, results);
									// das wird 0: evaluate(orderedScores, relevantTaggings, overallRelevantCount, "USER * CONTENT", resultIndex++);
									// hier ändert sich die Werungsreighenfolge nicht:
									evaluate(userExp, relevantTaggings, overallRelevantCount, "USER * CONTENT", resultIndex++, results);
								} else {
									// tun, was eigtl. getan werden soll:
									Set<TagScore> orderedScoresSum = new TreeSet<TagScore>(GetExpandedTags.comparator);
									Set<TagScore> orderedScoresProd = new TreeSet<TagScore>(GetExpandedTags.comparator);
									Set<TagScore> orderedScoresProdFullOuter = new TreeSet<TagScore>(GetExpandedTags.comparator);
									for (Map.Entry<Integer,Double> userEntry : userExp) {
										Double contentVal = contentExpMap.get(userEntry.getKey());
										if (contentVal != null) {
											orderedScoresSum.add(new TagScore(userEntry.getKey(), userEntry.getValue() + contentVal));
											orderedScoresProd.add(new TagScore(userEntry.getKey(), userEntry.getValue() * contentVal));
											orderedScoresProdFullOuter.add(new TagScore(userEntry.getKey(), (1d + userEntry.getValue()) * (1d + contentVal)));
										} else {
											orderedScoresSum.add(new TagScore(userEntry.getKey(), userEntry.getValue()));
											// =0 : orderedScoresProd.add(new TagScore(userEntry.getKey(), userEntry.getValue() * contentEntry.getValue()));
											orderedScoresProdFullOuter.add(new TagScore(userEntry.getKey(), userEntry.getValue() * 0.5));
										}
									}
									for (Map.Entry<Integer,Double> contentEntry : contentExp) {
										if (userExpMap.containsKey(contentEntry.getKey()) == false) {
											orderedScoresSum.add(new TagScore(contentEntry.getKey(), contentEntry.getValue()));
											// =0 : orderedScoresProd.add(new TagScore(contentEntry.getKey(), contentEntry.getValue() * userEntry.getValue()));
											orderedScoresProdFullOuter.add(new TagScore(contentEntry.getKey(), contentEntry.getValue() * 0.5));
										}
									}
									evaluate(orderedScoresSum, relevantTaggings, overallRelevantCount, "USER + CONTENT", resultIndex++, results);
									evaluate(orderedScoresProd, relevantTaggings, overallRelevantCount, "USER * CONTENT", resultIndex++, results);
									evaluate(orderedScoresProdFullOuter, relevantTaggings, overallRelevantCount, "USER * CONTENT Full outer", resultIndex++, results);
								}
							}
							
							
						}
					}
				} finally {
					getRelevantTaggings.close();
					for (GetExpandedTags geuts : expAndPreviouslyUnusedByCategory.values()) {
						geuts.close();
					}
				}*/
				contentTagRecommendation(itv,knownTags);
				System.gc();
				log.debug(Runtime.getRuntime().freeMemory());
			}
		} finally {
			gve.close();
		}
		System.out.println(testCount + " tests on recommendation of first tagusages of users with nonempty relevant data run => avg results:");
		for (EvaluationResults res : results) {
			System.out.println(res);
		}
		System.out.println((unknownNewPostContents + knownNewPostContents) + " tests on recommendation of tags for (user,content)-pairs with nonempty relevant data run => avg results:");
		for (EvaluationResults res : forContentResults) {
			System.out.println(res);
		}
		System.out.println((unknownNewPostContents) + " of these were unknown content => avg results:");
		for (EvaluationResults res : forUnknownContentResults) {
			System.out.println(res);
		}
		System.out.println((knownNewPostContents) + " of these were known content => avg results:");
		for (EvaluationResults res : forKnownContentResults) {
			System.out.println(res);
		}
		return null;
	}
	
	public static class TagScore implements Map.Entry<Integer,Double> {
		private final Integer tagId;
		private Double score;
		
		public TagScore(Integer tagId, Double score) {
			this.tagId = tagId;
			this.score = score;
		}

		public Double getValue() {
			return score;
		}

		public Integer getKey() {
			return tagId;
		}

		public Double setValue(Double score) {
			Double old = this.score;
			this.score = score;
			return old;
		}
	}
	
	private void evaluate(Iterable<? extends Map.Entry<Integer,Double>> found, Map<Integer,Integer> relevant, double overallRelevantCount, String algorithmName, int resultIndex, List<EvaluationResults> resultField, int allExisting) {
		final EvaluationResults res = getInitialisedResult(algorithmName, resultIndex, resultField);
		final EvaluationResults curRes = evaluate(found, relevant, overallRelevantCount, allExisting);
		curRes.interpolate();
		res.addToAvg(curRes);
	}
	
	private EvaluationResults evaluate(Iterable<? extends Map.Entry<Integer,Double>> found, Map<Integer,Integer> relevant, double overallRelevantCount, int allExisting) {
		int foundRelevance = 0;
		int foundRelevantTags = 0;
		int bufLength = 0;
		final EvaluationResults curRes = new EvaluationResults();
		
		for (Map.Entry<Integer,Double> entry : found) {
			if (entry.getKey() == -1) {
				continue; // -1 sind tags, die aus dem Titel extrahiert, aber dem System gänzlich unbekannt sind. Diese werden der nicht mitgerechnet
			}
			++bufLength;
			Integer relevance = relevant.get(entry.getKey());
			if (relevance != null) {
				++foundRelevantTags;
				foundRelevance += relevance;
				if (log.isDebugEnabled() == true) {
					log.debug("foundRelevantTags=" + foundRelevantTags + " bufLength=" + bufLength + " p=" + ((double)foundRelevantTags) / ((double) bufLength) + " foundRelevance=" + foundRelevance +  " overallRelevantCount=" + overallRelevantCount + " r=" + ((double)foundRelevance) / overallRelevantCount);
				}
				curRes.addPoint( ((double)foundRelevantTags) / ((double) bufLength), ((double)foundRelevance) / overallRelevantCount, bufLength);
			}
		}
		curRes.addPoint(0,1, allExisting); // wenn voller recall erreicht wurde, dann ändert dieser Aufruf nichts, da immer die höchste precision im recall-intervall verwendet wird.
		return curRes;
	}
	
	private EvaluationResults getInitialisedResult(String algorithmName, int resultIndex, List<EvaluationResults> resultField) {
		EvaluationResults res;
		if (resultField.size() > resultIndex) {
			res = resultField.get(resultIndex);
		} else {
			res = new EvaluationResults();
			res.algorithmName = algorithmName;
			resultField.add(res);
		}
		return res;
	}
	
	
	private void contentTagRecommendation( InvertedTagVector userItv, Set<Integer> knownTagIds) {
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
	
		GetBookURLHashOfUserAfterDate newlyPostedContents = new GetBookURLHashOfUserAfterDate(userItv.getKey(), trainingEnd, testEnd);
		try {
			runDBOperation(newlyPostedContents);
			for (Map.Entry<String,Integer> hashAndContentId : newlyPostedContents) {
				//GetVectorEntries gve = new GetVectorEntries("1"+hashAndContentId.getKey(), AbstractGetVectorEntries.Category.CONTENT, true);
				GetTaggingsOnContentIdInTimeSpan getRelevant = new GetTaggingsOnContentIdInTimeSpan( hashAndContentId.getValue(), trainingEnd, testEnd, knownTagIds);
				Map<Integer,Integer> relevantTagIds = new HashMap<Integer,Integer>();
				
				try {
					runDBOperation(getRelevant);
					final Integer eins = 1; // ein User kann einen Content nur einmal mit dem selben tag taggen
					for (Integer i : getRelevant) {
						relevantTagIds.put(i,eins);
					}
				} finally {
					getRelevant.close();
				}
				log.debug("relevant tagids for  (" + userItv.getKey() + "," + hashAndContentId.getKey() + "): " + relevantTagIds.toString() );
				if (relevantTagIds.size() == 0) {
					log.warn("no relevant tagids for '"+hashAndContentId.getKey() + "'");
					continue;
				}
				int resultIndex = 0;
				boolean isKnownContent;
				GetVectorEntries gve = new GetVectorEntries("1" + hashAndContentId.getKey(), AbstractGetVectorEntries.Category.CONTENT, true);
				try {
					runDBOperation(gve);
					isKnownContent = gve.hasNext();
				} finally {
					gve.close();
				}
				
				/*for (SimilarityCategory contentExpSim : contentExp) {
					for (SimilarityCategory userExpSim : userExp) {
						String algorithmName = contentExpSim.toString() + "-" + ((userExpSim == null) ? "null" : userExpSim.toString());
						UserContentTagRecommendation recommender = new UserContentTagRecommendation(userItv.getKey(), "1" + hashAndContentId.getKey(), contentExpSim, userExpSim); // TODO: hier wird immer Präfix "1" für bookmark-Content verwendet. Das muss bei einer Umstellung auf Validierung auch von Bibtex-Contents geändert werden
						runDBOperation(recommender);
						UserContentTagRecommendation.Recommendation r = recommender.getReturnValue();
						EvaluationResults singleRes;
						if (r.isKnownContent()) {
							singleRes = getInitialisedResult( algorithmName, resultIndex, forKnownContentResults );
							++knownNewPostContents;
						} else {
							singleRes = getInitialisedResult( algorithmName, resultIndex, forUnknownContentResults );
							++unknownNewPostContents;
						}
						EvaluationResults mixedRes = getInitialisedResult( algorithmName, resultIndex, forContentResults );
						EvaluationResults rRes =  evaluate(r.getOrderedScore(), relevantTagIds, relevantTagIds.size());
						//log.debug("found: " + r.getOrderedScore().toString());
						//log.debug("uninterpolated: " + rRes.toString());
						rRes.interpolate();
						//log.debug("interpolated: " + rRes.toString());
						singleRes.addToAvg(rRes);
						mixedRes.addToAvg(rRes);
						++resultIndex;
					}
				}*/
				if (isKnownContent == true) {
					++knownNewPostContents;
				} else {
					++unknownNewPostContents;
				}
				
				DatabaseQuery<RecommendedTag> recommender = new NeuroUserContentTagRecommendation(userItv.getKey(), "1" + hashAndContentId.getKey(), 1000 );
				try {
					runDBOperation(recommender);
					EvaluationResults singleRes;
					String algorithmName = "-titleTags"; 
					/*if (recommender.hasNext()) {
						++knownNewPostContents;
					} else {
						recommender.close();
						recommender = new GetMostUsedTagsOfUser(userItv.getKey());
						runDBOperation(recommender);
						++unknownNewPostContents;
					}*/
					singleRes = getInitialisedResult( algorithmName, resultIndex, (isKnownContent == true) ? forKnownContentResults : forUnknownContentResults );
					EvaluationResults mixedRes = getInitialisedResult( algorithmName, resultIndex, forContentResults );
					EvaluationResults rRes =  evaluate(recommender, relevantTagIds, relevantTagIds.size(), knownTagIds.size());
					rRes.interpolate();
					singleRes.addToAvg(rRes);
					mixedRes.addToAvg(rRes);
					++resultIndex;
				} finally {
					recommender.close();
				}
				
				GetBookmarkTitle getTitle = new GetBookmarkTitle(hashAndContentId.getValue());
				String title;
				try {
					runDBOperation(getTitle);
					title = getTitle.hasNext() ? getTitle.next() : "";
				} finally {
					getTitle.close();
				}
				log.debug(NeuroUserContentTagRecommendation.buildTagCSVFromString(title));
				recommender = new NeuroUserContentTagRecommendation(userItv.getKey(), "1" + hashAndContentId.getKey(), NeuroUserContentTagRecommendation.buildTagCSVFromString(title), 1000 );
				try {
					runDBOperation(recommender);
					String algorithmName = "+titleTags";
					EvaluationResults singleRes;
					/*if (recommender.hasNext()) {
						//singleRes = getInitialisedResult( algorithmName, resultIndex, forKnownContentResults );
					} else {
						recommender.close();
						//	singleRes = getInitialisedResult( algorithmName, resultIndex, forUnknownContentResults );
						recommender = new GetMostUsedTagsOfUser(userItv.getKey());
						runDBOperation(recommender);
					}*/
					singleRes = getInitialisedResult( algorithmName, resultIndex, (isKnownContent == true) ? forKnownContentResults : forUnknownContentResults );
					EvaluationResults mixedRes = getInitialisedResult( algorithmName, resultIndex, forContentResults );
					EvaluationResults rRes =  evaluate(recommender, relevantTagIds, relevantTagIds.size(), knownTagIds.size());
					rRes.interpolate();
					singleRes.addToAvg(rRes);
					mixedRes.addToAvg(rRes);
					++resultIndex;
				} finally {
					recommender.close();
				}
				
				String algorithmName = "Frontend";
				Collection<RecommendedTag> recs = RecommenderFrontEnd.getRecommendation(userItv.getKey(), hashAndContentId.getKey(), Bookmark.class, title, 1000);
				EvaluationResults singleRes = getInitialisedResult( algorithmName, resultIndex, (isKnownContent == true) ? forKnownContentResults : forUnknownContentResults );
				EvaluationResults mixedRes = getInitialisedResult( algorithmName, resultIndex, forContentResults );
				EvaluationResults rRes =  evaluate( recs, relevantTagIds, relevantTagIds.size(), knownTagIds.size());
				rRes.interpolate();
				singleRes.addToAvg(rRes);
				mixedRes.addToAvg(rRes);
				++resultIndex;
				
				
				relevantTagIds.clear();
				relevantTagIds = null;
			}
		} finally {
			newlyPostedContents.close();
			
		}
	}
	
	private static final SimilarityCategory[] contentExp = {/*SimilarityCategory.USER,*/ SimilarityCategory.CONTENT/*, SimilarityCategory.COMBIVECTOROVERALL*/};
	private static final SimilarityCategory[] userExp = {SimilarityCategory.USER/*, SimilarityCategory.CONTENT, SimilarityCategory.COMBIVECTOROVERALL*/, null};
}
