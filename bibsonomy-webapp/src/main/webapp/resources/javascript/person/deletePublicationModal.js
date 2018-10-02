$(document).ready(function() {
    $('.delete-button').click(function (e) {
        var ele = $(e.target);
        if (ele.is('span')) {
            ele = ele.parent();
        }
        var interhash = ele.data('interhash');
        var index = ele.data('personindex');
        var type = ele.data('relationtype');
        var form = $('#deletePersonResourceRelationForm');
        form.find('#interhash').val(interhash);
        form.find('#index').val(index);
        form.find('#type').val(type);
        $("#unlinkPublication").modal("toggle");
    });
});
