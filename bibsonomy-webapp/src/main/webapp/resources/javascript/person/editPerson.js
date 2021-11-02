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
        var personInfo = $('.person-info')
        formData.push({name: 'editAction', value: 'update'});
        formData.push({name: 'updateOperation', value: 'UPDATE_ALL'});
        formData.push({name: 'personId', value: personInfo.data('person')});
        formData.push({name: 'claimedPerson', value: personInfo.data('claimed-person')});

        $.ajax({
            type: "POST",
            url: '/person/edit',
            data: formData,
            complete: function (data) {
                // error handling
                if (data.status) {
                    // location.reload();
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
    $("#btnAddNameSubmit").click(function () {
        var form_data = $("#addNameForm").serializeArray();
        form_data.push({name: "editAction", value: "addName"});

        $.post("/person/edit", form_data).done(function (data) {
            // error handling
            if (data.status) {
                // everything is fine

                // no alternative names so far, delete the placholder
                if ($("#personPageAlternativeNameList").hasClass("hidden")) {
                    $("#personPageAlternativeNamePlaceholder").remove();
                    $("#personPageAlternativeNameList").removeClass("hidden");
                }

                // add the name to the list (includes the delete button)
                $("#personPageAlternativeNameList").append('<li id="personPageAlternativeNameID_' + data.personNameChangeId + '">' + ''
                    + $("#formFirstName").val() + ' ' + $("#formLastName").val() + ' '
                    + '<span '
                    + 'data-person-name-id="' + data.personNameChangeId + '" '
                    + 'data-firstName="' + $("#formFirstName").val() + '" '
                    + 'data-lastName="' + $("#formLastName").val() + '" '
                    + 'data-toggle="modal" '
                    + 'data-target="#removeName" '
                    + 'id="removeName_' + data.personNameChangeId + '" '
                    + 'class="removeName fa fa-remove"> '
                    + '</span>'
                    + '</li>'
                );

                // hide the modal and reset the form fields
                $("#addName").modal("hide");
                $("#formFirstName").val("");
                $("#formLastName").val("");

                // register the onclick function for the new added button:-
                $("#removeName_" + data.personNameChangeId).on("click", function () {
                    var e = $(this);
                    $("#removeNameForm input[name=formPersonNameId]").val(e.attr("data-person-name-id"));
                    $("#modalRemoveNameText").html(e.attr("data-firstName") + " " + e.attr("data-lastName"));
                    $("#removeName").modal("hide");
                });

            } else {
                // error during update
                if (data.message !== "") {
                    // display the error somewhere
                    $("#personPageAjaxError").text(data.message).show();
                } else {
                    $("#personPageAjaxError").show();
                    $("#personPageAjaxErrorDefaultMessage").show();
                }
            }
        });
    });

    // inserts the values into the modal (TODO: check if data can be taken from fields)
    $(".removeName").click(function (e) {
        $("#removeNameForm input[name=formPersonNameId]").val(e.attr("data-person-name-id"));
        $("#modalRemoveNameText").html(e.attr("data-firstName") + " " + e.attr("data-lastName"));
    });

    // submit the remove alternative name modal
    $("#btnRemoveNameSubmit").click(function () {
        $.ajax({
            type: "POST",
            url: '/person/edit',
            data: {
                editAction: "deleteName",
                formPersonNameId: $("#formPersonNameId").val()
            },
            complete: function (data) {
                // hide the modal
                var id = $("#formPersonNameId").val();
                $("#formPersonNameId").val("");
                $("#removeName").modal("hide");

                // error handling
                if (data.status) {
                    // everything is fine
                    // remove the name from the list
                    $("#personPageAlternativeNameID_" + id).remove();
                } else {
                    // error during update
                    if (data.message !== "") {
                        // display the error somewhere
                        $("#personPageAjaxError").text(data.message).show();
                    } else {
                        $("#personPageAjaxError").show();
                        $("personPageAjaxErrorDefaultMessage").show();
                    }
                }
            }
        });
    });

    // submit the new main name form
    $("#btnSetMainNameSubmit").click(function () {
        var form_data = $("#setMainNameForm").serializeArray();
        form_data.push({name: "editAction", value: "setMainName"});

        $.ajax({
            type: "POST",
            url: '/person/edit',
            data: form_data,
            complete: function (data) {
                // error handling
                if (data.status) {
                    // everything is fine - reload to render the page again
                    location.reload();
                } else {
                    // error during update
                    if (data.message !== "") {
                        // display the error somewhere
                        $("#personPageAjaxError").text(data.message).show();
                    } else {
                        $("#personPageAjaxError").show();
                        $("#personPageAjaxErrorDefaultMessage").show();
                    }
                }
            }
        });
    });

    // inserts the values into the modal
    $(".personPageAlternativeName").click(function () {
        var e = $(this);
        $("#setMainNameForm input[name=formSelectedName]").val(e.attr("data-person-name-id"));
        $("#modalMainNameText").html(e.attr("data-firstName") + " " + e.attr("data-lastName"));
    });
}