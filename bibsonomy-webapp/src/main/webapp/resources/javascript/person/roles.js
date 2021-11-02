/**
 * on load
 */
$(function () {
    initToggleAvailableRoles();
    initPersonAutocomplete();
    initRoleEditing();
});

function initToggleAvailableRoles() {
    // toggle view/hide all available roles
    $(".personPageShowAdditionalRoleFields").click(function () {
        $(".personPageAdditionalRoleFields", $(this).parent()).toggle();

        // toggle the link text
        if ($(".personPageShowAdditionalRoleFieldsMore", this).is(":visible")) {
            $(".personPageShowAdditionalRoleFieldsMore", this).hide();
            $(".personPageShowAdditionalRoleFieldsLess", this).show();
        } else {
            $(".personPageShowAdditionalRoleFieldsLess", this).hide();
            $(".personPageShowAdditionalRoleFieldsMore", this).show();
        }
    });
}

function initPersonAutocomplete() {
    setupPersonAutocomplete('#addRoleAuto', "search", 'extendedPersonName', function (data) {
        $("#btnAddRoleSubmit").attr("data-person-name", data.personName);
        $("#btnAddRoleSubmit").attr("data-extended-person-name", data.extendedPersonName);
        $("#btnAddRoleSubmit").attr("data-person-id", data.personId);
    });
}

function initRoleEditing() {
    $(".addRole").on("click", function () {
        addRole($(this));
    });

    $(".deleteRole").on("click", function () {
        deleteRole($(this));
    });

    $(".editRole").on("click", function () {
        editRole($(this));
    });


    // empty the input field for the add role dialog
    $('#addRole').on('show.bs.modal', function (e) {
        $('#addRoleAuto').val('');
        // also remove the fields set by typeahead
        $('#btnAddRoleSubmit').removeAttr('data-person-name');
        $('#btnAddRoleSubmit').removeAttr('data-extended-person-name');
        $('#btnAddRoleSubmit').removeAttr('data-person-id');
        $("#addRoleAuto").typeahead('val', '');

        $('#btnAddRoleSubmit').addClass('disabled');
    });

    $("#btnAddRoleSubmit").click(function () {
        var e = $(this);
        if ($("#addRoleAuto").typeahead('val') !== e.data('extended-person-name')) {
            // init values for implicitly adding a new person
            e.attr("data-person-name", $("#addRoleAuto").typeahead('val'));
            e.attr("data-person-id", '');
        }

        var nameSplit = e.data('person-name').split(" ");
        if (nameSplit.length > 1) {
            var firstName = nameSplit[0];
            nameSplit[0] = "";
            var lastName = nameSplit.join(" ");
        } else {
            var firstName = "";
            var lastName = nameSplit[0];
        }

        var formData = $("#addRoleForm").serializeArray();
        formData.push({name: "editAction", value: "addRole"});
        formData.push({name: "newName.firstName", value: firstName});
        formData.push({name: "newName.lastName", value: lastName});
        formData.push({name: "personId", value: e.attr("data-person-id")});
        formData.push({name: "formInterHash", value: e.attr("data-relation-simhash1")});
        formData.push({name: "formPersonRole", value: e.attr("data-person-role")});

        // TODO: validate
        formData.push({name: "resourcePersonRelation.personIndex", value: e.data('author-index')});
        formData.push({name: "formPersonIndex", value: e.data('author-index')});

        $.post("/person/edit", formData).done(
            function (data) {
                if (data.exception) {
                    alert(getString('person.show.error.addRoleFailed'));
                } else {
                    e.attr("data-person-id", data.personId);
                    var htmlToAdd = addRoleHtml(data.resourcePersonRelationid, firstName, lastName, data.personId, data.personUrl);
                    var selector = "." + e.attr("data-relation-simhash1") + "_" + e.attr("data-relation-simhash2") + " ." + e.attr("data-person-role") + " .addRole";
                    var selectedNode = $(selector);
                    selectedNode.before(htmlToAdd);
                    $("#addRole").modal("hide");
                }
            }
        );

    });

    $("#btnDeleteRoleSubmit").click(function () {
        var e = $(this);
        $.post("/person/edit",
            {
                editAction: "deleteRole",
                formResourcePersonRelationId: e.attr("data-resourcePersonRelation-id")
            }
        ).done(function (data) {
            $(".resourcePersonRelation_" + e.attr("data-resourcePersonRelation-id")).remove();
            $("#deleteRole").modal("hide");
        });
    });

    $("#btnEditRoleSubmit").click(function (e) {
        $("#editRole input[type=checkbox]:checked").each(function () {
            alert($(this).val());
        });
    });
}

function addRole(obj) {
    $("#btnAddRoleSubmit").attr("data-person-id", obj.attr("data-person-id"));
    // FIXME: seems to be not existing/used. check
    $("#btnAddRoleSubmit").attr("data-author-index", obj.attr("data-author-index"));
    $("#btnAddRoleSubmit").attr("data-relation-simhash1", obj.attr("data-relation-simhash1"));
    $("#btnAddRoleSubmit").attr("data-relation-simhash2", obj.attr("data-relation-simhash2"));
    $("#btnAddRoleSubmit").attr("data-person-role", obj.attr("data-person-role"));
}

function editRole(obj) {
    $("#btnEditRoleSubmit").attr("data-resourcePersonRelation-id", obj.attr("data-resourcePersonRelation-id"));
}

function deleteRole(obj) {
    $("#btnDeleteRoleSubmit").attr("data-resourcePersonRelation-id", obj.attr("data-resourcePersonRelation-id"));
}

function addRoleHtml(resourcePersonRelationid, personFirstName, personLastName, personId, personUrl) {
    var s = $("<span class='resourcePersonRelation_" + resourcePersonRelationid + "'></span");
    var a = $("<a href='" + personUrl + "'> " + personFirstName + " " + personLastName + " </a>");
    var sss = $(" <span data-toggle='modal' data-target='#deleteRole' data-resourcePersonRelation-id='" + resourcePersonRelationid + "' style='color:darkred;cursor:pointer' href='#deleteRole' class='deleteRole fa fa-remove'>&#160;</span>");

    sss.click(function () {
        deleteRole($(this));
    });

    s.append(a);
    //s.append(ss);
    s.append(sss);

    return s;
}