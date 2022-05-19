package org.bibsonomy.webapp.command.ajax;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApproveGoldStandardAjaxCommand extends AjaxCommand<String> {

    // copy from this username
    private String copyFrom;

    // hash to use, when copying post first and then edit
    private String intrahash;

    // hash to use, when editing existing gold standard
    private String interhash;

}
