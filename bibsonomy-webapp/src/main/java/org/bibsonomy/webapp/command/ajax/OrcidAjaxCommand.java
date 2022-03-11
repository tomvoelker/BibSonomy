package org.bibsonomy.webapp.command.ajax;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.webapp.command.BaseCommand;

@Getter
@Setter
public class OrcidAjaxCommand extends BaseCommand {

    /**
     * ORCID IDs
     */
    private String orcidId;
    private String workId;
    private List<String> workIds;

    /**
     * JSON response string
     */
    private String responseString;
}
