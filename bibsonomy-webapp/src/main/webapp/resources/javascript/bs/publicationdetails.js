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
	})
});

function updatePrivNote() {
	var textArea = $("#private-note");
	var newVal = textArea.val();
	var oldVal = $("#old-private-note").val();
	if (newVal == oldVal) {
		textArea.css('background-color', '#D8EBAE').animate({backgroundColor : '#ffffff'}, 1000);
		return false;
	}
	var formData = $("#note").serialize();
	$.ajax({
		url : "/ajax/updateprivatenote",
		data : formData,
		dataType: "text",
		success : function(data, textStatus, jqXHR) {
			textArea.css('background-color', '#D8EBAE').animate({backgroundColor : '#ffffff'}, 1000);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert("error: " + errorThrown);
		}
	});
	return false;
}