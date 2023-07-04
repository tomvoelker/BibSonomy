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

	// init sorting options
	initSortOptions('sorting-dropdown-menu', initPublicationPagination);
	initSortOptions('sorting-dropdown-menu-similar', loadSimilarAuthors);
});

/**
 * Load publications of similar authors.
 */
function loadSimilarAuthors(page) {
	var personId = $('.person-info').data('person');
	var selectedSort = $('#sorting-dropdown-menu-similar > .sort-selected');
	var sortPage = selectedSort.data('key');
	var sortPageOrder = selectedSort.data('asc') ? 'asc' : 'desc';

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
			$("#similarAuthorsLoader").show(0);
		},
		'after': function(){
			$("#similarAuthorsLoader").hide(0);
		}
	});
}

function initPublicationPagination(page) {
	var personId = $('.person-info').data('person');
	var query = generateFilterQuery();
	var selectedSort = $('#sorting-dropdown-menu > .sort-selected');
	var sortPage = selectedSort.data('key');
	var sortPageOrder = selectedSort.data('asc') ? 'asc' : 'desc';

	var container = $('#personPublicationsContainer');

	// disable previous pagination, when available
	var oldPagination = $('#personPublications');
	if (oldPagination.length > 0) {
		oldPagination.attr('scrollPagination', 'disabled');
	}

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
			'size': 1000, // Number of pages FIXME: controller should get the actual number after fixing the controller
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
			$("#publicationsLoader").show(0);
		},
		'after': function(){
			$("#publicationsLoader").hide(0);
			//$('#personPublications').attr("scrollpagination", "enabled");
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

		$('.person-tab-content').each(function() {
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
		initPublicationPagination(0);
	});
}

/**
 * Send a custom report via e-mail, if system is properly configured.
 * The e-mail will contain the custom message of the reporter.
 */
function reportPublicationCustom(publication) {
	var interhash = $(publication).data("interhash");
	var form = $("#formReportPublication-" + interhash);

	var formData = form.serializeArray();
	formData.map(function(x){formData[x.name] = x.value;});

	$.ajax({
		url: "/ajax/report/person/publications/custom",
		data: {
			'personId': formData.personId,
			'title': formData.title,
			'interhash': formData.interhash,
			'message': formData.message,
			'referer': window.location.href,
		},
		success: function(data) {
			if (data.success === true) {
				var successMsg = getString("report.error.feedback.success");
				showAlert('success', successMsg);
			} else {
				var errorMsg = getString("report.error.feedback.error");
				showAlert('danger', errorMsg);
			}
			// Close modal
			$('#reportModal-' + formData.interhash).modal('hide');
		}
	});
}

function reportPublicationDuplicate(publication) {
	$.ajax({
		url: "/ajax/report/person/publications/duplicate",
		data: {
			'personId': $(publication).data('person'),
			'title': $(publication).data('title'),
			'interhash': $(publication).data('interhash'),
			'referer': window.location.href,
		},
		success: function(data) {
			if (data.success === true) {
				var successMsg = getString("report.error.feedback.success");
				showAlert('success', successMsg);
			} else {
				var errorMsg = getString("report.error.feedback.error");
				showAlert('danger', errorMsg);
			}
		}
	});
}

function showAlert(type, message) {
	var alert = $('<div></div>')
		.attr('class', 'alert alert-dismissible alert-' + type)
		.attr('role', 'alert');

	var closeBtn = $('<button></button>')
		.attr('class', 'close')
		.attr('data-dismiss', 'alert')
		.html('<span aria-hidden="true">&times;</span>');

	alert.append(closeBtn);
	if (type === 'danger') {
		alert.append($('<strong></strong>').html(getString("error") + ': '));
	}
	alert.append(message);

	$('#ajaxAlerts').append(alert);
}