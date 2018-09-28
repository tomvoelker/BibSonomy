$(document).ready(function() {
    $('.delete-button').click(function (e) {
        var parent = $(e.target).parent();
        var interhash, index, type;
        // Check if clicked the button or the trash item
        if (parent.is('span')) {
            interhash = parent.find('.interhash');
            index = parent.find('.index');
            type = parent.find('.type');
        } else {
            interhash = parent.parent().find('.interhash');
            index = parent.parent().find('.index');
            type = parent.parent().find('.type');
        }
        $('#interhashToDelete').val(interhash.text());
        $('#indexToDelete').val(index.text());
        $('#typeToDelete').val(type.text());
        $("#unlinkPublication").modal("toggle");
    });
});
