$(document).ready(function() {
    $('.add-button').click(function (e) {
        var ele = $(e.target);
        if (ele.is('span')) {
            ele = ele.parent();
        }
        $('#modalLinkPublicationContentText').text(ele.closest('.simplePubEntry').data('title'));
        reset_select();
        var counter = 0;
        extract_authors(ele.closest('.simplePubEntry').data('author')).forEach(function (e) {
            $('#addPublicationAuthorSelect').append('<option value="' + counter + '">' + e + '</option>');
            counter++;
        });
        var index = ele.data('index');
        $('#addPublicationAuthorSelect').val(index);
        var type = ele.data('type');
        var interhash = ele.data('interhash');
        var form = $('#deletePersonResourceRelationForm');
        form.find('#interhash').val(interhash);
        form.find('#index').val(index);
        form.find('#type').val(type);
        $("#linkPublication").modal("toggle");
    });

    $('#addPublicationAuthorSelect').change(function() {
        $('#deletePersonResourceRelationForm').find('#index').val($(this).val())
    });
});

function reset_select(){
    $('#addPublicationAuthorSelect').find('option').remove();
}

function extract_authors(e){
    var list = [];
    var names = e.split(",");
    for (var i = 0; i < names.length; i = i + 2) {
        list.push(names[i + 1].replace(" ", "").replace("[", "").replace("]", "") + " " + names[i].replace(" ", "").replace("[", "").replace("]", ""));
    }
    return list;
}
