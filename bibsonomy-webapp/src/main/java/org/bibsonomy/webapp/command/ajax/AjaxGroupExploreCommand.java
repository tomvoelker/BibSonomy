package org.bibsonomy.webapp.command.ajax;

import org.bibsonomy.webapp.command.GroupExploreViewCommand;

public class AjaxGroupExploreCommand extends GroupExploreViewCommand {

    private int page;
    private int size;
    private int pageSize;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
