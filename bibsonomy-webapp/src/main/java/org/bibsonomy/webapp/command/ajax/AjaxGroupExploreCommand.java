package org.bibsonomy.webapp.command.ajax;

import org.bibsonomy.webapp.command.GroupExploreViewCommand;

public class AjaxGroupExploreCommand extends GroupExploreViewCommand {

    private int page;
    private int pageSize;

    private boolean distinctCount;
    private String responseString;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isDistinctCount() {
        return distinctCount;
    }

    public void setDistinctCount(boolean distinctCount) {
        this.distinctCount = distinctCount;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }
}
