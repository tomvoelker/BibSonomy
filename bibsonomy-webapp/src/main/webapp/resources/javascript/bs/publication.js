$(function() {
	var tabs = $('.citation-box a[data-toggle="tab"]')
	tabs.on('show.bs.tab', function (e) {
		var targetElement  = $(e.target.getAttribute("href"));
		if (targetElement.html().length > 0) return; 
		
		if (e.target.getAttribute("href")=="#citation_all") {
				var url = $(e.target).data("formaturl");
				$.ajax({
					url: url, 
					dataType: "html", 
					success: function(data) {
								targetElement.html(data).find("select").addClass("form-control input-sm");
							}
				});
			return;
		}
			
		targetElement.html(getString("bibtex.citation_format.loading")); // activated tab
		var url = $(e.target).data("formaturl");
		$.ajax({
			url: url, 
			dataType: "html", 
			success: function(data) {
						targetElement.html(data);
			}
		});
	});
	
	tabs.first().tab("show");
});