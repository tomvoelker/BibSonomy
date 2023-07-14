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
    // Uncheck author contract agreement on load
    $('#authorAgreement').prop('checked', false);

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
    if (!document.getElementById('authorAgreement').checked) {
        return;
    }

    var formData = $('#formSubmitRepository').serializeArray();
    console.log(formData);
    $.ajax({
        url: SWORD_SERVICE_URL,
        data: formData,
        success: function (data) {

            // response has following format:
            // {"response":{"message":"error.sword.noPDFattached","localizedMessage":"Keine PDF-Datei zum übermitteln gefunden","statuscode":0}}
            // statuscode can be 0 (error/warning) or 1 (success)

            // check and show response to user
            /*
            $.each(data, function (i, response) {
                if (null == data || null == data.response) {
                    showAjaxAlert("error", "unknown response error");
                } else {
                    // create text node behind transmit button, if not exists, to show response text in it
                    // confirmations and warnings get different css-classes
                    var s = createNode({
                        tag: 'div',
                        parent: null,
                        child: null,
                        childNodes: null,
                        parentID: null,
                        id: 'swordresponse',
                        className: "ajaxresponse" + data.response.statuscode
                    });

                    s.appendChild(document.createTextNode(data.response.localizedMessage));
                    $('#pumaSword').append(s);

                    swordResponseStatusCode = data.response.statuscode;

                    // on error enable button
                    if (data.response.statuscode === 0) {
                        $(elementId).removeClass("oadisabledsend2repositorybutton");
                        document.getElementById(elementId).disabled = false;
                    }

                    // show response text

                }
            });
             */
        },
        error: function (req, status, e) {
            showAjaxAlert("error", "Unable to send data to repository: " + status);
        },
        beforeSend: function () {
            $("#submitRepositoryLoader").show(0);
        },
        complete: function() {
            $("#submitRepositoryLoader").hide(0);
        }
    });
}

function cancelSubmitToRepository() {

}
