/**
 * on load
 */
$(function () {
    initSubmit();
    initResetableInput();
    initResetButtons();
    initNameEditing();
    initMainNameSelection();

    // ORCID formatter
    $("#editOrcid").mask("9999-9999-9999-9999");

    // Researcher ID formatter
    $("#editResearcherId").mask("\a-9999-9999");
});

function initSubmit() {
    $('#submitEditPersonDetails').click(function (e) {
        e.preventDefault();

        if (validateSubmit()) {
            console.log('error in submit');
            return;
        }

        var formData = $('#formEditPersonDetails').serializeArray();
        formData.push({name: 'updateOperation', value: 'UPDATE_ALL'});
        formData.push({name: 'personId', value:getPersonId()});
        formData.push({name: 'claimedPerson', value: getClaimedPerson()});

        $.ajax({
            type: 'POST',
            url: '/editPerson',
            data: formData,
            complete: function (data) {
                if (data.status) {
                    // request success
                    // location.reload()
                } else {
                    // error during update
                    console.log(data.message);
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

function initMainNameSelection() {
    $('.check-main').change(function () {
        $(this).siblings().prop('checked', false);
    });
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
    $("#submitEditPersonNames").click(function () {
        var e = $(this);

        var formData = $("#formEditPersonNames").serializeArray();
        formData.push({name: 'updateOperation', value: 'ADD_NAME'});
        formData.push({name: "personId", value: getPersonId()});

        $.ajax({
            type: 'POST',
            url: '/editPerson',
            data: formData,
            complete: function (data) {
                if (data.status) {
                    // request success
                    // location.reload()
                } else {
                    // error during update
                    console.log(data.message);
                }
            }
        });
    });

    // Select alternative names as main
    $('.btn-select-name').click(function () {
        var e = $(this);
        var personNameId = e.data('name-id');
        $.ajax({
            type: 'POST',
            url: '/editPerson',
            data: {
                updateOperation: "SELECT_MAIN_NAME",
                personId: getPersonId(),
                personNameId: personNameId
            },
            complete: function (data) {
                // error handling
                if (data.status) {
                    // location.reload()
                } else {
                    // error during update
                    console.log(data.message);
                }
            }
        });
    });

    // Remove alternative names
    $('.btn-delete-name').click(function () {
        var e = $(this);
        var personNameId = e.data('name-id');
        $.ajax({
            type: 'POST',
            url: '/editPerson',
            data: {
                updateOperation: 'DELETE_NAME',
                personId: getPersonId(),
                personNameId: personNameId
            },
            complete: function (data) {
                if (data.status) {
                    // request success
                    // location.reload()
                } else {
                    // error during update
                    console.log(data.message);
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