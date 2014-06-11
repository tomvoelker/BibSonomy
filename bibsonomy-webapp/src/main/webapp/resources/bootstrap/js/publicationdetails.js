$(document).ready(function () {
	$('a[data-toggle="tab"]').on('show.bs.tab', function (e) {
		var targetElement  = $(e.target.getAttribute("href"));
		if(targetElement.html().length > 0) return; 
		
		if(e.target.getAttribute("href")=="#citation_all") {
				$.ajax(
						  {
							  url: "/exportLayouts", 
							  dataType: "json", 
							  success: function(data) {

									var newElement = $("select").addClass("form-control input-sm");
									targetElement.append(newElement);
									data.layouts.forEach(function(value) {
										addLayoutElement($(newElement), value)
									});
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
						  $(e.target.getAttribute("href")).html(data);
					  }
				  }
			);
	});
	
	var URI_PREFIX = "resources/javascript/pdf.js/web/viewer.html?file=";
	var PDFJS_FRAME_ID = "#pdfViewer";	
	
	$(".pdfFile").each(function(i, l) {
		
		$(l).click(function (e) {
			$(PDFJS_FRAME_ID).attr("src", URI_PREFIX+$(e.target).parent().attr("href"));
			
			e.preventDefault();
			
	        BootstrapDialog.show({
	            size: BootstrapDialog.SIZE_LARGE,
	            message: $(PDFJS_FRAME_ID),
	            
	        });
		});
	})
});

function addLayoutElement(element, value) {
	var option = document.createElement("option");
	option.setAttribute("value", value.baseFileName);
	option.innerHTML = value.displayName;
	element.append($(option));
}