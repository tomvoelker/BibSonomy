$(function() {
	var tabs = $('.citation-box a[data-toggle="tab"]')
	tabs.on('show.bs.tab', function (e) {
		var selectedTab = $(e.target);
		var targetId = selectedTab.attr("href");
		var targetElement = $(targetId);
		// skip loading if the tab has content
		if (targetElement.html().length > 0) {
			return;
		}
		
		if (targetId == "#citation_all") {
			var url = selectedTab.data("formaturl");
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
		var publicationLink = tabContainer.data('publication-url');
		
		loadExportLayout(selectedTab, targetElement, publicationLink);
	});
	
	tabs.first().tab("show");
});

