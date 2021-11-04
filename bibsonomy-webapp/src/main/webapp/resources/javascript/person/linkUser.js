/**
 * on load
 */
$(function () {
	$("#btnLinkSubmit").click(function() {
		var e = $(this);
		var text = e.text();
		e.html(text + " <i class='fa fa-spinner fa-spin'></i>");
		e.addClass('disabled');
		$.post('/editPerson',
				{
					updateOperation: 'LINK_USER',
					personId: $('.person-info').data('person')
				}
		).done(function(data) {
			$("#linkPerson").modal('hide');
			location.reload();
		});
	});
	
	$("#btnUnlinkSubmit").click(function () {
		var e = $(this);
		var text = e.text();
		e.html(text + " <i class='fa fa-spinner fa-spin'></i>");
		e.addClass('disabled');
		
		$.post('/editPerson',
				{
					updateOperation: 'UNLINK_USER',
					personId: $('.person-info').data('person')
				}
		).done(function(data) {
			$("#unlinkPerson").modal('hide');
			location.reload();
		});
	});
});