/**
 * on load
 */
$(function () {
    initSubmit();
    initResetableInput();
    initResetButtons();
    initMainNameSelection();

    // ORCID formatter
    $("#editOrcid").mask("9999-9999-9999-9999");

    // Researcher ID formatter
    $("#editResearcherId").mask("\a-9999-9999");
});

function initSubmit() {
    $('#submitEditPersonDetails').click(function () {

    });
}

function initResetableInput() {
    $('#formEditPersonDetails input').change(function () {
        var id = $(this).attr('id');
        var resetLink = $('.reset-link[data-reset="' + id +'"]');
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

    pattuser = /^([A-Z0-9_%+\-!#$&'*\/=?^`{|}~]+\.?)*[A-Z0-9_%+\-!#$&'*\/=?^`{|}~]+$/i;
    pattdomain = /^([A-Z0-9-]+\.?)*[A-Z0-9-]+(\.[A-Z]{2,9})+$/i;

    tab = mail.split("@");
    if (tab.length != 2) {
        return false;
    }

    return (pattuser.test(tab[0]) && pattdomain.test(tab[1]));
}