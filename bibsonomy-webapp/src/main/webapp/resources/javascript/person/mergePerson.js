/**
 * on load
 */
$(function () {
    initMergeButton();
    initConflictButton();
    initConflictMergeButton();

    $('#moreMatchReasonsToggler').click(function () {
        $(this).text($(this).text() === 'less' ? 'more' : 'less');
    });
});

function initMergeButton() {
    $('.mergeButton').click(function () {
        var form_data = {
            formAction: "merge",
            formMatchId: $(this).attr("match-id"),
            updateOperation: $(this).attr("data-operation")
        };

        $.post("/person/edit", form_data).done(function (data) {
            if (data.status) {
                $("#match_" + form_data.formMatchId).slideUp(500, function () {
                    $(this).remove();
                });
                location.reload();
            }
        });
    });
}

function initConflictButton() {
    $('.mergeConflictButton').click(function () {
        var form_data = {};
        $.each($("#conflictInputForm").serializeArray(), function (i, field) {
            if (field.name == 'person.mainName') {
                var mainName = field.value;

                var names = mainName.split(", ");
                form_data["newName.firstName"] = names[1];
                form_data["newName.lastName"] = names[0];
            } else {
                form_data[field.name] = field.value;
            }
        });

        form_data["formMatchId"] = $("#conflictModalAccept").attr("match-id");
        form_data["formAction"] = "conflictMerge";

        $.post("/person/edit", form_data).done(function (data) {
            if (data.status) {
                $("#match_" + form_data.formMatchId).slideUp(500, function () {
                    $(this).remove();
                });
                location.reload();
            }
        });

    });
}

function initConflictMergeButton() {
    $('.conflictMergeButton').click(function () {
        $('#conflictModalAccept')[0].setAttribute("match-id", $(this).attr("match-id"));
        $('#conflictModalAccept').prop("disabled", true);
        $('#conflictModalDeny')[0].setAttribute("match-id", $(this).attr("match-id"));
        var body = $('#conflictModalDiaBody');
        form_data = {
            formAction: "getConflict",
            formMatchId: $(this).attr("match-id")
        }

        $.post("/person/edit", form_data).done(function (data) {
            var body = document.createElement("form");
            $(body).addClass("form-group");
            $(body)[0].setAttribute("id", "conflictInputForm")
            $(body)[0].setAttribute("method", "post")

            for (conflict in data) {
                var group = document.createElement("div");
                $(group).addClass("input-group");
                $(group)[0].setAttribute("style", "padding-top: 6px")
                var span = document.createElement("span");
                $(span).addClass("input-group-addon");
                var textnode = document.createTextNode(data[conflict].field);
                span.appendChild(textnode);
                group.appendChild(span);


                var input = document.createElement("input");
                $(input).addClass("form-control conflictInput");
                $(input)[0].setAttribute("type", "text");
                $(input)[0].setAttribute("id", "text");
                $(input)[0].setAttribute("name", 'person.' + data[conflict].field);
                $(input)[0].setAttribute("placeholder", "( " + data[conflict].person1Value + " | " + data[conflict].person2Value + " )");
                if (data[conflict].field == 'gender') {
                    $(input)[0].setAttribute("pattern", "(m|F)");
                    $(input)[0].setAttribute("title", "Gender must be 'm' or 'F'");
                } else if (data[conflict].field == 'mainName') {
                    $(input)[0].setAttribute("title", "Lastname, Fistname");
                    $(input)[0].setAttribute("pattern", "(.+)(, )(.+)");
                } else {
                    $(input)[0].setAttribute("pattern", ".+");
                }

                input.addEventListener('keyup', function () {
                    var notSatisfiedInputs = $.grep($('#conflictInputForm').serializeArray(), function (input) {
                        var fieldName = $(input).attr('name');
                        var reg;
                        switch (fieldName) {
                            case 'person.gender':
                                reg = new RegExp('(m|F)');
                                break;
                            case 'person.mainName':
                                reg = new RegExp('(.+)(,)(.+)');
                                break;
                            default:
                                reg = new RegExp('.+');
                        }

                        return ($(input).attr('value').length == 0 || !reg.test($(input).attr('value')));
                    });
                    if (notSatisfiedInputs.length == 0 && $('#conflictInputForm').serializeArray().length > 0) {

                        $('#conflictModalAccept').prop("disabled", false);
                    } else {
                        $('#conflictModalAccept').prop("disabled", true);
                    }
                });

                group.appendChild(input);
                body.appendChild(group);
            }
            $("#conflictModalDiaBody").html(body);
        });
    });
}





