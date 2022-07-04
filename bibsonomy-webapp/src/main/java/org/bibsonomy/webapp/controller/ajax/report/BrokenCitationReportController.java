package org.bibsonomy.webapp.controller.ajax.report;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.ajax.ReportCommand;
import org.bibsonomy.webapp.util.View;

public class BrokenCitationReportController extends AbstractReportController {

    private final static String SUBJECT_KEY = "report.error.brokenCitation.mail.subject";
    private final static String BODY_KEY = "report.error.brokenCitation.mail.body";

    @Override
    public View workOn(ReportCommand command) {
        final User loggedInUser = this.requestLogic.getLoginUser();

        // Check, if spammer
        if (loggedInUser.isSpammer()) {
            return this.error(command, ERROR_KEY);
        }

        // Set parameters for the messages
        String styleName = command.getStyleName();
        String referer = command.getReferer();

        Object[] subjectParameters = {styleName};
        Object[] bodyParameters = {styleName, referer, loggedInUser.getName()};

        // Send e-mail
        boolean result = report(SUBJECT_KEY, BODY_KEY, subjectParameters, bodyParameters);

        if (result) {
            return this.success(command, SUCCESS_KEY);
        } else {
            return this.error(command, ERROR_KEY);
        }
    }

}
