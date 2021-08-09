package org.bibsonomy.model.extra;

public class SearchFilterElement implements Comparable<SearchFilterElement> {

    private String name;
    private String field;
    private String filter;
    private String messageKey;

    private long count;

    public SearchFilterElement(final String name, final long count) {
        this.name = name;
        this.count = count;
    }

    @Override
    public int compareTo(SearchFilterElement other) {
        int nameCompare = this.getName().compareTo(other.getName());
        if (nameCompare == 0) {
            return Long.compare(this.getCount(), other.getCount());
        }
        return nameCompare;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
