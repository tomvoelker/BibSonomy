package org.bibsonomy.webapp.controller.ajax.report;

import org.bibsonomy.webapp.command.ajax.ReportCommand;
import org.bibsonomy.webapp.util.View;

public class BrokenCitationReportController extends AbstractReportController {

    private final static String SUBJECT_KEY = "report.error.brokenCitation.mail.subject";
    private final static String BODY_KEY = "report.error.brokenCitation.mail.body";

    @Override
    public View workOn(ReportCommand command) {
        String styleName = command.getStyleName();
        String referer = command.getReferer();
        String reporter = this.requestLogic.getLoginUser().getName();

        Object[] subjectParameters = {styleName};
        Object[] bodyParameters = {styleName, referer, reporter};

        boolean result = report(SUBJECT_KEY, BODY_KEY, subjectParameters, bodyParameters);

        if (result) {
            return this.success(command, SUCCESS_KEY);
        } else {
            return this.error(command, ERROR_Key);
        }
    }

}
