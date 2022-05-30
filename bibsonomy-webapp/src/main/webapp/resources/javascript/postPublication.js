$(function () {
    // buttons
    $('#publication-functions .btn-group .btn[data-target]').click(function () {
        var id = "#publication-" + $(this).data('target');
        if ($(this).hasClass('active')) {
            $(id).hide();
            $(this).removeClass('active');
        } else {
            $(this).siblings().removeClass('active');
            $(this).addClass('active');
            $('.publication-function').hide();
            $(id).show();
            $.scrollTo($('#publication-search'));
        }
    });

    // init title autocomplete
    var searchInput = $('#publication-search input');
    if (searchInput.length > 0) {
        var publicationSource = new Bloodhound({
            datumTokenizer: function (datum) {
                return Bloodhound.tokenizers.whitespace(datum.value);
            },
            queryTokenizer: Bloodhound.tokenizers.whitespace,
            remote: {
                url: '/json/ajax/autocomplete?search=%QUERY&resourceType=publication',
                filter: function (data) {
                    return $.map(data.items, function (publication) {
                        return {
                            value: publication.label,
                            intrahash: publication.intraHash,
                            authors: publication.authors,
                            user: publication.user,
                            year: publication.year
                        };
                    });
                }
            }
        });

        publicationSource.initialize();

        var searchText = getString('post_publication.search.loading');
        var notFoundText = getString('post_publication.search.notfound');
        searchInput.typeahead({
            highlight: true,
            minLength: 1
        }, {
            displayKey: 'value',
            source: publicationSource.ttAdapter(),
            templates: {
                notFound: '<p class="tt-suggestion" id="tt-notfound">' + notFoundText + '</p>',
                pending: '<p class="tt-suggestion tt-loading">' + searchText + '</p>',
                suggestion: Handlebars.compile("<p>{{value}} ({{year}})<br /><span class='author text-muted'>{{#each authors}}{{first}} {{last}}{{#unless @last}}, {{/unless}}{{/each}}</span></p>"),
            }
        });

        // to e.g. keep isbns to pass them to the edit post controller
        var originalSearch;
        searchInput.bind('typeahead:select', function (evt, data) {
            var hash = data.intrahash;

            if (hash == "") {
                window.location.href = '/editPublication?selection=' + originalSearch;
                return;
            }

            window.location.href = '/editPublication?hash=' + data.intrahash + "&user=" + data.user;
        }).bind('typeahead:asyncreceive', function () {
            originalSearch = searchInput.val();
        }).bind('typeahead:render', function (evt, suggestion) {
            $('#tt-notfound').click(function () {
                var input = searchInput.val();
                showManualForm(input);
            });
        });

        // handle person add function from /persons page
        if ($('#personIndex').length > 0) {
            showManualForm('');
        } else {
            var anchor = window.location.hash.substr(1);
            if (anchor.length > 0) {
                $('button[data-target=' + anchor + ']').click();
                $.scrollTo($('publication-functions'));
            } else {
                searchInput.focus();
            }
        }
    }

    // whitespace substitute
    $('#whitespace').attr('disabled', 1);

    $('#delimiter').change(function () {
        $('#whitespace').attr('disabled', $(this).val() == " ");
    });

	if(window.location.href.substring(window.location.href.lastIndexOf('#') + 1) === 'bibtexPost') {
		$('#bibtextButton').addClass('active');
		document.getElementById('publication-snippet').style.display = "block";
	}

    /*
    Edit mask for ORCID import
    The last digit of the ORCID iD is the checksum and allows an X, hence the asterisk in the mask.
    For more information: https://support.orcid.org/hc/en-us/articles/360006897674-Structure-of-the-ORCID-Identifier
     */
    var orcidInput = $("#orcidId");
    if ($(orcidInput).length > 0) {
        $(orcidInput).mask("9999-9999-9999-999*");
    }

    // FIXME by update our JS message plugin
    var connectionMsg = getString('post_bibtex.orcid.action.error.connection');
    var invalidIdMsg = getString('post_bibtex.orcid.action.error.invalidId');
    var noWorksMsg = getString('post_bibtex.orcid.action.error.noWorks');
});

function showManualForm(titleText) {
    var manualButton = $('.btn[data-target=manual]');
    manualButton.click();
    $('#post\\.resource\\.title').focus().val(titleText);
}

function setOwnOrcidID() {
    var ownOrcid = $('#ownOrcidID').val();
    var parts = ownOrcid.match(/.{1,4}/g);
    $('#orcidId').val(parts.join('-'));
}

var orcidRequest = null;
var orcidRequestRunning = false;
var orcidCancelAllowed = true;

function importOrcidPublications() {
    enableCancelOrcidImport();

    var orcidId = $('#orcidId').val();

    orcidRequest = $.ajax({
        url: '/ajax/orcid', // The url you are fetching the results.
        data: {
            // These are the variables you can pass to the request
            'orcidId': orcidId
        },
        success: function (data) {
            if (data.success) {
                // Check, if works are available
                var responseData = JSON.parse(data.message);
                if (responseData['group'].length < 1) {
                    showOrcidError('post_bibtex.orcid.action.error.noWorks');
                    $("#orcidImportLoader").hide(0);
                } else {
                    // Set form data, get work details and submit
                    getWorkDetailsAndSubmit(responseData);
                }
            } else {
                showOrcidError(data.error);
                $("#orcidImportLoader").hide(0);
            }
        },
        beforeSend: function () {
            $("#orcidImportLoader").show(0);
        },
        error: function() {
            $("#orcidImportLoader").hide(0);
        }
    });
}

function getWorkDetailsAndSubmit(data) {
    var orcidId = $('#orcidId').val();
    var workIds = [];
    var works = data["group"];

    works.forEach(function (work) {
        var summary = work["work-summary"][0];
        workIds.push(summary["put-code"]);
    });

    var size = workIds.length;

    // Making 1 request for every 100 works
    var done = Math.ceil(size / 100); // number of total requests
    var result = [];

    /* Normal loops don't create a new scope */
    var numOfRequest = Array.from(Array(done).keys()); // if we have to send 3 requests the result would be [0, 1, 2]
    $(numOfRequest).each(function() {
        if (orcidRequestRunning) {
            var number = this;
            var workIdsForBulk = workIds.slice(number * 100, (number + 1) * 100).join(',');
            orcidRequest = $.ajax({
                url: '/ajax/orcid', // The url you are fetching the results.
                data: {
                    // These are the variables you can pass to the request
                    'orcidId': orcidId,
                    'workIds': workIdsForBulk
                },
                success: function (data) {
                    if (data.success) {
                        var responseData = JSON.parse(data.message);
                        responseData['bulk'].forEach(function (obj) {
                            result.push(JSON.stringify(obj['work']));
                        });
                    } else {
                        showOrcidError(data.error);
                    }
                },
                complete: function () {
                    done--;
                    if (done === 0) {
                        disableCancelOrcidImport();
                        submitOrcidImport(orcidId, workIds, result);
                    }
                },
                error: function () {
                    $("#orcidImportLoader").hide(0);
                }
            });
        }
    });
}

function submitOrcidImport(orcidId, workIds, works) {
    // Set form data
    $('#externalId').val(orcidId);
    $('#workIds').val(workIds);
    $('#bulkSnippet').val('{"orcid": [' + works.join(',') + ']}');
    // Submit form
    $('#orcidImportForm').submit();
}

function showOrcidError(errorMsg) {
    var alert = $('<div class="alert alert-danger alert-dismissable"></div>');
    var button = $('<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>');
    alert.append(button);
    alert.append($('<strong>Error: </strong>'));
    alert.append(getString(errorMsg));

    $('#orcidImportFormError').append(alert);
}

function cancelOrcidImport() {
    if (orcidCancelAllowed) {
        orcidRequestRunning = false;

        if (orcidRequest != null) {
            orcidRequest.abort();
        }

        $("#orcidImportLoader").hide(0);
    }
}

function enableCancelOrcidImport() {
    orcidCancelAllowed = true;
    orcidRequestRunning = true;
    $("#orcidImportCancelBtn").show(0);
}

function disableCancelOrcidImport() {
    orcidCancelAllowed = false;
    orcidRequestRunning = false;
    $("#orcidImportCancelBtn").hide(0);
}