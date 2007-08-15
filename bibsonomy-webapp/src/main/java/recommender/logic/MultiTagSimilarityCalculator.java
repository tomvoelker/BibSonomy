/*
 * Created on 19.02.2006
 */
package recommender.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import recommender.model.CombinableTagSimilarity;
import recommender.model.SimpleCosinusTagSimilarity;
import recommender.model.TagVector;

public class MultiTagSimilarityCalculator {
	private static final Logger log = Logger.getLogger(MultiTagSimilarityCalculator.class);
	private final Map<String,Collection<ScalarProductComponent>> scalarProdComponentsOfCalculationTags = new HashMap<String,Collection<ScalarProductComponent>>();
	private final Map<Integer,CalculationTagInfo> calculationTagInfos = new HashMap<Integer,CalculationTagInfo>();
	private Integer curSimilarTagVectorId = null;
	private long curSimilarTagQuadSum = 0l;
	
	
	/** markiert einen Wechsel des Tags aus dem kompletten Stream, zu dem gerade die Ähnlichkeiten ausgehend von den tags, die mit addCalculationTagVector eingefügt wurden, berechnet werden sollen */
	public void resetForNextCalculations( Integer nextSimilarTagVectorId ) {
		this.curSimilarTagVectorId = nextSimilarTagVectorId;
		this.curSimilarTagQuadSum = 0l;
		for (CalculationTagInfo calcInfo : calculationTagInfos.values()) {
			if (calcInfo != null) {
				calcInfo.resetForNextTagComparison();
			}
		}
	}
	
	public Iterator<Integer> tagIdIterator() {
		return calculationTagInfos.keySet().iterator();
	}
	
	/** fügt Kompenenten der TagVektoren von Tags ein, zu denen Schritt für Schritt eine Ähnlichkeit zu allen per addToCalculation eingefügten TagVektoren aufgebaut werden soll */
	public void addCalculationTagVectorEntry(TagVector.Entry tve) {
		CalculationTagInfo calcInfo = calculationTagInfos.get( tve.getTagId() );
		if (calcInfo == null) {
			calcInfo =  new CalculationTagInfo();
			calculationTagInfos.put( tve.getTagId(), calcInfo );
		}
		Collection<ScalarProductComponent> scalarProdComponentsOfCalculationTagsForKey = scalarProdComponentsOfCalculationTags.get( tve.getKey() );
		if (scalarProdComponentsOfCalculationTagsForKey == null) {
			scalarProdComponentsOfCalculationTagsForKey = new ArrayList<ScalarProductComponent>();
			scalarProdComponentsOfCalculationTags.put( tve.getKey(), scalarProdComponentsOfCalculationTagsForKey );
		}
		scalarProdComponentsOfCalculationTagsForKey.add( new ScalarProductComponent( tve.getValue(), calcInfo ) );
	}
	
	/** fügt Komponenten des mit resetForNextCaculations angegebenen Tags in die Berechnung ein */
	public void addToCalculation(TagVector.Entry tve) {
		final int val = tve.getValue();
		Collection<ScalarProductComponent> scalarProdComponentsOfCalculationTagsForKey = scalarProdComponentsOfCalculationTags.get( tve.getKey() );
		if (scalarProdComponentsOfCalculationTagsForKey != null) {
			for (ScalarProductComponent component : scalarProdComponentsOfCalculationTagsForKey) {
				component.add( val );
			}
		}
		curSimilarTagQuadSum += val * val;
	}
	
	public CombinableTagSimilarity getSimilarity(Integer tagId) {
		if (curSimilarTagVectorId == null) {
			log.error("curSimilarTagVectorId == null");
			throw new IllegalStateException("curSimilarTagVectorId == null");
		}
		final CalculationTagInfo calcTagInfo = calculationTagInfos.get(tagId);
		if (calcTagInfo == null) {
			log.fatal("calcTagInfo == null. => Tag " + tagId + " bei Content verwendet aber von keinem User oder andersherum. Reagiere mit Ähnlichkeit 0");
			log.fatal("nur vorhanden: " + calculationTagInfos.keySet().toString());
			//return new SimpleSymTunedImplicationProbability(curSimilarTagVectorId, tagId, 0d, 1, 1, 0, 0);
			return new SimpleCosinusTagSimilarity(curSimilarTagVectorId, tagId, calcTagInfo.quadSum, curSimilarTagQuadSum, 0);
		}
		return new SimpleCosinusTagSimilarity(curSimilarTagVectorId, tagId, calcTagInfo.quadSum, curSimilarTagQuadSum, calcTagInfo.scalarProdToCurStreamTag);
		//return new SimpleImplicationProbability( curSimilarTagVectorId, tagId, TagSimilarityCalculator.calculateImplicationProbability( calcTagInfo.quadSum, calcTagInfo.scalarProdToCurStreamTag), calcTagInfo.quadSum ,calcTagInfo.scalarProdToCurStreamTag);
		//return new SimpleSymTunedImplicationProbability( curSimilarTagVectorId, tagId, calcTagInfo.quadSum , curSimilarTagQuadSum, calcTagInfo.scalarProdToCurStreamTag, calcTagInfo.inverseScalarProdToCurStreamTag);
	}
	
	protected static class ScalarProductComponent {
		private final int multiplier;
		private final CalculationTagInfo calcTagInfo;
		
		public ScalarProductComponent(int multiplier, final CalculationTagInfo calcTagInfo) {
			this.multiplier = multiplier;
			this.calcTagInfo = calcTagInfo;
			calcTagInfo.quadSum += multiplier * multiplier;
		}
		
		public void add(int count) {
			calcTagInfo.scalarProdToCurStreamTag += multiplier * count;
			//calcTagInfo.scalarProdToCurStreamTag += multiplier * ((count < multiplier) ? count : multiplier);
			//calcTagInfo.inverseScalarProdToCurStreamTag += count * ((multiplier < count) ? multiplier : count);
			//calcTagInfo.commonContentQuadSumOfCurStreamTag += count * count;
			//++calcTagInfo.commonContents;
		}
	}
	
	/** Struktur der Informationen, welche der Algorithmus zur ÄhnlichstenListenbestimmung für ein Tag benötigt */
	protected static class CalculationTagInfo {
		private long scalarProdToCurStreamTag = 0l;
		//private long inverseScalarProdToCurStreamTag = 0l;
		private long quadSum = 0l;
		//private long commonContentQuadSumOfCurStreamTag = 0l;
		//private int commonContents = 0;
				
		public void resetForNextTagComparison() {
			scalarProdToCurStreamTag = 0l;
			//inverseScalarProdToCurStreamTag = 0l;
			//commonContentQuadSumOfCurStreamTag = 0l;
			//commonContents = 0;
		}
	}
}
