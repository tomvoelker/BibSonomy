/** TODO: merge with friendsoverview logic */
$(function() {
	$('.groupUnshare').hover(function() {
		$(this).removeClass('btn-success').addClass('btn-danger');
		$(this).children('.fa').removeClass('fa-check').addClass('fa-times');
		$(this).children('.button-text').text(getString('groups.actions.unshareDocuments'));
	}).mouseleave(function() {
		$(this).removeClass('btn-danger').addClass('btn-success');
		$(this).children('.fa').removeClass('fa-times').addClass('fa-check');
		$(this).children('.button-text').text(getString('groups.documentsharing.shared'));
	});




//instantiate the bloodhound suggestion engine
var engine = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    highlight: true,
    local: [{
    	value: "BibTeX"
    }, {
    	value: "Endnote"
    }]
});



// initialize the bloodhound suggestion engine
engine.initialize();

$('.typeahead').typeahead(null, {
    displayKey: 'value',
    highlight: true,
    source: engine.ttAdapter()
});


$('#searchCitationAutocomplete').on('typeahead:select', function (e, datum) {
	var toBeAppended = '<li class="list-group-item favouriteLayoutsListItem"><input type="hidden" name="user.settings.favouriteLayouts"  id="SIMPLE/' + datum.value.toUpperCase() + '" value="SIMPLE/' + datum.value.toUpperCase() + '"/><span class="btn btn-default badge label-danger delete-Style">Delete</span>' + datum.value + '</li>';
	$('#favouriteLayoutsList').append(toBeAppended);
	clearFavouriteLayoutsList();
});


$('#searchCitationAutocomplete').on('keydown', function(event) {
    // Define tab key

    if (event.which == 13) // if pressing enter
    	var e = jQuery.Event("keydown");
    	e.keyCode = e.which = 9; // 9 == tab //error message but still working??????
    	event.preventDefault();
        $('#searchCitationAutocomplete').trigger(e); // trigger "tab" key - which works as "enter"
});

$('.delete-Style').click(function(){
	$(this).parent().remove();
	
});



function clearFavouriteLayoutsList() { //removing duplicates
	var seen = {};
	$('.favouriteLayoutsListItem').each(function() {
	    var txt = $(this).text();
	    if (seen[txt])
	        $(this).remove();
	    else
	        seen[txt] = true;
	});
}


});