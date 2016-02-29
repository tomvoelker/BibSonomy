/**
 * Little helper JS for the admin/group page
 */
$(document).ready(function() {
	$('.declineGroupCancel').click(function() {
		$(this).closest("tr").hide();
	});	
})

function declineGroup(element) {
	nextTR = $(element).closest("tr").next();
	nextTR.show();
	$("textarea#declineMessage", nextTR).val("");
	return false;
}