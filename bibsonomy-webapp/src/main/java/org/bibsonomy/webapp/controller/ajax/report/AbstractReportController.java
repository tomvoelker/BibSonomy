package org.bibsonomy.webapp.controller.ajax.report;

import java.util.Locale;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.util.MailUtils;
import org.bibsonomy.webapp.command.ajax.ReportCommand;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.json.simple.JSONObject;

@Getter
@Setter
public abstract class AbstractReportController extends AjaxController implements MinimalisticController<ReportCommand> {

    protected final static String SUCCESS_KEY = "report.error.feedback.success";
    protected final static String ERROR_KEY = "report.error.feedback.error";

    protected MailUtils mailUtils;
    protected URLGenerator urlGenerator;

    @Override
    public abstract View workOn(ReportCommand command);

    protected boolean report(final String subjectKey, final String bodyKey, final Object[] subjectParameters, final Object[] bodyParameters) {
        final Locale locale = this.requestLogic.getLocale();
        return mailUtils.sendReportMail(subjectKey, bodyKey, subjectParameters, bodyParameters, locale);
    }

    protected View success(final ReportCommand command, final String successMsg) {
        final JSONObject response = new JSONObject();
        response.put("success", true);
        response.put("message", successMsg);

        command.setResponseString(response.toString());
        return Views.AJAX_JSON;
    }

    protected View error(final ReportCommand command, final String errorMsg) {
        final JSONObject response = new JSONObject();
        response.put("success", false);
        response.put("error", errorMsg);

        command.setResponseString(response.toString());
        return Views.AJAX_JSON;
    }


    @Override
    public ReportCommand instantiateCommand() {
        return new ReportCommand();
    }

}
