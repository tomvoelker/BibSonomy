package org.bibsonomy.webapp.command.ajax;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportCommand extends AjaxCommand {

    // fields for broken citation
    private String styleName;

    // fields for duplicates on person page
    private String personId;
    private String title;
    private String interhash;
    private String intrahash;
    private String message;

    // field for the reporter
    private String referer;
}
