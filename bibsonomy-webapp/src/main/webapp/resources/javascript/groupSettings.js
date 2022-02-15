$(function () {
    initPresetTagSelection();
});

function checkAddTag() {
    var tagNameInput = $('#presetTagNameAdd');
    if (!tagNameInput.val()) {
        tagNameInput.parents('.form-group').addClass('has-error');
        return false;
    } else {
        return true;
    }
}

function initPresetTagSelection() {
    $('#presetTagNameUpdate').children('option').click(function () {
        $('#presetTagDescriptionUpdate').val($(this).data('description'));
    });
}