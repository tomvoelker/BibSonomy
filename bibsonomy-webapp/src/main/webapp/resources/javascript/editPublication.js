//methods for editPublication page
//setup jQuery to update recommender with form data

var MISC_EXPERT_BTN = '#miscExpertMode';
var MISC_ADD_BTN = '#addMiscFieldButton';
var MISC_EXTRA_FIELD = '#extraFieldsWrap';
var MISC_RAW_EXPERT = '#post\\.resource\\.misc';

var tagRecoOptions = {
    type: "POST",
    url: '/ajax/getPublicationRecommendedTags',
    data: $('#postForm').serialize(),
    dataType: "json",
    success: function showResponse(responseText, statusText) {
        handleRecommendedTags(responseText);
    }
};

var getFriends = null;

var getGroups = null;

function toggleView() {
    var collapse = $("#collapse");
    var showAll = collapse.data("showAll");
    collapse.data("showAll", !showAll);
    changeView(collapse.data("showAll"));
}

/* update view when user selects another type of publication in list */
function changeView(showAll) {
    var requiredFields = requiredForType[$("#post\\.resource\\.entrytype").val()];
    var message = getString('post.resource.fields.detailed.show.all');
    var noRequiredFields = (requiredFields === undefined);
    var collapse = $('#collapse');

    if (showAll || noRequiredFields) {
        requiredFields = fields;
        if (noRequiredFields) {
            collapse.parent().addClass("hidden");
        } else {
            message = getString('post.resource.fields.detailed.show.required');
            collapse.parent().removeClass("hidden");
        }
    } else {
        collapse.parent().removeClass("hidden");
    }

    collapse.text(message);

    for (var i = 0; i < fields.length; i++) {
        var bibtexField = fields[i];
        var field = $("#post\\.resource\\." + bibtexField);
        var parent = field.closest(".form-group");
        if (showAll || in_array(requiredFields, bibtexField)) {
            parent.show();
        } else {
            if (!field.val()) { //fix: don't hide not empty fields
                parent.hide();
            }
        }
    }

    if (showAll || in_array(requiredFields, "misc")) {
        $(MISC_EXPERT_BTN).show();
        $(MISC_ADD_BTN).show();
        $(MISC_EXTRA_FIELD).show();
    } else {
        var hasMiscField = false;
        $.each(requiredFields, function (index, item) {
            if (item.startsWith("misc.")) {
                $("#miscDiv").show();
                $("#post\\.resource\\.misc").closest(".form-group").show();
                hasMiscField = true;
                //ends the function
                return false;
            }
        });

        $.each($(".extraInputs"), function () {
            var hasValue = false;
            $(this).find("input[type='text']").each(function () {
                if ($(this).val()) {
                    hasValue = true;
                    hasMiscField = true;
                    return false;
                }
            });

            if (!hasValue) {
                $(this).remove();
            }
        });

        if (!miscFieldHasError()) {
            // don't show misc field gui, only show the required misc fields
            $(MISC_EXPERT_BTN).hide();
            $(MISC_ADD_BTN).hide();
            $(MISC_EXTRA_FIELD).hide();
        }
    }
}

function miscFieldHasError() {
    return $(MISC_RAW_EXPERT).closest('.form-group').hasClass('has-error');
}

/* checks if element is member of given array */
function in_array(array, element) {
    for (var j = 0; j < array.length; j++) {
        if (array[j] == element) {
            return true;
        }
    }
    return false;
}

/* checks if element is member of given array in lowerCase*/
function in_array_lower(array, element) {
    if (typeof array != "undefined") {
        for (var j = 0; j < array.length; j++) {
            if (array[j].toLowerCase() == element) {
                return true;
            }
        }
    }
    return false;
}

function generateBibTexKey(obj) {
    var buffer = "";

    /* get author */
    buffer += getFirstPersonsLastName($("#post\\.resource\\.author").val());

    /* the year */
    var year = $("#post\\.resource\\.year").val();
    if (year != null) {
        buffer += year.trim();
    }

    /* first relevant word of the title */
    var title = $("#post\\.resource\\.title").val();
    if (title != null) {
        buffer += getFirstRelevantWord(title).toLowerCase();
    }

    if (buffer.length === 0) {
        window.alert(getString("error.field.valid.bibtexKey.generation"));
    } else {
        $("#post\\.resource\\.bibtexKey").val(buffer.toLowerCase());
    }
}

function getFirstPersonsLastName(person) {
    if (person != null) {
        var firstauthor;
        /*
         * check, if there is more than one author
         */
        var firstand = person.indexOf("\n");
        if (firstand < 0) {
            firstauthor = person;
        } else {
            firstauthor = person.substring(0, firstand);
        }
        /*
         * first author extracted, get its last name
         */
        var comma = firstauthor.search(/,/);
        var lastname;
        if (comma < 0) {
            // no comma found - find last space
            var lastspace = firstauthor.search(/\s\S+$/);
            if (lastspace < 0) {
                lastname = firstauthor;
            } else {
                lastname = firstauthor.substring(lastspace + 1, firstauthor.length);
            }
        } else {
            lastname = firstauthor.substring(0, comma);
        }
        return lastname.trim().replace(/[^0-9A-Za-z]+/g, "");
    }
    return "";
}

function getFirstRelevantWord(title) {
    var split = title.split(" ");
    for (i in split) {
        var regex = new RegExp("[^a-zA-Z0-9]", "g");
        ss = split[i].replace(regex, "");
        if (ss.length > 4) {
            return ss;
        }
    }
    return "";
}

function activateAffixEntry(el) {
    $(el).addClass("active").siblings().each(function (h, g) {
        $(g).removeClass("active");
    });
}

/**
 * provide each input field with suggestions from multiple post values passed as json data
 *
 * @param json - posts as json data
 * @return
 */
function buildGoodPostSuggestion(json) {
    /* array of indices to sort */
    var sortIndices = function (j) {
        var h = new Array();
        while (j.length > h.length)
            h.push(h.length);
        for (i = 0; i < j.length; i++) {
            for (k = i + 1; k < j.length; k++)
                if (j[h[i]] < j[h[k]]) {
                    h[i] = h[i] + h[k];
                    h[k] = h[i] - h[k];
                    h[i] = h[i] - h[k];
                }
        }
        return h;
    };
    /*
     * constants
     */
    var inputFields = $("textarea, #postForm > textarea, input");
    var postResource = "post.resource.";
    var inputFieldMap = new Object();
    inputFieldMap["post.resource.title"] = "label";
    inputFieldMap["post.description"] = "description";
    var u = /(\s+|,)/g; // regex to remove whitespace and commas
    var v = /\s+$/; // regex to remove trailing whitespace

    var arrayOfPartialTags = new Array();

    /*
     * loop over all input fields
     */
    for (var x = 0; inputFields.length > x; x++) {
        var fieldValue = new Array();
        var inputField = $(inputFields[x]);
        var inputFieldName = inputField[0].name;

        /*
         * field name starts with "post.resource" or it appears in the inputFieldMap
         */
        if (inputFieldName.substring(0, postResource.length) == postResource || inputFieldMap[inputFieldName] != undefined) {
            var g = inputField[0].value.replace(u, "");
            var suggestions = new Array();
            var occurrences = new Array();
            var k = -1;
            /*
             * loop over posts
             */
            var fieldVal;

            for (var z = 0; json.items.length > z; z++) {
                var post = json.items[z];
                if (((fieldVal = post[inputFieldName.substring(postResource.length, inputFieldName.length)]) != undefined
                    || (fieldVal = post[inputFieldMap[inputFieldName]])) && fieldVal.length > 0) {
                    var name = "";
                    if (typeof fieldVal == "object") {
                        var delimiter = " ";
                        /*
                         * special handling for person names:
                         * - join them using ", "
                         * - prepend the prefix (last name) to our first name chain if present
                         */
                        if (inputFieldName == postResource + "author" || inputFieldName == postResource + "editor") {
                            delimiter = ', ';
                            var namePair = post[inputFieldName.replace(postResource, '') + "s"];
                            for (var m = 0; m < namePair.length; m++) {
                                name += ((namePair[m].last.length > 0) ? namePair[m].last + ((namePair[m].first.length > 0) ? ", " : "") : "") + namePair[m].first + "\n";
                            }
                        }
                        fieldVal = fieldVal.join(delimiter);
                    }
                    /* if a term has not been acquired yet,
                    add a occurrence of 1 to our occurrence array and add the term to our suggestion array */
                    if ((k = $.inArray(fieldVal, suggestions)) == -1) {
                        suggestions.push(fieldVal); // add term to suggestions
                        occurrences.push(1); // add initial occurrence count of 1
                        /* if we encounter author or editor suggestions,
                         * label and field value are not the same, so we have to differentiate between
                         * field value and label by providing two different sets of values 							*/
                        if (name.length) // if name has a length of > 0 the current input field is an author or editor field
                            fieldValue.push(name);
                    } else if (k > -1) { // if true we already have this term as a suggestion, so we just have to increment the occurrence of the term
                        occurrences[k]++;
                        k = -1;
                    }
                }
            } // loop over posts
            /*
             * no suggestions or the suggestion count is 1 AND field value is the same as the suggestion value
             * - skip in both cases
             */
            if (!suggestions.length || fieldVal === undefined || (suggestions.length == 1
                && g == ((name.length) ? name.replace(u, "") : fieldVal.replace(u, "")))) continue;

            inputField.tooltip({
                trigger: 'focus',
                placement: 'top',
                title: getString('post.resource.suggestion.hint')
            }).tooltip('show');

            inputField.after('<span class="autocompletion fa fa-caret-down form-control-feedback"><!-- --></span>');
            inputField.popover('destroy');  //remove popover help


            /* we have a bijective mapping therefore (f:suggestion->occurrence) we sort our indices by descending order */
            var indices = sortIndices(occurrences);
            /* occurrences are sorted and aligned to the corresponding suggestions */
            /* if fieldvalue contains values, it is a different set of values we apply to the value label - this is true for the author and editor fields */
            var labels = $.map(suggestions, function (item, index) {
                var suggestion = suggestions[indices[index]];
                return {
                    label: suggestion + " (" + occurrences[indices[index]] + ")",
                    value: ((fieldValue.length == 0) ? suggestion : fieldValue[indices[index]])
                };
            });
            inputField.autocomplete(
                {
                    source: labels,
                    minLength: 0,
                    delay: 0
                }
            ).bind("focus",
                function () {
                    $(this).autocomplete("search", "");
                }
            );
            applyKeyDownHandler(inputField);
        }
    } // loop over input fields
}

/* ugly hack to keep the up and down key working if no suggestions are shown */
function applyKeyDownHandler(element) {
    var widget = element.autocomplete("widget");
    var keyHandler = function (e) {
        var p = widget.hasClass("ui-autocomplete-disabled");
        if ((e.keyCode == 38 || e.keyCode == 40)
            && !p
            && widget[0].style.display == 'none') {
            element.autocomplete("disable");
        } else if (p)
            element.autocomplete("enable");
    };
    element
        .bind("autocompleteopen", function (event, ui) {
            widget.prepend("<li class='fsInputRecoHelp'>" + getString("post.resource.suggestion.help") + "</li>");
        })
        .bind("keydown", function (e) {
            keyHandler(e);
        });
}

// adds the send:<username> tag
function addForFriendTag(username) {
    var forFriendTag = "send:" + username;
    copyTag("inpf_tags", forFriendTag);
}

// adds the for:<groupname> tag
function addForGroupTag(groupname) {
    var forGroupTag = "for:" + groupname;
    copyTag("inpf_tags", forGroupTag);
}

function addOrUpdateStandardMiscInput(fieldName, fieldValue) {
    var labelKey = "post.resource.misc." + fieldName;
    var name = getString(labelKey);
    if (name.startsWith("???")) {
        name = fieldName;
    }

    // Check, if field input already exists and update it if it does
    var fieldInput = $("#standardFieldsWrap :input[type=text][name='" + name + "']");

    if (fieldInput.length > 0) {
        $(fieldInput).val(fieldValue);
    } else {
        $("#standardFieldsWrap").append('<div class="standardInputs form-group">' +
            '<label for="post.resource.' + fieldName + '" class="col-sm-3 control-label">' + name + '</label>' +
            '<div class="col-sm-9"><input id="post.resource.' + fieldName + '" name="' + name + '" value="' + fieldValue + '" class="form-control" type="text"/>' +
            '</div></div>');
    }
}

function addExtraMiscInput() {
    // check if there are any extra misc inputs, if not, adds "misc" as labeltext
    var miscError = miscFieldHasError();
    var title = getString('post.resource.misc.tooltipRemove');
    if ($('.extraInputs').length > 0) {
        $(MISC_EXTRA_FIELD).append('<div class="extraInputs form-group' + (miscError ? ' has-error' : '') + '"><label class="col-sm-3 control-label"></label><div class="col-sm-4"><input class="form-control" type="text"/></div><div class="col-sm-4"><input class="form-control" type="text"/></div><div class="col-sm-1"><button title="' + title + '" class="btn btn-default pull-right remove-field" type="button">-</form:button></div></div>');
    } else {
        $(MISC_EXTRA_FIELD).append('<div class="extraInputs form-group' + (miscError ? ' has-error' : '') + '"><label class="col-sm-3 control-label">' + getString('post.resource.misc') + '</label><div class="col-sm-4"><input class="form-control" type="text"/></div><div class="col-sm-4"><input class="form-control" type="text"/></div><div class="col-sm-1"><button title="' + title + '" class="btn btn-default pull-right remove-field" type="button">-</form:button></div></div>');
    }
}

function addExtraMiscInputWithValues(fieldName, fieldValue) {
    addExtraMiscInput();
    $("#extraFieldsWrap > div:last > div:eq(0) > input").val(fieldName);
    $("#extraFieldsWrap > div:last > div:eq(1) > input").val(fieldValue);
}

/**
 * updated required fields
 */
function updateRequiredFields() {
    var requiredFields = requiredForType[$("#post\\.resource\\.entrytype").val()];
    var existingInputs = [];

    // remove not required inputs for this entry type
    var standardInputs = $('.standardInputs');
    $.each(standardInputs, function () {
        var input = $(this).find("input");
        var inputName = "misc." + $(input).attr("name").toLowerCase();

        if (!$(input).val() && !in_array_lower(requiredFields, inputName)) {
            $(this).remove();
        } else {
            existingInputs.push(inputName);
        }
    });

    // add the required ones for this entry type
    if (typeof requiredFields != 'undefined') {
        for (var i = 0; i < requiredFields.length; i++) {
            var requiredField = requiredFields[i].toLowerCase();
            if (requiredField.startsWith("misc.") && !in_array_lower(existingInputs, requiredField)) {
                addOrUpdateStandardMiscInput(requiredField.slice(5), "");
            }
        }
    } else if (!$(".extraInputs").length) {
        addExtraMiscInput();
    }
}

/*
 * change appearance of misc and transfer data
 */
$(document).ready(function () {
    var miscFieldValues = [];

    // FIXME: show error message if misc field has errors
    function transferMiscValuesToFields() {
        var fieldString = [];
        for (var i = 0; i < miscFieldValues.length; i += 2) {
            if (miscFieldValues[i] != "undefined" || miscFieldValues[i + 1] != "undefined") {
                fieldString.push("  " + miscFieldValues[i] + " = {" + miscFieldValues[i + 1] + "}");
            }
        }
        $(MISC_RAW_EXPERT).val(fieldString.join(", \n"));
    }

    function parseRawMiscData() {
        // gets the data from misc
        var miscVal = $(MISC_RAW_EXPERT).val();
        var pairs = miscVal.split(/,\s*\n/);
        var miscPairsAlternateList = [];

        // split the pairs and delete the characters not needed then save data
        $.each(pairs, function (index, item) {
            item = item.trim();
            var itemValues = item.split(/\s*=\s*{/);
            if (itemValues.length === 2) { // should have 2 values
                itemValues[0] = itemValues[0].trim();
                itemValues[1] = itemValues[1].trim();

                if (itemValues[1].endsWith("}")) { // formatting
                    itemValues[1] = itemValues[1].substr(0, itemValues[1].length - 1);
                }

                // show an empty input instead of "undefined"
                for (var i = 0; i < itemValues.length; i++) {
                    if (itemValues[i] === "undefined") {
                        itemValues[i] = "";
                    }
                }
            }
            miscPairsAlternateList = miscPairsAlternateList.concat(itemValues);
        });

        // set length to 0, so no new line is added
        if (miscPairsAlternateList.length === 1) {
            miscPairsAlternateList = [];
        }

        return miscPairsAlternateList;
    }

    function transferDataFromRaw() {
        var miscPairsAlternateList = parseRawMiscData();

        for (var i = 0; i < miscPairsAlternateList.length; i += 2) {
            var fieldName = miscPairsAlternateList[i].toLowerCase();
            var fieldValue = miscPairsAlternateList[i + 1];
            var fieldInput = $("#standardFieldsWrap :input[type=text][name='" + fieldName + "']");

            if (fieldInput.length > 0) {
                $(fieldInput).val(fieldValue);
            } else {
                addExtraMiscInputWithValues(fieldName, fieldValue);
            }
        }
    }

    function refreshDefaultView() {
        miscFieldValues = [];

        //standard fields
        $("#standardFieldsWrap :input[type=text]").each(function () {
            /*
             * pushes the name, then the value if not empty
             */
            if ($(this).val().replace(/\s+/g, '') != "") {
                miscFieldValues.push($(this).attr("name"));
                miscFieldValues.push($(this).val());
            }
        });

        //extra fields
        $("#extraFieldsWrap :input[type=text]").each(function () {
            /*
             * pushes the value of each text-input if it is not empty after deleting every whitespace
             * else pushes "undefined"
             */
            if ($(this).val().replace(/\s+/g, '') != "") {
                miscFieldValues.push($(this).val());
            } else {
                miscFieldValues.push("undefined");
            }
        });

        transferMiscValuesToFields();
    }

    function showDefaultMiscView() {
        if ($(".standardInputs").length === 0) {
            addExtraMiscInput();
        }
        $(MISC_RAW_EXPERT).closest(".form-group").addClass("hidden");
        $("#allFieldsWrap").removeClass("hidden");
    }

    function initMiscDataView() {
        var miscPairsAlternateList = parseRawMiscData();
        var initEntrytype = $("#post\\.resource\\.entrytype").data('selected-entrytype');
        var requiredFields = requiredForType[initEntrytype];

        for (var i = 0; i < miscPairsAlternateList.length; i += 2) {
            var fieldName = miscPairsAlternateList[i].toLowerCase();
            var fieldValue = miscPairsAlternateList[i + 1];

            if (typeof requiredFields != 'undefined' && in_array_lower(requiredFields, "misc." + fieldName)) {
                addOrUpdateStandardMiscInput(fieldName, fieldValue);
            } else {
                addExtraMiscInputWithValues(fieldName, fieldValue);
            }
        }
    }

    /*
     * after loading
     */
    initMiscDataView();

    // hides old view
    $(MISC_RAW_EXPERT).parent("div").parent("div").addClass("hidden");

    // user click on remove button
    $(MISC_EXTRA_FIELD).on("click", ".remove-field", function (e) {
        e.preventDefault();
        var parentDiv = $(this).parent('div').parent('div');
        var labelText = parentDiv.find("label").first().text();

        parentDiv.remove();
        $(".extraInputs").first().find("label").first().text(getString('post.resource.misc'));

        //this refreshes the values in the misc-fields
        refreshDefaultView();
    });

    // reloads standard fields for new entry type and toggle between all fields and required fields
    $("#post\\.resource\\.entrytype").change(function (e) {
        updateRequiredFields();
        changeView($("#collapse").data("showAll"));
    });

    // transfer field values of new design to array
    $("#allFieldsWrap").focusout(function (e) {
        refreshDefaultView();
    });

    // change view to old or new
    $("#expertViewToggle").change(function () {
        if (this.checked) {
            $(MISC_ADD_BTN).hide();
            // refresh data from default view
            refreshDefaultView();
            $("#allFieldsWrap").addClass("hidden");
            $(MISC_RAW_EXPERT).closest(".form-group").removeClass("hidden");
            $(".extraInputs").remove();
            $("#standardFieldsWrap :input[type=text]").each(function () {
                $(this).val("");
            });
        } else {
            $(MISC_ADD_BTN).show();
            // transfer data to default view
            transferDataFromRaw();
            // actually change to default misc view
            showDefaultMiscView();
        }
    });

    $("#collapse").click(function (e) {
        toggleView();
    });

    toggleView();

    var hash = $("#post\\.resource\\.interHash").val();
    if (hash == -1 || hash == undefined) return;

    $.ajax({
        url: '/json/bibtex/1' + hash + "?items=100",
        dataType: "jsonp",
        success: function (data) {
            if (data.items != undefined)
                buildGoodPostSuggestion(data);
        }
    });
});
