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
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('d'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    highlight: true,
    local: [{
        d: "BibTeX"
    }, {
        d: "Endnote"
    }]
});



// initialize the bloodhound suggestion engine
engine.initialize();

$('.typeahead').typeahead(null, {
    displayKey: 'd',
    source: engine.ttAdapter()
});


$('#searchCitationAutocomplete').on('typeahead:select', function (e, datum) {
	 alert(datum + "chosen");
	$("#favouriteLayoutsList ul").append('<li class="list-group-item"><input type="hidden" name="user.settings.favouriteLayouts"  id="' + datum + '" value="' + datum + '" /><span class="btn btn-default badge label-danger delete-Style">Delete</span>' + datum + '</li>');
});

$('.delete-Style').click(function(){
	$(this).parent().remove();
});
});