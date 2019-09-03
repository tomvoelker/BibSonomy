$(document).ready(function() {
    setupPersonAutocomplete('.typeahead', "search", 'extendedPersonName', function(data) {
        $("#projectIdMemberAdd").val(data.personId);
        $("#searchMemberAutocomplete").blur();
    });

    $("#searchMemberAutocomplete").focus();

    $('#addMember').click(function(event) {
        var button = $(this);
        button.prop('disabled', true);
        event.preventDefault();

        // submit the form
        // TODO: loading screen
        var form = $('#add-member-form')
        $.ajax({
            type: "POST",
            url: '/ajax/cris/projectpersonlinks',
            data: form.serialize(), // serializes the form's elements.
            success: function() {
                button.prop('disabled', '');
                $('#addMemberModal').modal('hide');
            }
        });

    });

});
