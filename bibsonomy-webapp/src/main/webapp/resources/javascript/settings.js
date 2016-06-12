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
    
	// a lot of typeahead configuration will follow
	// --
	
	//firstly two "simple" styles in JSON
	var simpleData = '[{"source": "SIMPLE", "displayName": "BibTeX", "name":"BIBTEX"},{"source": "SIMPLE", "displayName": "EndNote", "name":"ENDNOTE"}]';
	var jsonObj = $.parseJSON(simpleData);
	
	//initializing what has already been loaded. Nothing.
	var csl = false;
	var jabref = false;
	
	var cslData;
	var jabRefData;
	
	
	//getting external JSON for CSL styles
	$.get("/csl-style", function (data) {
		//safety first, safety always
		data = $.trim(data);
		data = $.parseJSON(data);
		cslData = data;
		processResultCSL(data);
		setCSLDisplayName();
	});
	
	//getting external JSON for JABREF styles
	$.get("/layoutinfo", function (data) {
		jabRefData = data;
		processResultJabref(data);
		setJabRefDisplayName();
	});

	//adding JABREF to the "simple" styles array, which will be displayed on the twitter typeahead
	function processResultJabref(data) {
		for (var prop in data.layouts) {
			var JabrefData = '{"source": "JABREF", "displayName": "' + data.layouts[prop].displayName + '", "name":"' + data.layouts[prop].name.toUpperCase() +'"}';
			var JabrefObj = $.parseJSON(JabrefData);
			jsonObj.push(JabrefObj);
		}
		//everything fetched, good to go
		jabref = true;
		initializeBloodhound();
	};
	
	//adding CSL to the "simple" styles array, which will be displayed on the twitter typeahead
	function processResultCSL(data) {
		for (var prop in data.layouts) {		
			var CSLData = '{"source":"CSL","displayName":"' + data.layouts[prop].displayName + '","name":"' + data.layouts[prop].name.toUpperCase() +'"}';
			var CSLObj = $.parseJSON(CSLData);
			jsonObj.push(CSLObj);
		}
	
		//everything fetched, good to go
		csl = true;
		initializeBloodhound();
	};

	//instantiate the bloodhound suggestion engine
	//more or less standard procedure for typeahead
	function initializeBloodhound() {
		//only begin initializing if everything has been loaded
		if(csl && jabref){
			var engine = new Bloodhound({
				datumTokenizer: function (d) {return Bloodhound.tokenizers.whitespace(d.displayName);},
				queryTokenizer: Bloodhound.tokenizers.whitespace,
				//using the created combination of "simple" layouts array, CSL and Jabref layouts
				local: jsonObj
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
		}
	};

	//triggers when something is selected in the typeahead
	//adds a new list item to the list including a remove button and an input field with correct ID, source and displayName
	//ID has to be "source"/"id" for the StringToFavouriteLayoutConverter to read
	$('#searchCitationAutocomplete').on('typeahead:select', function (e, datum) {
		var toBeAppended = '<li class="list-group-item favouriteLayoutsListItem"><input type="hidden" name="user.settings.favouriteLayouts"  id="'+ datum.source.toUpperCase() +'/' + datum.name.toUpperCase() + '" value="'+datum.source.toUpperCase()+'/' + datum.name.toUpperCase() + '"/><span class="btn btn-default badge label-danger delete-Style">Delete</span>' + datum.displayName + '</li>';
		$('#favouriteLayoutsList').append(toBeAppended);
		clearFavouriteLayoutsList();
	});

	//catching presses of "enter", else the form would be submitted by each "accidental" press
	$('#searchCitationAutocomplete').on('keydown', function(event) {
		if (event.which == 13) // if pressing enter
			event.preventDefault();
	});
	
	//getting the "Delete" batch to work
	$('.delete-Style').click(function(){
		$(this).parent().remove();
	});

	function setCSLDisplayName() {
		$("[id^='displayName:']").each(function() {
			if($(this).attr("source").toUpperCase() == "CSL"){
				for (var prop in cslData.layouts) {
					if(cslData.layouts[prop].name.toUpperCase() == $(this).attr("style").toUpperCase()){
						$(this).text(cslData.layouts[prop].displayName);
					}
				}
			}
		});
	}
	
	function setJabRefDisplayName() {
		$("[id^='displayName:']").each(function() {
			if($(this).attr("source").toUpperCase() == "JABREF"){
				for (var prop in jabRefData.layouts) {
					if(jabRefData.layouts[prop].name.toUpperCase() == $(this).attr("style").toUpperCase()){
						$(this).text(jabRefData.layouts[prop].displayName);
					}
				}
			}
		});
	}
	
	
	//TODO: make the clean-up "id" based
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
	
	clearFavouriteLayoutsList();
});