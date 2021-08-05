package org.bibsonomy.model.statistics;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StatisticsAggregation {

    Map<String, Statistics> aggregation;

    public StatisticsAggregation() {
        this.aggregation = new HashMap<>();
    }

    public Set<String> getListOfFieldKeys() {
        return this.aggregation.keySet();
    }

    public Statistics getStatisticsByFieldKey(final String fieldKey) {
        return aggregation.get(fieldKey);
    }

    public Map<String, Statistics> getAggregation() {
        return aggregation;
    }

    public void setAggregation(Map<String, Statistics> aggregation) {
        this.aggregation = aggregation;
    }
}
