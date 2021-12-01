/**
 * IMPORTANT: Variables are only to be overwritten in custom PUMA instances
 */
var removedEntrytypes = [];
var extraEntrytypes = [];

$(function() {
    // check, if a list to remove is set
    if (Array.isArray(removedEntrytypes) && removedEntrytypes.length) {
        adjustEntrytypesRemoved();
    }

    if (Array.isArray(extraEntrytypes) && extraEntrytypes.length) {
        adjustEntrytypesExtra();
    }

    // set selected if non is selected
    var entrytypeSelect = $('#post\\.resource\\.entrytype');
    var selected = entrytypeSelect.data('selected-entrytype');
    $(entrytypeSelect).find('option[value=' + selected + ']').attr('selected', 'selected');
});


/**
 * Adjust displayed entrytypes for custom PUMA instances,
 * where certain entrytypes are hidden.
 */
function adjustEntrytypesRemoved() {
    var entrytypeHelp = $('#entrytypeHelp .help-description-list');
    var entrytypeSelect = $('#post\\.resource\\.entrytype');

    // remove excluded entrytypes from select
    $(entrytypeSelect).children().each(function () {
        if (removedEntrytypes.includes($(this).val())) {
            $(this).remove();
        }
    });

    // remove excluded entrytype from description list
    $(entrytypeHelp).children().each(function () {
        if (removedEntrytypes.includes($(this).data('entrytype'))) {
            $(this).remove();
        }
    });
}



/**
 * Adjust displayed entrytypes for custom PUMA instances,
 * where extra entrytypes are added.
 */
function adjustEntrytypesExtra() {
    var entrytypeHelp = $('#entrytypeHelp .help-description-list');
    var entrytypeSelect = $('#post\\.resource\\.entrytype');

    // add new extra entrytypes to selection and help description list
    extraEntrytypes.forEach(function (element) {
        // Create new entry for selection
        var newOption = $('<option></option>').val(element).html(element);
        entrytypeSelect.append(newOption);

        // Create new entry for description list
        var newDescription = $('<div></div>', {
            class: 'entrytype-description'
        }).data('entrytype', element);
        $('<dt></dt>').html(getString('post.resource.entrytype.' + element + '.title'))
            .appendTo(newDescription);
        $('<dd></dd>').html(getString('post.resource.entrytype.' + element + '.description'))
            .appendTo(newDescription);
        entrytypeHelp.append(newDescription);
    });
}