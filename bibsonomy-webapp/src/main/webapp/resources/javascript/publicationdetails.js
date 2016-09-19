$(document).ready(function () {
	var URI_PREFIX = "/resources/javascript/pdf.js/web/viewer.html?file=";
	var PDFJS_FRAME_ID = "#pdfViewer";
	
	/*
	$(".pdfFile").each(function(i, l) {
		
		$(l).click(function (e) {
			e.preventDefault();
			$(PDFJS_FRAME_ID).attr("src", URI_PREFIX+$(e.target).parent().attr("href")).parent().modal({});
			return false;
		});
	});
	*/
	
	var docInfo = $('#document-info .item');
	docInfo.hide();
	docInfo.first().show();
	var docCarousel = $('#carousel-documents');
	
	if (docCarousel.find('ol.carousel-indicators li').length == 0) {
		docCarousel.hide();
		$('#document-info').hide();
	} else {
		$('#add-document').hide();
		docCarousel.on('slid.bs.carousel', function () {
			var currentIndex = $('#carousel-documents div.active').index();
			docInfo.hide();
			$(docInfo[currentIndex]).show();
		});
	}
	

	/* DELETE BUTTON */
	$('.remove-btn').click(function() {
		var url = $(this).attr("href");
		var parent = $(this).parent().parent().parent();
		var el = $(this);
		
		var container = $(this).closest('.row');
		var index = container.data('index');
		$.ajax({
			url: url,
			dataType: "xml",
			success: function(data) {
				if ($(data).find('status').text() == "deleted") {
					container.remove();
					
					// show the next item
					$('#carousel-documents').carousel('next');
					
					// remove item from carousel
					$('#carousel-documents .carousel-inner > .item:eq(' + index + ')').remove();
					$('#carousel-documents ol.carousel-indicators > li:eq(' + index + ')').remove();
				}
			}
		});
		
		return false;
	});
});

function handleDeleteResponse(o) {
	
	if (o.data.getElementsByTagName("status")[0].innerHTML=="deleted" || o.data.getElementsByTagName("status")[0].innerHTML=="ok") { 
		
		o.parent.parentNode.removeChild(o.parent); //remove edit form and buttons
		$('#'+o.ident).remove(); //remove thumbnail
		$('.bibtexpreviewimage').first().show(); //show next thumbnail
		
	} else {
		$(o.el).removeClass("btn-stripped").addClass("btn-danger").popover({
				animation: false,
				trigger: 'manual',
				delay: 0,
				title: function() {
					return getString("post.resource.document.remove.error.title");
				},
				content: function() {
					return getString("post.resource.document.remove.error");
				}
		}).popover("show");
	}
}

function privNoteCallback(o) {
	var form = o.parent.children(".form-group");
	if(!form.hasClass(o.stateClass)) form.addClass(o.stateClass);
	if(o.error!==undefined) alert("error: " + o.error);
} 

function updatePrivNote() {
	var textArea = $("#private-note");
	var newVal = textArea.val();
	var oldVal = $("#old-private-note").val();
	var parent = $("#note");
	
	if (newVal == oldVal) {
		textArea.css('background-color', '#D8EBAE').animate({backgroundColor : '#ffffff'}, 1000);
		return false;
	}
	
	$.ajax({
		url : "/ajax/updateprivatenote",
		data : parent.serialize(),
		dataType: "text",
		success : function(data, textStatus, jqXHR) {
			privNoteCallback({stateClass:"has-success", parent:parent});
		},
		error : function(jqXHR, textStatus, errorThrown) {
			privNoteCallback({stateClass:"has-error", parent:parent, error:errorThrown});
		}
	});
	return false;
}