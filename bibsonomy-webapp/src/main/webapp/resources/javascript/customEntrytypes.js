$(function () {
    var entrytypeSelect = $('#post\\.resource\\.entrytype');
    var entrytypeHelp = $('#entrytypeHelp .help-description-list');

    // check, if a list to remove is set
    if (Array.isArray(removedEntrytypes) && removedEntrytypes.length) {
        adjustEntrytypesRemoved();
    }

    if (Array.isArray(extraEntrytypes) && extraEntrytypes.length) {
        adjustEntrytypesExtra();
    }

    // sort entries after entrytypes have been added/removed
    // sort entrytype dropdown by translated entrytype name
    entrytypeSelect.html($(entrytypeSelect).children('option').sort(function(a, b) {
        return $(a).text().toLowerCase() < $(b).text().toLowerCase() ? -1 : 1;
    }));

    // sort help menu by translated entrytype name
    entrytypeHelp.html($(entrytypeHelp).children('div').sort(function(a, b) {
        return $(a).data('entrytype-title').toLowerCase() < $(b).data('entrytype-title').toLowerCase() ? -1 : 1;
    }));

    // set selected entrytype
    var selected = entrytypeSelect.data('selected-entrytype');
    if (!selected) {
        // If no entrytype given, select first option
        $(entrytypeSelect).val($(entrytypeSelect).children("option:first").val()).change();
    } else {
        $(entrytypeSelect).val(selected).change();
    }

    // on change of select, show description popover
    $(entrytypeSelect).popover({
        html: true,
        placement: 'top',
        trigger: 'manual',
        content: function () {
            return $(entrytypeSelect).find(':selected').data('description');
        }
    });

    $(entrytypeSelect).change(function () {
        $(entrytypeSelect).popover('show');
    });

    $(entrytypeSelect).focusout(function () {
        $(entrytypeSelect).popover('hide');
    });
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
        var title = getString('post.resource.entrytype.' + element + '.title');
        var description = getString('post.resource.entrytype.' + element + '.description');
        // Create new entry for selection
        var newOption = $('<option></option>', { 'data-description': description })
            .val(element)
            .html(title);
        entrytypeSelect.append(newOption);

        // Create new entry for description list
        var newDescription = $('<div></div>', {class: 'entrytype-description'})
            .data('entrytype', element)
            .data('entrytype-title', title);
        $('<dt></dt>').html(title + '<span> (' + element + ')</span>')
            .appendTo(newDescription);
        $('<dd></dd>').html(description)
            .appendTo(newDescription);
        entrytypeHelp.append(newDescription);
    });
}
