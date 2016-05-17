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
    
	//two "simple" styles in JSON
	var simpleData = '[{"source": "SIMPLE", "displayName": "BibTeX", "name":"BIBTEX"},{"source": "SIMPLE", "displayName": "EndNote", "name":"ENDNOTE"}]';
	var jsonObj = $.parseJSON(simpleData);
		
	//getting external JSON for JABREF styles
	$.get("/layoutinfo", function (data) {

		$.get("/csl-style", function (data) {
		
			processResultCSL(data);
			
		});
		
		processResultJabref(data);
	
	});

	//adding JABREF to source array, which will be displayed on the twitter typeahead
	function processResultJabref(data) {
		for (var prop in data.layouts) {
		
			//		alert("Key:" + prop);
			//		alert("Value:" + data.layouts[prop].displayName);
		
			var JabrefData = '{"source": "JABREF", "displayName": "' + data.layouts[prop].displayName + '", "name":"' + data.layouts[prop].name.toUpperCase() +'"}';
			var JabrefObj = $.parseJSON(JabrefData);
			jsonObj.push(JabrefObj);
		}
	};
	
	function processResultCSL(data) {
		for (var prop in data.layouts) {
		
			//		alert("Key:" + prop);
			//		alert("Value:" + data.layouts[prop].displayName);
		
			var CSLData = '{"source":"CSL","displayName":"' + data.layouts[prop].displayName + '","name":"' + data.layouts[prop].name.toUpperCase() +'"}';
			var CSLObj = $.parseJSON(JabrefData);
			jsonObj.push(CSLObj);
		}
	
		//everything fetched, good to go
		initializeBloodhound();
	};

	//instantiate the bloodhound suggestion engine

	function initializeBloodhound() {
		var engine = new Bloodhound({
			datumTokenizer: function (d) {return Bloodhound.tokenizers.whitespace(d.displayName);},
			queryTokenizer: Bloodhound.tokenizers.whitespace,
			local: jsonObj,
		});


		// initialize the bloodhound suggestion engine
		engine.initialize();

		$('.typeahead').typeahead({
			highlight: true,
		    minLength: 1
		},
		{
			displayKey: 'displayName',
			source: engine.ttAdapter()
		});
	};


	$('#searchCitationAutocomplete').on('typeahead:select', function (e, datum) {
		var toBeAppended = '<li class="list-group-item favouriteLayoutsListItem"><input type="hidden" name="user.settings.favouriteLayouts"  id="'+ datum.source.toUpperCase() +'/' + datum.name.toUpperCase() + '" value="'+datum.source.toUpperCase()+'/' + datum.name.toUpperCase() + '"/><span class="btn btn-default badge label-danger delete-Style">Delete</span>' + datum.name + '</li>';
		$('#favouriteLayoutsList').append(toBeAppended);
		clearFavouriteLayoutsList();
	});

	//catching presses of "enter", else the form would be submitted by each accidental press
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
			var txt = $(this).text().toUpperCase();
			if (seen[txt])
				$(this).remove();
			else
				seen[txt] = true;
		});
	}

	clearFavouriteLayoutsList();
});