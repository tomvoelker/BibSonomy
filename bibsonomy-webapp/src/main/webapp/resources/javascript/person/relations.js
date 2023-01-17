/**
 * on load
 */
$(function () {
    // TODO rework and activate again
    initAddRelationButtons();
});


function initDeleteRelationButtons(button) {
    var interhash = $(button).data('interhash');
    var index = $(button).data('personindex');
    var type = $(button).data('relationtype');

    var form = $('#deletePersonResourceRelationForm');
    form.find('#interhash').val(interhash);
    form.find('#index').val(index);
    form.find('#type').val(type);
    $("#unlinkPublication").modal("toggle");
}

function initAddRelationButtons() {
    $('.add-button').click(function () {
        var e = $(this);
        if (e.is('span')) {
            e = e.parent();
        }
        $('#modalLinkPublicationContentText').text(e.closest('.simplePubEntry').data('title'));
        reset_select();
        var counter = 0;
        extract_authors(ele.closest('.simplePubEntry').data('author')).forEach(function (e) {
            $('#addPublicationAuthorSelect').append('<option value="' + counter + '">' + e + '</option>');
            counter++;
        });
        var index = e.data('index');
        $('#addPublicationAuthorSelect').val(index);
        var type = e.data('type');
        var interhash = ele.data('interhash');
        var form = $('#addPersonResourceRelationForm');
        form.find('#interhash').val(interhash);
        form.find('#index').val(index);
        form.find('#type').val(type);
        $("#linkPublication").modal("toggle");
    });

    $('#addPublicationAuthorSelect').change(function () {
        $('#addPersonResourceRelationForm').find('#index').val($(this).val())
    });
}

function reset_select() {
    $('#addPublicationAuthorSelect').find('option').remove();
}

function extract_authors(e) {
    var list = [];
    var names = e.split(",");
    for (var i = 0; i < names.length; i = i + 2) {
        list.push(names[i + 1].replace(" ", "").replace("[", "").replace("]", "") + " " + names[i].replace(" ", "").replace("[", "").replace("]", ""));
    }
    return list;
}
