const HIDDEN_CLASS = 'hidden';
const ACTIVE_CLASS = 'active';

/**
 * on load
 */
$(function () {
	initFilterButtons('entrytype');
});

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
 * Add action to all filter buttons in a section. On click set to active and update search results correspondingly.
 *
 * @param field the section identified by the field
 */
function initFilterButtons(field) {
	$('#filter-entries-' + field + ' > button').click(function () {
		$(this).toggleClass(ACTIVE_CLASS);
	});
}