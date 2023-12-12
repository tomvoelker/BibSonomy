var OA_STATUS_URL = "/ajax/openaccess/status";
var OA_CLASSIFY_PUB_URL = "/ajax/openaccess/classifyPublication";
var SWORD_SERVICE_URL = "/ajax/openaccess/swordService";

// Actions
var DISSEMIN = "DISSEMIN";
var GET_AVAILABLE_CLASSIFICATIONS = "AVAILABLE_CLASSIFICATIONS";
var GET_POST_CLASSIFICATION_LIST = "GET_POST_CLASSIFICATION_LIST";
var GET_CLASSIFICATION_DESCRIPTION = "GET_CLASSIFICATION_DESCRIPTION";
var SAVE_CLASSIFICATION_ITEM = "SAVE_CLASSIFICATION_ITEM";
var REMOVE_CLASSIFICATION_ITEM = "REMOVE_CLASSIFICATION_ITEM";
var GET_SENT_REPOSITORIES = "GET_SENT_REPOSITORIES";

/**
 * on load
 */
$(function () {
    var submitButton = $('#submitRepositoryBtn');
    var agreement = $('#openAccessAgreement');

    // Uncheck author contract agreement on load and disable submit button
    agreement.prop('checked', false);
    submitButton.prop( "disabled", true);

    // Enable submission, if checked
    agreement.click(function () {
        if ($(this).is(':checked')) {
            submitButton.prop( "disabled", false);
        } else {
            submitButton.prop( "disabled", true);
        }
    });

    updateStatusOA();
});

function updateStatusOA() {
    var intrahash = $('.post-openaccess').data('intrahash');

    $.ajax({
        url: OA_STATUS_URL,
        data: {
            // These are the variables you can pass to the request
            'action': DISSEMIN,
            'intrahash': intrahash
        },
        success: function (data) {
            $('#oaPostStatus').html(data);
        },
        beforeSend: function(){
            $("#oaPostStatusLoader").show(0);
        },
        complete: function(){
            $("#oaPostStatusLoader").hide(0);
        }
    });
}

function sendToRepository() {
    if (!document.getElementById('openAccessAgreement').checked) {
        return;
    }

    var formData = $('#formSubmitRepository').serializeArray();
    $.ajax({
        url: SWORD_SERVICE_URL,
        data: formData,
        success: function (data) {

            // response has the following format:
            // {"response":{"message":"error.sword.noPDFattached","localizedMessage":"Keine PDF-Datei zum übermitteln gefunden","statuscode":0}}
            // status code can be 0 (error/warning) or 1 (success)

            // check and show response to user
            $.each(data, function (i, response) {
                if (null == data || null == data.response) {
                    showAjaxAlert("danger", "unknown response error");
                } else {
                    if (data.response.statuscode === 0) {
                        showAjaxAlert("danger", data.response.localizedMessage);
                    } else {
                        showAjaxAlert("success", data.response.localizedMessage);
                    }
                }
            });
        },
        error: function (req, status, e) {
            showAjaxAlert("danger", "Unable to send data to repository: " + status);
        },
        beforeSend: function () {
            $("#submitRepositoryLoader").show(0);
        },
        complete: function() {
            $("#submitRepositoryLoader").hide(0);
        }
    });
}
