/**
 * on load
 */
$(function () {
    initDetailsEditing();
    initNameEditing();
    initUserLinking();

    // ORCID formatter
    $("#editOrcid").mask("9999-9999-9999-999*");

    // Researcher ID formatter - disabled due to different formats
    //$("#editResearcherId").mask("\a-9999-9999");
});

function initDetailsEditing() {
    initResetableInput();
    initResetButtons();

    $('#submitEditPersonDetails').click(function (e) {
        e.preventDefault();

        var btn = $(this);
        if (validateSubmit()) {
            return;
        }

        var formData = $('#formEditPersonDetails').serializeArray();
        formData.push({name: 'updateOperation', value: 'UPDATE_DETAILS'});
        formData.push({name: 'personId', value: getPersonId()});
        formData.push({name: 'claimedPerson', value: getClaimedPerson()});

        $.ajax({
            type: 'POST',
            async: false,
            url: '/editPerson',
            data: formData,
            beforeSend: function (data) {
                setButtonLoading(btn);
            },
            complete: function (data) {
                if (data.success) {
                    // request success
                    location.reload();
                } else {
                    // error during update
                    showErrorAlert(data.message);
                    $('#editPersonDetails').modal('hide');
                    unsetButtonLoading(btn);
                }
            }
        });
    });
}

function validateSubmit() {
    var errorInSubmit = false;

    // validate URL
    var homepage = $('#editHomepage');
    if (!isValidURL(homepage.val())) {
        homepage.css("border-color", "red");
        errorInSubmit = true;
    }

    // validate E-Mail
    var email = $('#editEmail');
    if (!isValidEMail(email.val())) {
        email.css("border-color", "red");
        errorInSubmit = true;
    }

    return errorInSubmit;
}

function initResetableInput() {
    $('#formEditPersonDetails input').change(function () {
        var id = $(this).attr('id');
        var resetLink = $('.reset-link[data-reset="' + id + '"]');
        if ($(this).val() !== $(this).data('reset')) {
            $(resetLink).show(0);
        } else {
            $(resetLink).hide(0);
        }
    });
}

function initResetButtons() {
    $('.reset-link').click(function () {
        var resetId = $(this).data('reset');
        var input = $('#' + resetId);
        $(input).val($(input).data('reset'));
        $(this).hide(0);
    })
}

/**
 * Validates a given url string
 * @param url
 * @returns    true if the given url is valid or empty, false otherwise
 */
function isValidURL(url) {
    if (!url) {
        return true;
    }
    return /^(https?|s?ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(url);
}

/**
 * Validates a given email string
 * @param mail
 * @returns true if the given mail is valid or empty, false otherwise
 */
function isValidEMail(mail) {
    if (!mail) {
        return true;
    }

    var pattuser = /^([A-Z0-9_%+\-!#$&'*\/=?^`{|}~]+\.?)*[A-Z0-9_%+\-!#$&'*\/=?^`{|}~]+$/i;
    var pattdomain = /^([A-Z0-9-]+\.?)*[A-Z0-9-]+(\.[A-Z]{2,9})+$/i;

    var tab = mail.split("@");
    if (tab.length !== 2) {
        return false;
    }

    return (pattuser.test(tab[0]) && pattdomain.test(tab[1]));
}

function initNameEditing() {
    // add a new name to the alternative names list
    $("#submitEditPersonNames").click(function (e) {
        e.preventDefault();

        var btn = $(this);
        var lastNameInput = $('#addLastName');
        if (!lastNameInput.val()) {
            lastNameInput.css('border-color', 'red');
            return;
        }

        var formData = $("#formEditPersonNames").serializeArray();
        formData.push({name: "updateOperation", value: 'ADD_NAME'});
        formData.push({name: "personId", value: getPersonId()});

        $.ajax({
            type: 'POST',
            async: false,
            url: '/editPerson',
            data: formData,
            beforeSend: function (data) {
                setButtonLoading(btn);
            },
            complete: function (data) {
                if (data.success) {
                    // success
                    location.reload();
                } else {
                    // error
                    showErrorAlert(data.error);
                    $('#editPersonNames').modal('hide');
                    unsetButtonLoading(btn);
                }
            }
        });
    });

    // Select alternative names as main
    $('.btn-select-name').click(function () {
        var btn = $(this);
        $.ajax({
            type: 'POST',
            async: false,
            url: '/editPerson',
            data: {
                updateOperation: "SELECT_MAIN_NAME",
                personId: getPersonId(),
                "personName.firstName": btn.data('first-name'),
                "personName.lastName": btn.data('last-name'),
            },
            beforeSend: function (data) {
                setButtonLoading(btn);
            },
            complete: function (data) {
                if (data.success) {
                    // success
                    location.reload();
                } else {
                    // error
                    showErrorAlert(data.error);
                    $('#editPersonNames').modal('hide');
                    unsetButtonLoading(btn);
                }
            }
        });
    });

    // Remove alternative names
    $('.btn-delete-name').click(function () {
        var btn = $(this);
        $.ajax({
            type: 'POST',
            async: false,
            url: '/editPerson',
            data: {
                updateOperation: 'DELETE_NAME',
                personId: getPersonId(),
                "personName.firstName": btn.data('first-name'),
                "personName.lastName": btn.data('last-name'),
            },
            beforeSend: function (data) {
                setButtonLoading(btn);
            },
            complete: function (data) {
                if (data.success) {
                    // success
                    location.reload();
                } else {
                    // error
                    showErrorAlert(data.error);
                    $("#editPersonNames").modal('hide');
                    unsetButtonLoading(btn);
                }
            }
        });
    });
}

function initUserLinking() {
    $("#btnLinkSubmit").click(function () {
        var btn = $(this);

        $.ajax({
            type: 'POST',
            async: false,
            url: '/editPerson',
            data: {
                updateOperation: 'LINK_USER',
                personId: getPersonId()
            },
            beforeSend: function (data) {
                setButtonLoading(btn);
            },
            complete: function (data) {
                if (data.success) {
                    // success
                    location.reload();
                } else {
                    // error
                    showErrorAlert(data.error);
                    $("#linkPerson").modal('hide');
                    unsetButtonLoading(btn);
                }
            }
        });
    });

    $("#btnUnlinkSubmit").click(function () {
        var btn = $(this);

        $.ajax({
            type: 'POST',
            async: false,
            url: '/editPerson',
            data: {
                updateOperation: 'UNLINK_USER',
                personId: getPersonId()
            },
            beforeSend: function (data) {
                setButtonLoading(btn);
            },
            complete: function (data) {
                if (data.success) {
                    // success
                    location.reload();
                } else {
                    // error
                    showErrorAlert(data.error);
                    $("#unlinkPerson").modal('hide');
                    unsetButtonLoading(btn);
                }
            }
        });
    });
}

function getPersonId() {
    return $('.person-info').data('person');
}

function getClaimedPerson() {
    return $('.person-info').data('claimed-person');
}

function setButtonLoading(button) {
    var text = $(button).text();
    $(button).html(text + " <i class='fa fa-spinner fa-spin'></i>");
    $(button).addClass('disabled');
}

function unsetButtonLoading(button) {
    var text = $(button).text();
    text.replace("<i class='fa fa-spinner fa-spin'></i>", "");
    $(button).html(text);
    $(button).removeClass('disabled');
}

function showErrorAlert(messageKey) {
    var alert = $('<div class="alert alert-danger alert-dismissable"></div>');
    var button = $('<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>');
    alert.append(button);
    alert.append($('<strong>Error: </strong>'));
    alert.append(getString(messageKey));

    $('#ajaxErrorAlerts').append(alert);
}