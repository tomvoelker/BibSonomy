$(document).ready(function () {

	$('a[data-toggle="tab"]').on('show.bs.tab', function (e) {
		var targetElement  = $(e.target.getAttribute("href"));
		if(targetElement.html().length > 0) return; 
		
		if(e.target.getAttribute("href")=="#citation_all") {
				var url = $(e.target).data("formaturl");
				$.ajax(
						  {
							  url: url, 
							  dataType: "html", 
							  success: function(data) {
								  	targetElement
								  	.html(data)
									.find("select")
									.addClass("form-control input-sm");
									
							  }
						  }
					);
				return;
		}
			
		targetElement.html(getString("bibtex.citation_format.loading")); // activated tab
		var url = $(e.target).data("formaturl");
		$.ajax(
			  {
				  url: url, 
				  dataType: "html", 
				  success: function(data) {
					  targetElement.html(data);
				  }
			  }
		);
	});
	
	$($(".firstTab")[0]).tab("show");
	
	var URI_PREFIX = "/resources/javascript/pdf.js/web/viewer.html?file=";
	var PDFJS_FRAME_ID = "#pdfViewer";	
	
	$(".pdfFile").each(function(i, l) {
		
		$(l).click(function (e) {
			e.preventDefault();
			$(PDFJS_FRAME_ID).attr("src", URI_PREFIX+$(e.target).parent().attr("href")).parent().modal({});
			return false;
		});
	});
	
	$('.edit-document-forms .bibtexpreviewimage').first().show();

    /* DELETE BUTTON */
    
    $('.remove-btn').click(function(e){
    	e.preventDefault();
    	var url = this.getAttribute("href");
    	var parent = this.parentNode.parentNode;
    	var el = this;
    	var ident = this.getAttribute('data-ident');
    	$.ajax({
    		url: url,
    		dataType: "xml",
    		success: function(data) {
    			handleDeleteResponse({parent:parent, data: data, el: el, ident: ident});
    		},
    		error: function(data) {
    			handleDeleteResponse({parent:parent, data: data, el: el});
    		}
    	});
    	
    	return false;
    });
});

function handleDeleteResponse(o) {
	
	if(o.data.getElementsByTagName("status")[0].innerHTML=="deleted" || o.data.getElementsByTagName("status")[0].innerHTML=="ok") { 
		
		o.parent.parentNode.removeChild(o.parent); //remove edit form and buttons
		$('#'+o.ident).remove(); //remove thumbnail
		$('.bibtexpreviewimage').first().show(); //show next thumbnail
		
	} else {
		$(o.el).removeClass("btn-stripped").addClass("btn-danger").popover({
				animation: false,
				trigger: 'manual',
				delay: 0,
				title: function() {
					return getString("post.resource.remove.error.title");
				},
				content: function() {
					return getString("post.resource.remove.error");
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