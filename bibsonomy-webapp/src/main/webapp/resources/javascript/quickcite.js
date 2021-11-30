/**
 * Loads the select2 combobox layout selection
 * @param formatUrl
 * @param element
 * @returns
 */
function loadLayoutSelect(formatUrl, element) {
    $.ajax({
        url: formatUrl,
        dataType: "html",
        success: function(data) {
            $("#quick-cite-select").html(data).find("select").addClass("form-control input-sm");
            $("#quick-cite-select-dummy").hide();
            // hide the loader
            $(".cust-loader").hide();
            openSelect2(element);
        }
    });
    return;
}

/**
 * Opens the select2 element
 * @param element
 * @returns
 */
function openSelect2(element) {
    if ($(element).next().find('#selectAllStyles').hasClass("select2-hidden-accessible")) {
        $(element).next().find('#selectAllStyles').removeAttr("onchange")
            .bind("change", function(){ ajaxLoadLayout(this.value); });
        $(element).next().find('#selectAllStyles').select2('open');
    }
}

function openModalWithBibTex() {
    $("#sidebar-quick-cite-box-modal #sidebar-quick-cite-box-modal-textarea").html($("#sidebar-quick-cite-box-bibtex").html());
    $("#sidebar-quick-cite-box-modal").modal("show");
}

function openModalWithEndnote() {
    $("#sidebar-quick-cite-box-modal #sidebar-quick-cite-box-modal-textarea").html($("#sidebar-quick-cite-box-endnote").html());
    $("#sidebar-quick-cite-box-modal").modal("show");
}

function ajaxLoadLayout(url) {
	url_parts = url.split("/");
    container = $("#sidebar-quick-cite-box-modal #sidebar-quick-cite-box-modal-textarea");
    container.empty();

	switch (url_parts[1]) {
		case "bib":
			openModalWithBibTex();
			break;
		case "csl":
		case "layout":
			if (url_parts[2] == "endnote") {
				openModalWithEndnote();
			} else {
				container = $("#sidebar-quick-cite-box-modal #sidebar-quick-cite-box-modal-textarea");
                container.empty();

                $.ajax({
                    url: url,
                    success: function(data) {
                        $("#sidebar-quick-cite-box-modal #sidebar-quick-cite-box-modal-textarea").html(data);
                        $("#sidebar-quick-cite-box-modal").modal("show");
                    }
                });
			}
			break;
		case "csl-layout":
			// load CSL via AJAX
			csl_style = url_parts[2];
			csl_url = "/csl/bibtex/" + url_parts[4];
			container = $("#sidebar-quick-cite-box-modal #sidebar-quick-cite-box-modal-textarea");
			container.empty();

			$.ajax({
				url: csl_url,
				success: function(data) {
					renderCSL(data, csl_style, container, false);
					$("#sidebar-quick-cite-box-modal").modal("show");
				}
			});
			break;
		default:
			alert("Error during CSL rendering;");
	}
}

$(document).ready(function() {


    // init clipboard for modal
    initNewClipboard("#sidebar-quick-cite-box-modal-clipboard-button", "#sidebar-quick-cite-box-modal #sidebar-quick-cite-box-modal-textarea");

	// remove the dummy element and replace it by select2 combobox layout selection
	$("#quick-cite-select-dummy").click(function() {
		// show the loader
		$(".cust-loader").show();
		$(".select2-fake-placeholder").hide();
		loadLayoutSelect($(this).data("formaturl"), this);
	})
})