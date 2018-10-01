$(document).ready(function() {
    $('.delete-button').click(function (e) {
        var ele = $(e.target);
        if (ele.is('span')) {
            ele = ele.parent();
        }
        var interhash = ele.data('interhash');
        var index = ele.data('personindex');
        var type = ele.data('relationtype');
        $('#interhashToDelete').val(interhash);
        $('#indexToDelete').val(index);
        $('#typeToDelete').val(type);
        $("#unlinkPublication").modal("toggle");
    });
});
