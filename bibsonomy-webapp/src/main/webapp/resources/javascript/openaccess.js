var OA_STATUS_URL = "/ajax/openaccess/status";
var CLASSIFY_PUB_URL = "/ajax/openaccess/classifyPublication";
var SWORD_SERVICE_URL = "/ajax/openaccess/swordService";

// Actions
var GET_AVAILABLE_CLASSIFICATIONS = "AVAILABLE_CLASSIFICATIONS";
var SAVE_CLASSIFICATION_ITEM = "SAVE_CLASSIFICATION_ITEM";
var SAVE_ADDITIONAL_METADATA = "SAVE_ADDITIONAL_METADATA";
var GET_ADDITIONAL_METADATA = "GET_ADDITIONAL_METADATA";
var REMOVE_CLASSIFICATION_ITEM = "REMOVE_CLASSIFICATION_ITEM";
var GET_POST_CLASSIFICATION_LIST = "GET_POST_CLASSIFICATION_LIST";
var GET_CLASSIFICATION_DESCRIPTION = "GET_CLASSIFICATION_DESCRIPTION";
var GET_SENT_REPOSITORIES = "GET_SENT_REPOSITORIES";
var DISSEMIN = "DISSEMIN";

var metadataChanged = false; // flag to remember if metadata has changend
var metadataFields = Array();
var autoSaveMetadataCounter = 0;

/**
 * on load
 */
$(function () {
    updateStatusOA();
});

function updateStatusOA() {
    var intrahash = $('.post-openaccess').data('intrahash');

    $.ajax({
        url: OA_STATUS_URL,
        data: {
            // These are the variables you can pass to the request
            'action': DISSEMIN,
            'intrahash': intrahash
        },
        success: function (data) {
            $('#oaPostStatus').html(data);
        },
        beforeSend: function(){
            $("#oaPostStatusLoader").show(0);
        },
        complete: function(){
            $("#oaPostStatusLoader").hide(0);
        }
    });
}

function empty(mixed_var) {
    var key;
    if (mixed_var === "" || mixed_var === 0 || mixed_var === "0" || mixed_var === null || mixed_var === false || typeof mixed_var === 'undefined') {
        return true;
    }

    if (typeof mixed_var == 'object') {
        for (key in mixed_var) {
            return false;
        }
        return true;
    }
    return false;
}

function _removeSpecialChars(s) {
    s = s.replace(/[^a-zA-Z0-9]/g, '');
    return s;
}

function sentPublicationToRepository(elementId, intraHash) {
    if (document.getElementById('authorcontractconfirm').checked) {
        var loadingNode = document.createElement('img');
        loadingNode.setAttribute('src', '/resources_puma/image/ajax-loader.gif');

        if (isMetadataChanged()) sendAdditionalMetadataFields(false);

        $.ajax({
            url: SWORD_SERVICE_URL + "?resourceHash=" + intraHash,
            dataType: 'json',
            beforeSend: function (XMLHttpRequest) {

                // remove node #swordresponse
                $('#swordresponse').remove();
                $('#pumaSword').append(loadingNode);
                $('#oasendtorepositorybutton').addClass("oadisabledsend2repositorybutton");
                document.getElementById(elementId).disabled = true;

            },
            success: function (data) {
                // remove loading icon
                $(loadingNode).remove();

                // response has following format:
                // {"response":{"message":"error.sword.noPDFattached","localizedMessage":"Keine PDF-Datei zum übermitteln gefunden","statuscode":0}}
                // statuscode can be 0 (error/warning) or 1 (success)

                // check and show response to user
                $.each(data, function (i, response) {
                    if (null == data || null == data.response) {
                        showAjaxAlert("error", "unknown response error");
                    } else {
                        // create text node behind transmit button, if not exists, to show response text in it
                        // confirmations and warnings get different css-classes
                        var s = createNode({
                            tag: 'div',
                            parent: null,
                            child: null,
                            childNodes: null,
                            parentID: null,
                            id: 'swordresponse',
                            className: "ajaxresponse" + data.response.statuscode
                        });

                        s.appendChild(document.createTextNode(data.response.localizedMessage));
                        $('#pumaSword').append(s);

                        swordResponseStatusCode = data.response.statuscode;

                        // on error enable button
                        if (data.response.statuscode === 0) {
                            $(elementId).removeClass("oadisabledsend2repositorybutton");
                            document.getElementById(elementId).disabled = false;
                        }

                        // show response text

                    }
                });
            },
            error: function (req, status, e) {
                $(loadingNode).remove();
                showAjaxAlert("error", "Unable to send data to repository: " + status);
            }
        });
    } // end of if ($('#authorcontractconfirm').checked)
}

function initClassifications(divClassificationSelectName, divClassificationListName) {
    $.ajax({
        dataType: 'json',
        url: CLASSIFY_PUB_URL + "?action=" + GET_AVAILABLE_CLASSIFICATIONS,
        success: function (data) {
            doInitialise(divClassificationSelectName, divClassificationListName, data);
        },
        error: function (req, status, e) {
            showAjaxAlert("error", "There seems to be an error in the ajax request, classifications.js::init");
        }
    });
}

function doInitialise(divClassificationSelectName, divClassificationListName, data) {
    $.each(data.available, function (i, it) {

        var saveNode = document.createElement('div');
        saveNode.setAttribute('id', it.name + 'saved');

        var helpNode = document.createElement('div');
        helpNode.setAttribute('class', 'help');

        var questionMark = document.createElement('b');
        questionMark.setAttribute('class', 'smalltext');

        var link = document.createElement('a');
        link.setAttribute('href', it.url)
        link.appendChild(document.createTextNode('?'));

        questionMark.appendChild(link);

        var helpNode1 = document.createElement('div');
        helpNode1.appendChild(document.createTextNode(it.desc));

        helpNode.appendChild(questionMark);
        helpNode.appendChild(helpNode1);

        var mainNode = document.createElement('div');
        mainNode.setAttribute('id', it.name);

        var input = document.createElement('div');
        input.setAttribute('id', it.name + '_input');
        input.setAttribute("class", "classificationInput");

        mainNode.appendChild(input);
        mainNode.appendChild(helpNode);

        $('#' + divClassificationListName).append(saveNode);
        $('#' + divClassificationSelectName).append(mainNode);

        populate(it.name, it.name);
    });
}

function populate(classification, container) {
    $.ajax({
        dataType: 'json',
        url: CLASSIFY_PUB_URL + "?classificationName=" + classification,
        success: function (data) {
            createSubSelect(null, data, classification, "", container);
        },
        error: function (req, status, e) {
            showAjaxAlert("error", "There seems to be an error in the ajax request, classifications.js::populate");
        }
    });
}

function removeNode(node) {
    node.parentNode.removeChild(node);
    return node;
}

function createOptionsTag(atts) {
    var tag = atts.tag;
    var value = atts.value;
    var text = atts.text;

    var node = document.createElement(tag);

    node.value = value;
    node.text = text;

    return node;
}

function createNode(atts) {

    var tag = atts.tag;
    var parent = atts.parent;
    var childNodes = atts.childNodes;

    delete atts.tag;
    delete atts.parent;
    delete atts.childNodes;

    var node = document.createElement(tag);


    if (childNodes != null) {
        for (var i = 0; i < childNodes.length; i++) {
            node.appendChild(createOptionsTag(childNodes[i]));
        }
    }

    for (var i in atts) {
        try {
            node[i] = atts[i];
        } catch (e) {
            showAjaxAlert("error", e);
        }
    }

    if (parent)
        parent.child = node;

    return node;
}

function _addClassificationItemToList(classificationName, classificationValue) {
    var node = document.createElement('div');
    node.className = "classificationListItemContainer";
    var saveListItem = document.createElement('div');
    var classificationId = _removeSpecialChars(classificationName + classificationValue);
    saveListItem.setAttribute('id', "classificationListItemElement" + classificationId);
    saveListItem.setAttribute('class', 'classificationListItem');

    var remove = document.createElement('input');
    remove.type = 'button';
    remove.className = 'ajaxButton btnspace';
    remove.value = getString("post.resource.openaccess.button.removeclassification");
    remove.id = "classificationListItemRemove" + classificationId;


    var dCnode = document.createElement('div');
    dCnode.setAttribute("class", "classificationListItemDescriptionContainer");

    var description = document.createElement('div');
    description.setAttribute("id", "classificationListItemElementDescription" + classificationId);
    description.setAttribute("class", "classificationListItemDescription");

    node.appendChild(saveListItem);
    dCnode.appendChild(description);
    dCnode.appendChild(remove);
    node.appendChild(dCnode);

    $('#' + classificationName + 'saved').append(node);

    $('#' + "classificationListItemElement" + classificationId).text(classificationName + ' ' + classificationValue + ' ');

    $.ajax({
        dataType: 'json',
        url: CLASSIFY_PUB_URL + "?action=" + GET_CLASSIFICATION_DESCRIPTION + "&key=" + classificationName + "&value=" + classificationValue,
        success: function (data) {
            $('#classificationListItemElementDescription' + _removeSpecialChars(data.name + data.value)).text(data.description);
        },
        error: function (req, status, e) {
            $('#classificationListItemElementDescription' + classificationId).text("-");
        }
    });


    remove.onclick = function () {
        var loadingNode = document.createElement('img');
        loadingNode.setAttribute('src', '/resources_puma/image/ajax-loader.gif');

        $.ajax({
            dataType: 'json',
            url: CLASSIFY_PUB_URL + "?action=" + REMOVE_CLASSIFICATION_ITEM + "&hash=" + intrahash + "&key=" + classificationName + "&value=" + classificationValue,
            beforeSend: function (XMLHttpRequest) {
                $('#classificationListItemRemove' + classificationId).parent().append(loadingNode);

            },
            success: function (data) {
                $(node).remove();
                $(loadingNode).remove();

            },
            error: function (req, status, e) {
                $(loadingNode).remove();
                showAjaxAlert("error", ("There seems to be an error in the ajax request, classifications.js::createSubSelect"));
            }
        });


    };
}

function addSaved(container, parentID, description) {

    /*
     * add only a new item, if it does not exist.
     * $().length / if length is 0, element does not exist
     */
    if (!$("#classificationListItemElement" + _removeSpecialChars(container + parentID)).length) {
        var loadingNode = document.createElement('img');
        loadingNode.setAttribute('src', '/resources_puma/image/ajax-loader.gif');

        $.ajax({
            dataType: 'json',
            url: CLASSIFY_PUB_URL + "?action=" + SAVE_CLASSIFICATION_ITEM + "&hash=" + intrahash + "&key=" + container + "&value=" + parentID,
            beforeSend: function (XMLHttpRequest) {
                $('#' + container).append(loadingNode);

            },
            success: function (data) {
                $(loadingNode).remove();
                _addClassificationItemToList(container, parentID);
            },
            error: function (req, status, e) {
                $(loadingNode).remove();
                showAjaxAlert("error", "There seems to be an error in the ajax request, classifications.js::createSubSelect");
            }
        });
    }
}

function createSaveButton(parent, parentID, container) {

    var s = createNode({
        tag: 'input',
        className: "ajaxButton btnspace",
        parent: parent,
        child: null,
        type: 'button',
        value: getString("post.resource.openaccess.button.saveclassification"),
        childNodes: null,
        parentID: parentID,
        onclick: function () {

            addSaved(container, parentID, parent.text);
        },
        deleteChild: function () {

            if (this.child) {

                if (this.child.deleteChild) {
                    this.child.deleteChild();
                }

                removeNode(this.child);
                this.child = null;
            }
        }
    });

    $('#' + container).append(s);
}


function createSubSelect(parent, data, classification, parentID, container) {
    if (!data) {
        return;
    }

    $('#' + container + '_input').text(parentID);

    var options = [{tag: "option", value: "", text: classification}];

    $.each(data.children, function (i, item) {

        options.push({tag: "option", value: item.id, text: item.id + " - " + item.description});
    });

    if (options.length === 1) {
        //no more options available
        createSaveButton(parent, parentID, container);
        return;
    }


    var s = createNode({
        tag: "select",
        className: "classificationSelect",
        data: data,
        parent: parent,
        child: null,
        size: '1',
        childNodes: options,
        parentID: parentID,
        classification: classification,
        onchange: function () {
            this.deleteChild();

            if (this.value === "")
                return;

            var id = parentID + this.value;
            var loadingNode = document.createElement('img');
            loadingNode.setAttribute('src', '/resources_puma/image/ajax-loader.gif');

            $.ajax({
                dataType: 'json',
                url: CLASSIFY_PUB_URL + "?classificationName=" + classification + "&id=" + id,
                beforeSend: function (XMLHttpRequest) {
                    $('#' + container).append(loadingNode);
                },
                success: function (data) {
                    $(loadingNode).remove();
                    createSubSelect(s, data, classification, id, container);
                },
                error: function (req, status, e) {
                    $(loadingNode).remove();
                    showAjaxAlert("error", "There seems to be an error in the ajax request, classifications.js::createSubSelect");
                }
            });
        },
        deleteChild: function () {
            if (this.child) {
                if (this.child.deleteChild) {
                    this.child.deleteChild();
                }

                removeNode(this.child);
                this.child = null;
            }
        }
    });

    if (parent) {
        parent.child = s;
    }

    $('#' + container).append(s);
}

function sendAdditionalMetadataFields(async) {
    if (async !== true) async = false;

    var ElementId = "sendMetadataMarker";
    var mdf = Array();
    var i = 0;

    mdf = getMetadataFields();

    var collectedMetadataJSONText = '{ ';
    var collectedMetadataJSON = {};
    for (var i = 0; i < mdf.length; i++) {
        collectedMetadataJSONText += '"' + mdf[i] + '":"' + $("#" + (mdf[i].replace(/\./g, '\\.'))).val() + '", ';
    }
    collectedMetadataJSONText += " } ";

    // send item via ajax to database
    var loadingNode = document.createElement('img');
    loadingNode.setAttribute('src', '/resources_puma/image/ajax-loader.gif');

    // send metadata
    $.ajax({
        dataType: 'json',
        url: CLASSIFY_PUB_URL,
        async: async,
        data: {
            "action": SAVE_ADDITIONAL_METADATA,
            "hash": intrahash,
            "value": collectedMetadataJSONText
        },
        type: 'post',

        beforeSend: function (XMLHttpRequest) {
            $('#' + elementId).append(loadingNode);

        },
        success: function (data) {
            $(loadingNode).remove();
            setMetadataChanged(false);
        },
        error: function (req, status, e) {
            $(loadingNode).remove();
            showAjaxAlert("error", "There seems to be an error in the ajax request, classifications.js::createSubSelect");
        }
    });
}

function autoSaveMetadata(value) {
    booleanValue = value ? true : false;
    //console.log("autoSaveMetadata:"+autoSaveMetadataCounter);
    if (booleanValue) {
        // is counter already runnning?
        if (autoSaveMetadataCounter > 0) {
            // reset counter, but do not start another one
            autoSaveMetadataCounter = 4;
            return;
        }

        // init
        autoSaveMetadataCounter = 4;
    } else {
        autoSaveMetadataCounter--;
    }

    if (autoSaveMetadataCounter < 1) {
        sendAdditionalMetadataFields(false);
        autoSaveMetadataCounter = 0;
    } else {
        setTimeout("autoSaveMetadata()", 700);
    }

}

function setMetadataChanged(value) {
    booleanValue = value ? true : false;
    elementId = "sendMetadataMarker";
    elementClass = "highlight";
    if (booleanValue === true) {
        // add class to tag
        if (!$("#" + elementId).hasClass(elementClass)) $("#" + elementId).addClass(elementClass);
        // start autosave counter
        setTimeout("autoSaveMetadata(true)", 100);
    } else {
        // remove class from tag
        if ($("#" + elementId).hasClass(elementClass)) $("#" + elementId).removeClass(elementClass);
    }

    metadataChanged = booleanValue;
}

function checkauthorcontractconfirm() {
    if (document.getElementById('authorcontractconfirm').checked) {
        if ($('#oasendtorepositorybutton').hasClass("oadisabledsend2repositorybutton")) {
            $('#oasendtorepositorybutton').removeClass("oadisabledsend2repositorybutton");
            document.getElementById('oasendtorepositorybutton').disabled = false;
        }
    } else {
        if (!$('#oasendtorepositorybutton').hasClass("oadisabledsend2repositorybutton")) {
            $('#oasendtorepositorybutton').addClass("oadisabledsend2repositorybutton");
            document.getElementById('oasendtorepositorybutton').disabled = true;
        }
    }
}
