const HIDDEN_CLASS = 'hidden';
const ACTIVE_CLASS = 'active';

/**
 * on load
 */
$(function () {
	initFilterButtons('entrytype');
	loadSimilarAuthors();
	// loadPublications();
	initPublicationPagination(0);

	// ORCID formatter
	$("#editOrcid").mask("9999-9999-9999-9999");

	// Researcher ID formatter
	$("#editResearcherId").mask("\a-9999-9999");
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
		beforeSend: function () {
			$('#loader-similar').removeClass(HIDDEN_CLASS);
		},
		success: function (data) {
			$('#personSimilarAuthors').html(data);
		},
		complete: function () {
			$('#loader-similar').addClass(HIDDEN_CLASS);
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

function initPublicationPagination(page) {
	var personId = $('#personInfo').data('person');
	var sortPage = '';
	var sortPageOrder = '';
	var query = '';

	$('#personPublications').scrollPagination({
		'url': '/ajax/person/publications',  // The url you are fetching the results.
		'data': {
			// These are the variables you can pass to the request
			'requestedPersonId': personId,
			'search': query,
			'sortPage': sortPage,
			'sortPageOrder': sortPageOrder,
			'page': page, // Which page at the first time
			'pageSize': 20,
		},
		'scroller': $(window), // Who gonna scroll? default is the full window
		'autoload': true, // Change this to false if you want to load manually, default true.
		'heightOffset': 250, // It gonna request when scroll is 10 pixels before the page ends
		'loading': "#loading", // ID of loading prompt.
		'loadingText': 'click to loading more.', // Text of loading prompt.
		'loadingNomoreText': 'No more.', // No more of loading prompt.
		'manuallyText': 'click to loading more.', // Click of loading prompt.
		'before': function(){
			$("#loader-publications").removeClass(HIDDEN_CLASS);
		},
		'after': function(elementsLoaded){
			// After loading content, you can use this function to animate your new elements
			$("#loader-publications").addClass(HIDDEN_CLASS);
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

/**
 * Validates a given url string
 * @param url
 * @returns	true if the given url is valid or empty, false otherwise
 */
function isValidURL(url) {
	if (!url) {
		return true;
	}
	return /^(https?|s?ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(url);
}

/**
 * Validates a given email string
 * @param mail
 * @returns true if the given mail is valid or empty, false otherwise
 */
function isValidEMail(mail) {
	if (!mail) {
		return true;
	}

	pattuser = /^([A-Z0-9_%+\-!#$&'*\/=?^`{|}~]+\.?)*[A-Z0-9_%+\-!#$&'*\/=?^`{|}~]+$/i;
	pattdomain = /^([A-Z0-9-]+\.?)*[A-Z0-9-]+(\.[A-Z]{2,9})+$/i;

	tab = mail.split("@");
	if (tab.length != 2) {
		return false;
	}

	return (pattuser.test(tab[0]) && pattdomain.test(tab[1]));
}