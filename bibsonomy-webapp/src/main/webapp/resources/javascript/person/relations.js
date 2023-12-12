/**
 * on load
 */
$(function () {

});


function addPersonRelation(button) {
    var interhash = $(button).data('interhash');
    var index = $(button).data('personindex');
    var type = $(button).data('relationtype');

    var form = $('#addPersonResourceRelationForm');
    form.find('#interhash').val(interhash);
    form.find('#index').val(index);
    form.find('#type').val(type);
    $("#linkPublication").modal("toggle");
}

function deletePersonRelation(button) {
    var interhash = $(button).data('interhash');
    var index = $(button).data('personindex');
    var type = $(button).data('relationtype');

    var form = $('#deletePersonResourceRelationForm');
    form.find('#interhash').val(interhash);
    form.find('#index').val(index);
    form.find('#type').val(type);
    $("#unlinkPublication").modal("toggle");
}

function extract_authors(e) {
    var list = [];
    var names = e.split(",");
    for (var i = 0; i < names.length; i = i + 2) {
        list.push(names[i + 1].replace(" ", "").replace("[", "").replace("]", "") + " " + names[i].replace(" ", "").replace("[", "").replace("]", ""));
    }
    return list;
}
