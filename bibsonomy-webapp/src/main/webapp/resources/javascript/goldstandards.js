/**
 * for deleting and adding references
 */

var FADE_DURATION = 1000;
var GOLD_REFERENCE_URL = '/ajax/goldstandards/relation';
var RELATION=0;
var REFHASH;

$(function() {
	$("#gold_exports").tabs();
	
	// hide reference list if no publication references it
	if ($("li.reference").length == 1) { // 1 because template for list item is in the list :)
		$("#gold_references").hide();
	}
});

function editReferences() {
	// TODO replace menu text
	if ($('.reference_menu').length > 0) {
		removeReferenceMenu();
	} else {
		// display list if hidden
		$("#gold_references").show();
		addReferenceMenu();
	}
}

function addReferenceMenu() {
	$("li.reference").each(function() {
		// display delete link
		var deleteLink = $('<span class="reference_menu"><a href="#">' + getString('post.actions.edit.gold.references.delete') + '</a></span>');
		deleteLink.hide();
		$(this).append(deleteLink);
		deleteLink.fadeIn(FADE_DURATION);
		
		deleteLink.click(deleteReference);
	});
	
	//Reference experiment: lka
//	var Form = $('<form class="new_reference_menu"><h3>'+getString('post.actions.edit.gold.references.addcitation')+'</h3></form>');
//	var exRefHashInput = $('<input id  = "refHash2" type="text"/>');
//	var exRelationType = {
//		    '0': 'Rerefence',
//		    '1': 'Part of'
//		};
//	var exRelationInput = $('<select id="relation2" />');
//	for(var val in exRelationType) {
//		    $('<option />', {value: val, text: exRelationType[val]}).appendTo(exRelationInput);
//		}
//	
//	var exAddRefButton =   $('<button id = "addButon2" type = "button">\n'+getString('post.actions.edit.gold.references.addbutton')+'</button>');
//	Form.append($('<label>'+getString('post.actions.edit.gold.references.relation')+': </label>'));
//	Form.append(exRelationInput);
//	Form.append($('<label>\n' + getString('post.actions.edit.gold.references.publication') + ': </label>'));
//	Form.append(exRefHashInput);
//	Form.append(exAddRefButton);
//	$("#gold_references").append(Form);
//	$("#addButon2").click(function(){
//		RELATION = document.getElementById("relation2").value;
//		REFHASH = $("#refHash2").val();
//
//		addReferenceHASHnRelation(REFHASH, RELATION);
//	});
//	
//	function addReferenceHASHnRelation(reference, relation) {
//		var referenceHash = reference;
//		$.ajax({
//			url: GOLD_REFERENCE_URL,
//			data: {ckey: ckey, hash: getGoldInterHash(), references: referenceHash, relation: relation},
//			type: 'POST',
//			success: function(data) {
//				// clone the template
//				var template = $('#referenceTemplate').clone();
//				template.attr('id', ''); // remove id
//				template.attr('data-interhash', referenceHash); // set interHash in data attribute
//				
//				// authors and editors
//				var personList = template.find('.authorEditorList');
//				personList.append(getAuthorsEditors(reference.authors, reference.editors));
//				
//				// title
//				var titleLink = template.find('.publicationLink');
//				titleLink.attr('href', '/bibtex/' + referenceHash);
//				// TODO: bibtex not cleaned
//				titleLink.text(reference.title); // TODO: escape?!
//				
//				// year
//				template.find('.year').text(reference.year);
//				
//				// add template
//				$('#gold_references ol').append(template);
//				template.show();
//				
//				// delete link
//				$('span.reference_menu a').click(deleteReference);
//			}
//		});
//	}

	
	// display function for searching gold standards
	var addForm = $('<form class="reference_menu"><h3>'+getString('post.actions.edit.gold.references.addcitation')+'</h3></form>');
	var relationType = {
		    '0': getString('post.actions.edit.gold.references.ref'),
		    '1': getString('post.actions.edit.gold.references.partof')
		};
	var relationInput = $('<select id="relation" />');
	for(var val in relationType) {
		    $('<option />', {value: val, text: relationType[val]}).appendTo(relationInput);
		}
	addForm.append($('<label>'+getString('post.actions.edit.gold.references.relation')+': </label>'));
	addForm.append(relationInput);
	
	addForm.append($('<label>\n' + getString('post.actions.edit.gold.references.publication') + ': </label>'));	
	var refHashInput = $('<input type="text" />');
	addForm.append(refHashInput);
	var addRefButton =   $('<button id = "addButon" type = "button">\n'+getString('post.actions.edit.gold.references.addbutton')+'</button>');
	addForm.append(addRefButton);
	refHashInput.autocomplete({
		source: function(request, response) {
			$.ajax({
				url: '/json/tag/' + createParameters(request.term),
				data: {items: 10, resourcetype: 'goldStandardPublication', duplicates: 'no'},
				dataType: 'json',
				success: function(data) {
					response($.map(data.items, function(item) {
						return {
							label: item.label + ' (' + item.year + ')',
							value: item.interHash,
							author: (concatArray(item.author, 40, ' ' + getString('and') + ' ')),
							authors: item.author,
							editors: item.editor,
							year: item.year,
							title: item.label
						};
					}));
				}
			});
		},
		minLength: 3,
		select: function(event, ui) {
			REFHASH = ui.item;
//			addReference(ui.item);
			return false;
		},
		focus: function(event, ui) {
			return false;
		}
	})
	.data('autocomplete')._renderItem = function(ul, item) {
		// no A <-> A reference
		if (item.value == getGoldInterHash()) {
			return ul;
		}
		// TODO: remove already 'referenced' publications
		
		return $('<li></li>').data('item.autocomplete', item).append($('<a></a>').html(item.label + '<br><span class="ui-autocomplete-subtext">' + item.author + '</span>')).appendTo(ul);
	};
	
	addForm.hide();
	$("#gold_references").append(addForm);
	addForm.fadeIn(FADE_DURATION);
	$("#addButon").click(function(){
		RELATION = document.getElementById("relation").value;
		addReferenceNRelation(REFHASH, RELATION);
	});
}

function removeReferenceMenu() {
	$('.reference_menu').fadeOut(FADE_DURATION, function() {
		$(this).remove();
	});
}

function deleteReference() {
	var referenceView = $(this).parent('li');
	var referenceHash = referenceView.data("interhash");

	// TODO: added reference can't be removed!
	
	if (confirm(getString('post.actions.edit.gold.references.delete.confirm'))) {
		$.ajax({
			url: GOLD_REFERENCE_URL + '?ckey=' + ckey + '&hash=' + getGoldInterHash() + '&references=' + referenceHash,
			type: 'DELETE',
			success: function(response) {
						referenceView.fadeOut(FADE_DURATION, function() {
							referenceView.remove();
						});
					}
		});
	}
	
	return false;
}

function addReferenceNRelation(reference, relation) {
	var referenceHash = reference.value;
	$.ajax({
		url: GOLD_REFERENCE_URL,
		data: {ckey: ckey, hash: getGoldInterHash(), references: referenceHash, relation: relation},
		type: 'POST',
		success: function(data) {
			// clone the template
			var template = $('#referenceTemplate').clone();
			template.attr('id', ''); // remove id
			template.attr('data-interhash', referenceHash); // set interHash in data attribute
			
			// authors and editors
			var personList = template.find('.authorEditorList');
			personList.append(getAuthorsEditors(reference.authors, reference.editors));
			
			// title
			var titleLink = template.find('.publicationLink');
			titleLink.attr('href', '/bibtex/' + referenceHash);
			// TODO: bibtex not cleaned
			titleLink.text(reference.title); // TODO: escape?!
			
			// year
			template.find('.year').text(reference.year);
			
			// add template
			$('#gold_references ol').append(template);
			template.show();
			
			// delete link
			$('span.reference_menu a').click(deleteReference);
		}
	});
}

function getAuthorsEditors(authors, editors) {
	if (authors.length > 0) {
		return getPersonList(authors);
	}
	// else return editors
	return getPersonList(editors) + ' (' + getString('bibtex.editors.abbr') + ')';
}

function getPersonList(persons) {
	var result = new Array();
	for (var i = 0; i < persons.length; i++) {
		if (i != 0) {
			result.push(', ');
		}
		if (i == (persons.length - 1) && i != 0) { // last item but not the first one
			result.push(getString('and'));
			result.push(' ');
		}
		
		var author = persons[i];
		
		// FIXME: last name is last string
		var authorNameSplit = author.split(' ');
		
		for (var j = 0; j < authorNameSplit.length - 1; j++) {
			result.push(authorNameSplit[j]);
			result.push(' ');
		}
		
		var lastName = authorNameSplit[authorNameSplit.length - 1];
		// TODO: escape?!
		result.push('<a href="/author/' + lastName + '">');
		result.push(lastName);
		result.push('</a>');
	}
	
	return result.join('');
}

/** 
 * @returns the hash of the publication
 */
function getGoldInterHash() {
	return $('#gold_title').data('interhash');
}