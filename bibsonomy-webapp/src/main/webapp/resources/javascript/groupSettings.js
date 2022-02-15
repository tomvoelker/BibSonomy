$(function () {
    initPresetTagSelection();
});

/**
 * Check, if tag name is given before submit.
 * @returns {boolean}
 */
function checkAddTag() {
    var tagNameInput = $('#presetTagNameAdd');
    if (!tagNameInput.val()) {
        tagNameInput.parents('.form-group').addClass('has-error');
        return false;
    } else {
        return true;
    }
}

/**
 * Updates description with selected tag.
 */
function initPresetTagSelection() {
    $('#presetTagNameUpdate').children('option').click(function () {
        $('#presetTagDescriptionUpdate').val($(this).data('description'));
    });
}