/**
 * on load
 */
$(function () {
    // setup autocomplete for theses
    setupBibtexAuthorSearchForForm("#addThesisAuto", "#addThesisForm");

    // setup autocomplete for advised theses
    setupBibtexAuthorSearchForForm("#addSupervisedThesisAuto", "#addSupervisedThesisForm");

    // init role editing
    initToggleAvailableRoles();
    setupPersonSearchWithSubmit("#addRoleSearch", "#btnAddRoleSubmit");
    initRoleAddDelete();
});

function initRoleAddDelete() {
    $(".addRole").on("click", function () {
        prepareAddRoleData($(this));
    });

    $(".deleteRole").on("click", function () {
        prepareDeleteRoleData($(this));
    });

    // empty the input field for the add role dialog
    $('#addRole').on('show.bs.modal', function (e) {
        $('#addRoleAuto').val('');
        $("#addRoleAuto").typeahead('val', '');

        // also remove the fields set by typeahead and disable button
        $('#btnAddRoleSubmit')
            .removeAttr('data-person-name')
            .removeAttr('data-extended-person-name')
            .removeAttr('data-person-id')
            .addClass('disabled');
    });

    $("#btnAddRoleSubmit").click(function () {
        var e = $(this);
        if ($("#addRoleSearch").typeahead('val') !== e.data('extended-person-name')) {
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
        formData.push({name: "operation", value: "ADD_ROLE"});
        formData.push({name: "newName.firstName", value: firstName});
        formData.push({name: "newName.lastName", value: lastName});
        formData.push({name: "personId", value: e.attr("data-person-id")});
        formData.push({name: "formInterHash", value: e.attr("data-relation-interhash")});
        formData.push({name: "formPersonRole", value: e.attr("data-person-role")});

        $.post("/editPerson", formData).done(
            function (data) {
                if (data.exception) {
                    alert(getString('person.show.error.addRoleFailed'));
                } else {
                    e.attr("data-person-id", data.personId);
                    var htmlToAdd = appendAddedRolePerson(data.resourcePersonRelationId, firstName, lastName, data.personId, data.personUrl);
                    var selector = ".addRole[data-relation-interhash='" + e.attr("data-relation-interhash")+ "']" +
                        "[data-person-role='" + e.attr("data-person-role")+ "']";
                    var selectedNode = $(selector);
                    selectedNode.before(htmlToAdd);
                    $("#addRole").modal("hide");
                }
            }
        );

    });

    $("#btnDeleteRoleSubmit").click(function () {
        var e = $(this);
        var formData = $("#deleteRoleForm").serializeArray();
        formData.push({name: "operation", value: "DELETE_ROLE"});
        formData.push({name: "personId", value: e.attr("data-person-id")});
        formData.push({name: "formInterHash", value: e.attr("data-relation-interhash")});
        formData.push({name: "formPersonRole", value: e.attr("data-person-role")});

        // TODO: validate
        formData.push({name: "resourcePersonRelation.personIndex", value: e.data('author-index')});
        formData.push({name: "formPersonIndex", value: e.data('author-index')});
        $.post("/editPerson", formData).done(function (data) {
            $(".resourcePersonRelation_" + e.attr("data-relation-id")).remove();
            $("#deleteRole").modal("hide");
        });
    });

    $("#btnEditRoleSubmit").click(function (e) {
        $("#editRole input[type=checkbox]:checked").each(function () {
            alert($(this).val());
        });
    });
}

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

function prepareAddRoleData(obj) {
    $("#btnAddRoleSubmit")
        .attr("data-person-id", obj.attr("data-person-id"))
        .attr("data-person-role", obj.attr("data-person-role"))
        .attr("data-relation-interhash", obj.attr("data-relation-interhash"));
}

function prepareDeleteRoleData(obj) {
    $("#btnDeleteRoleSubmit")
        .attr("data-person-id", obj.attr("data-person-id"))
        .attr("data-person-role", obj.attr("data-person-role"))
        .attr("data-author-index", obj.attr("data-author-index"))
        .attr("data-relation-interhash", obj.attr("data-relation-interhash"))
        .attr("data-relation-id", obj.attr("data-relation-id"));
}

function appendAddedRolePerson(resourcePersonRelationid, personFirstName, personLastName, personId, personUrl) {
    var container = $("<span class='resourcePersonRelation_" + resourcePersonRelationid + "'></span");
    var linkPerson = $("<a href='" + personUrl + "'> " + personFirstName + " " + personLastName + " </a>");
    var deleteButton = $(" <span data-toggle='modal' data-target='#deleteRole' data-resourcePersonRelation-id='" + resourcePersonRelationid + "' style='color:darkred;cursor:pointer' href='#deleteRole' class='deleteRole fa fa-remove'>&#160;</span>");

    deleteButton.click(function () {
        prepareDeleteRoleData($(this));
    });

    container.append(linkPerson);
    container.append(deleteButton);

    return container;
}