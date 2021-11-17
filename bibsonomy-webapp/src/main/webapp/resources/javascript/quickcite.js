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
    $("#sidebar-quick-cite-box-modal .modal-body").html($("#sidebar-quick-cite-box-bibtex").html());
    $("#sidebar-quick-cite-box-modal").modal("show");
}

function openModalWithEndnote() {
    $("#sidebar-quick-cite-box-modal .modal-body").html($("#sidebar-quick-cite-box-endnote").html());
    $("#sidebar-quick-cite-box-modal").modal("show");
}

// /layout/apa_html/bibtex/27befbc559cad029f1c2ac2534c0adc09/tester01?formatEmbedded=true&items=1

http://puma.dd/layout/apa_html/bibtex/27befbc559cad029f1c2ac2534c0adc09/tester01?formatEmbedded=true&items=1

/*
function openModalWithStyle(intrahash, style) {
    csl_url = "/csl/bibtex/" + csl_style;
    csl_url = "/layout/" + style + "/bibtex/" + intrahash + "?formatEmbedded=true&items=1";
    container = $("#sidebar-quick-cite-box-modal .modal-body");
    container.empty();

    $.ajax({
        url: csl_url,
        success: function(data) {
            renderCSL(data, csl_style, container, false);
            $("#sidebar-quick-cite-box-modal").modal("show");
        }
    });
}
*/

function openModalWithStyle(url) {
    container = $("#sidebar-quick-cite-box-modal .modal-body");
    container.empty();

    $.ajax({
        url: url,
        success: function(data) {
            $("#sidebar-quick-cite-box-modal .modal-body").html(data);
            $("#sidebar-quick-cite-box-modal").modal("show");
        }
    });
}


function ajaxLoadLayout(link) {
	link_parts = link.split("/");

	switch (link_parts[1]) {
		case "bib":
			openModalWithBibTex();
			break;
		case "csl":
		case "layout":
			if (link_parts[2] == "endnote") {
				openModalWithEndnote();
			} else {
			alert(link)
				container = $("#sidebar-quick-cite-box-modal .modal-body");
                container.empty();

                $.ajax({
                    url: link,
                    success: function(data) {
                        $("#sidebar-quick-cite-box-modal .modal-body").html(data);
                        $("#sidebar-quick-cite-box-modal").modal("show");
                    }
                });
			}
			break;
		case "csl-layout":
			// load CSL via AJAX
			csl_style = link_parts[2];
			csl_url = "/csl/bibtex/" + link_parts[4];
			container = $("#sidebar-quick-cite-box-modal .modal-body");
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
	// remove the dummy element and replace it by select2 combobox layout selection
	$("#quick-cite-select-dummy").focus(function() {
		// show the loader
		$(".cust-loader").show();
		loadLayoutSelect($(this).data("formaturl"), this);
	})
})