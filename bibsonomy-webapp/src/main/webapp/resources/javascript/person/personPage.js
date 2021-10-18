const HIDDEN_CLASS = 'hidden';
const ACTIVE_CLASS = 'active';

/**
 * on load
 */
$(function () {
	initFilterButtons('entrytype');
	loadTheses();
	loadSimilarAuthors();
	loadPublications();
});

/**
 * Add action to all filter buttons in a section. On click set to active and update search results correspondingly.
 *
 * @param field the section identified by the field
 */
function initFilterButtons(field) {
	$('#filter-entries-' + field + ' > button').click(function () {
		$(this).toggleClass(ACTIVE_CLASS);
	});
}

/**
 * Load the theses of the person.
 */
function loadTheses() {
	var personId = $('#personInfo').data('person');

	$.ajax({
		url: '/ajax/person/theses', // The url you are fetching the results.
		data: {
			// These are the variables you can pass to the request
			'requestedPersonId': personId
		},
		success: function (data) {
			$('#personTheses').html(data);
		}
	});
}

/**
 * Load publications of similar authors.
 */
function loadSimilarAuthors() {
	var personId = $('#personInfo').data('person');

	$.ajax({
		url: '/ajax/person/similar', // The url you are fetching the results.
		data: {
			// These are the variables you can pass to the request
			'requestedPersonId': personId
		},
		success: function (data) {
			$('#personSimilarAuthors').html(data);
		}
	});
}

/**
 * Load publications of the person.
 */
function loadPublications() {
	var personId = $('#personInfo').data('person');

	$.ajax({
		url: '/ajax/person/publications', // The url you are fetching the results.
		data: {
			// These are the variables you can pass to the request
			'requestedPersonId': personId
		},
		beforeSend: function () {
			$('#loader-publications').removeClass(HIDDEN_CLASS);
		},
		success: function (data) {
			$('#personPublications').html(data);
		},
		complete: function () {
			$('#loader-publications').addClass(HIDDEN_CLASS);
		}
	});
}

function togglePersonContent(contentType) {
	var nav = $('#personContentNav');

	if ($(nav).length ) {
		$(nav).children().each(function () {
			$(this).toggleClass('active');
		});
		$('.person-content').each(function() {
			if ($(this).data('content') === contentType) {
				$(this).removeClass(HIDDEN_CLASS);
			} else {
				$(this).addClass(HIDDEN_CLASS);
			}
		});
	}
}

