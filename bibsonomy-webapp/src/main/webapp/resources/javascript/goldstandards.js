/**
 * TODO
 */

var FADE_DURATION = 1000;
var GOLD_REFERENCE_URL = '/ajax/goldstandards/references';

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
		var deleteLink = $('<span class="reference_menu"><a href="#">' + getString('post.actions.edit.gold.references.delete') + '</a></span>'); // TODO: i18n
		deleteLink.hide();
		$(this).append(deleteLink);
		deleteLink.fadeIn(FADE_DURATION);
		
		deleteLink.click(deleteReference);
	});
	
	var addForm = $('<form class="reference_menu"></form>');
	var input = $('<input type="text" />');
	addForm.append($('<label>' + getString('post.actions.edit.gold.references.add') + ': </label>'));
	addForm.append(input);
	input.autocomplete({
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
						}
					}));
				}
			});
		},
		minLength: 3,
		select: function(event, ui) {
			addReference(ui.item);
			return false;
		},
		focus: function(event, ui) {
			return false;
		}
	})
	.data('autocomplete')._renderItem = function(ul, item) {
		// no A <-> A reference
		if (item.value == getHash()) {
			return ul;
		}
		
		// TODO: remove already 'referenced' publications
		
		return $('<li></li>').data('item.autocomplete', item).append($('<a></a>').html(item.label + '<br><span class="ui-autocomplete-subtext">' + item.author + '</span>')).appendTo(ul);
	};
	
	addForm.hide();
	$("#gold_references").append(addForm);
	addForm.fadeIn(FADE_DURATION);
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
			url: GOLD_REFERENCE_URL + '?ckey=' + getCKey() + '&hash=' + getHash() + '&references=' + referenceHash,
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

function addReference(reference) {
	var referenceHash = reference.value;
	$.ajax({
		url: GOLD_REFERENCE_URL,
		data: {ckey: getCKey(), hash: getHash(), references: referenceHash},
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
		
		var author = authors[i];
		
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
 * @returns the ckey
 */
function getCKey() {
	return $('#gold_menu').data('ckey');
}

function getHash() {
	return $('#gold_title').data('interhash');
}