/**
 * on load
 */
$(function () {
	$("#btnLinkSubmit").click(function() {
		var e = $(this);
		var text = e.text();
		var personInfo = $('.person-info');
		e.html(text + " <i class='fa fa-spinner fa-spin'></i>");
		e.addClass("disabled");
		$.post("/person/edit",
				{
					editAction: 'link',
					personId: personInfo.data('person')
				}
		).done(function(data) {
			$("#linkPerson").modal("hide");		
			location.reload();
		});
	});
	
	$("#btnUnlinkSubmit").click(function () {
		var e = $(this);
		var text = e.text();
		var personInfo = $('.person-info');
		e.html(text + " <i class='fa fa-spinner fa-spin'></i>");
		e.addClass("disabled");
		
		$.post("/person/edit",
				{
					editAction: 'unlink',
					personId: personInfo.data('person')
				}
		).done(function(data) {
			$("#unlinkPerson").modal("hide");
			location.reload();
		});
	});
});