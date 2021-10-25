/**
 * on load
 */
$(function () {
	// init content tab navigation
	initPersonNavigation();

	// init filter buttons
	initFilterButtons('entrytype');

	// init publications
	loadSimilarAuthors();
	initPublicationPagination(0);
});

/**
 * Load publications of similar authors.
 */
function loadSimilarAuthors() {
	var personId = $('#personInfo').data('person');

	$('#personSimilarAuthors').empty();

	$.ajax({
		url: '/ajax/person/similar', // The url you are fetching the results.
		data: {
			// These are the variables you can pass to the request
			'requestedPersonId': personId
		},
		success: function (data) {
			$('#personSimilarAuthors').html(data);
		},
		'before': function(){
			$("#loader-similar").show(0);
		},
		'after': function(){
			$("#loader-similar").hide(0);
		}
	});
}

function initPublicationPagination(page) {
	var personId = $('#personInfo').data('person');
	var sortPage = '';
	var sortPageOrder = '';
	var query = generateFilterQuery();

	var container = $('#personPublicationsContainer');
	container.empty();
	$('<div>', {
		id: 'personPublications',
	}).appendTo(container);

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
			$("#loader-publications").show(0);
		},
		'after': function(){
			$("#loader-publications").hide(0);
		}
	});
}

function initPersonNavigation() {
	$('.tab-link').click(function () {
		var contentType = $(this).data('tab');
		$('#personContentNav li').each(function() {
			if ($(this).data('tab') === contentType) {
				$(this).addClass('active');
			} else {
				$(this).removeClass('active');
			}
		});

		$('.person-content').each(function() {
			if ($(this).data('content') === contentType) {
				$(this).removeClass('hidden');
			} else {
				$(this).addClass('hidden');
			}
		});
	});
}

/**
 * Add action to all filter buttons in a section. On click set to active and update search results correspondingly.
 *
 * @param field the section identified by the field
 */
function initFilterButtons(field) {
	$('#filter-entries-' + field + ' > button').click(function () {
		$(this).toggleClass('active');
		updateResults(0);
	});
}