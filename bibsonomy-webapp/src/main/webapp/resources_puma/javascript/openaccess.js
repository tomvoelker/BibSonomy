var oaBaseUrl = "/ajax/checkOpenAccess";
var classificationURL = "/ajax/classificatePublication";
var swordURL = "/ajax/swordService";
var GET_AVAILABLE_CLASSIFICATIONS = "AVAILABLE_CLASSIFICATIONS";
var SAVE_CLASSIFICATION_ITEM = "SAVE_CLASSIFICATION_ITEM";
var SAVE_CLASSIFICATION_ITEMS = "SAVE_CLASSIFICATION_ITEMS";
var REMOVE_CLASSIFICATION_ITEM = "REMOVE_CLASSIFICATION_ITEM";
var GET_POST_CLASSIFICATION_LIST = "GET_POST_CLASSIFICATION_LIST";
var publication_intrahash = ""; // will be set during initialisation

function initialiseOpenAccessClassification(divBaseName, intraHash) {
	// div-structure for classification:
	// divBaseName+'Container' - outer container - may be defined in html
	// divBaseName+'List' - list of selected classification elements - must be defined in html
	// divBaseName+'Select' - classifications to select an element  - must be defined in html
	var divClassificationContainerName =  divBaseName+'Container';
	var divClassificationListName =  divBaseName+'List';
	var divClassificationSelectName =  divBaseName+'Select';

	if ((null == document.getElementById(divClassificationListName)) || (null == document.getElementById(divClassificationListName))) {
		return;
	}

	// set to visible
	if (null != $('#'+divClassificationContainerName)) {
		$('#'+divClassificationContainerName).show();
	}
	// set to visible
	if (null != $('#'+divClassificationListName)) {
		$('#'+divClassificationListName).show();
	}
	// set to visible
	if (null != $('#'+divClassificationSelectName)) {
		$('#'+divClassificationSelectName).show();
	}
	
	publication_intrahash = $("#openAccessCurrentPublicationHash").val();
	
	// init Classification 
	initClassifications(divClassificationSelectName, divClassificationListName);
	
}

function initialiseOpenAccessSendToRepository(divName, intraHash) {
	
	// create Send to repository button 
	
	var sword = document.createElement('div');
	sword.setAttribute('id','pumaSword');
	
	var saveSword = document.createElement('input');
	saveSword.type = 'button';
	saveSword.className = "ajaxButton",
	saveSword.value =  getString("post.resource.openaccess.button.sendtorepository");
	sword.appendChild(saveSword);
	

	var loadingNode = document.createElement('img');
	loadingNode.setAttribute('src', '/resources_puma/image/ajax-loader.gif');
	
	var url = swordURL +"?resourceHash=" + intraHash;
	saveSword.onclick = function(data) {
		$.ajax({
			url: url,
			dataType: 'json',
			beforeSend: function(XMLHttpRequest) {
				// remove node #swordresponse
				$('#swordresponse').remove();
				$('#pumaSword').append(loadingNode);
				
			},
			success: function(data) {
				// remove loading icon
				$(loadingNode).remove();

				// response has following format:
				// {"response":{"message":"error.sword.noPDFattached","localizedMessage":"Keine PDF-Datei zum übermitteln gefunden","statuscode":0}}
				// statuscode can be 0 (error/warning) or 1 (success)
				
				// check and show response to user
				$.each(data, function(i, response) {
					if (null == data || null == data.response) {
						alert ("unknown response error");
					} else {
						// create text node behind transmit button, if not exists, to show response text in it
						// confirmations and warnings get different css-classes  
						var s = createNode({
							tag: 'div',
							parent: null,
							child: null,
							childNodes: null,
							parentID : null,
							id: 'swordresponse',
							className: "ajaxresponse"+data.response.statuscode
						});

						s.appendChild(document.createTextNode(data.response.localizedMessage));
						$('#pumaSword').append(s);						
						
						swordResponseStatusCode = data.response.statuscode; 

						// show response text
						
					}
				});


			},
			error: function(req, status, e) {
				$(loadingNode).remove();
				alert("Unable to send data to reposity: " + status);
			}
		});
	};	
	

	// add elements to dom
	$('#' +divName).append(sword);

}


/* open access check */
/* TODO: add error handling, check apicontrol and outcome in response. */
function checkOpenAccess () {
	var container = $("#openAccessRomeoSherpa");	

	// TODO: add progress animation
	var url = "/ajax/checkOpenAccess"+$("#oaRequestPublisher").val();

	$.ajax({
		url: url,
		dataType: 'json',
		success: function(data) {
			/*
			 * build list with publishers
			 */
			if ((data.publishers.length>0) && (undefined != data.publishers) && (data.publishers!="")) {
				container.empty();
				var ul = document.createElement("ul");
				ul.className = "oa-publishers";
				$.each(data.publishers, function(index, publisher) {
					var li = document.createElement("li");
					li.className = "oa-" + publisher.colour;
					var span = document.createElement("span");
					span.appendChild(document.createTextNode(publisher.name));
					span.className = "oa-publisher";
					li.appendChild(span);
					var ulCond = document.createElement("ul");
					ulCond.className = "oa-conditions";
					$.each(publisher.conditions, function(index, condition) {
						var liCond = document.createElement("li");
						liCond.appendChild(document.createTextNode(condition));
						ulCond.appendChild(liCond);
					});
					li.appendChild(ulCond);
					ul.appendChild(li);
				});
				container.append(ul);
				container.fadeIn();
			}
		},
		error: function(req, status, e) {
			alert("check open access: " + status);
		}
	});
}

function initClassifications(divClassificationSelectName, divClassificationListName) {
	var url = classificationURL + "?action=" +GET_AVAILABLE_CLASSIFICATIONS;
	$.ajax({
		dataType: 'json',
		url: url,
		success: function(data) {
			doInitialise(divClassificationSelectName, divClassificationListName, data);
		},
		error: function(req, status, e) {
			alert("There seems to be an error in the ajax request, classifications.js::init");
		}
	});
}

function doInitialise(divClassificationSelectName, divClassificationListName, data) {
	$.each(data.available, function(i,item){

		var saveNode = document.createElement('div');
		saveNode.setAttribute('id', item +'saved');

		
		var mainNode = document.createElement('div');
		mainNode.setAttribute('id', item);
		
		var input = document.createElement('div');
		input.setAttribute('id',item +'_input');
		input.setAttribute("class", "classificationInput");
		
		mainNode.appendChild(input);

		$('#' +divClassificationListName).append(saveNode);
		$('#' +divClassificationSelectName).append(mainNode);
		
		populate(item,item);
	});
}

function populate(classification, container) {

	var url = classificationURL + "?classificationName=" +classification;
	// perform ajax request
	$.ajax({
		dataType: 'json',
		url: url,
		success: function(data) {
			createSubSelect(null,data,classification,"",container);
		},
		error: function(req, status, e) {
			alert("There seems to be an error in the ajax request, classifications.js::populate");
		}
	});
}

function removeNode(node){
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

	
	if(childNodes != null) {
		for(var i = 0; i < childNodes.length; i++) {
			node.appendChild(createOptionsTag(childNodes[i]));
		}
	}
	
	for (var i in atts){
		try{
			node[i] = atts[i];
		}catch(e){
			alert(e);
		}
	}
	
	if(parent)
		parent.child = node;

	return node;
}

/* 
 * TODO: wird diese Funktion benutzt?
 * hardcoded #classifications und JEL 
 */
function createNewClassField(container) {
	var node = document.createElement('div');	
	node.id = container +'1';
	
	var input = document.createElement('input');
	input.type = 'text';
	input.size = '25';
	input.id= node.id +'_input';
	input.disabled= 'true';
	
	node.appendChild(input);

	$('#classifications').append(node);

	populate('JEL',node.id);
	
}

function _addClassificationItemToList(classificationName, ClassificationValue) {
	var node = document.createElement('div');
	var saveListItem = document.createElement('div');
	saveListItem.setAttribute('id', "classificationListItemElement"+classificationName+ClassificationValue);
	saveListItem.setAttribute('class', 'classificationListItem');
	
	var save = document.createElement('div');
	save.type = 'text';
	save.value = classificationName +' ' +ClassificationValue +' ';
	
	save.name = 'classification';
	save.setAttribute('readonly', "readonly");
	
	var remove = document.createElement('input');
	remove.type = 'button';
	remove.className = 'ajaxButton btnspace';
	remove.value = getString("post.resource.openaccess.button.removeclassification");
	remove.id	= "classificationListItemRemove"+classificationName+ClassificationValue;

	node.appendChild(saveListItem);
	node.appendChild(remove);
	
	$('#'+classificationName +'saved').append(node);

	$('#'+"classificationListItemElement"+classificationName+ClassificationValue).text(classificationName +' ' +ClassificationValue +' ');
	
	remove.onclick = function() {

		var removeurl = classificationURL + "?action=" +REMOVE_CLASSIFICATION_ITEM+"&hash="+publication_intrahash+"&key="+classificationName+"&value="+ClassificationValue;
		
		var loadingNode = document.createElement('img');
		loadingNode.setAttribute('src', '/resources_puma/image/ajax-loader.gif');
		
		$.ajax({
			dataType: 'json',
			url: removeurl,
			beforeSend: function(XMLHttpRequest) {
				$('#classificationListItemRemove'+classificationName+ClassificationValue).parent().append(loadingNode);
				
			},
			success: function(data) {
				$(node).remove();
				$(loadingNode).remove();
			
			},
			error: function(req, status, e) {
				$(loadingNode).remove();
				alert("There seems to be an error in the ajax request, classifications.js::createSubSelect");
			}
		});					
		
		
		
	}
}


function addSaved(container, parentID, description) {

	/*
	 * add only a new item, if it does not exist. 
	 * $().length / if length is 0, element does not exist 
	 */
	if (!$("#classificationListItemElement"+container+parentID).length){

		// send item via ajax to database
		var saveurl = classificationURL + "?action=" +SAVE_CLASSIFICATION_ITEM+"&hash="+publication_intrahash+"&key="+container+"&value="+parentID;
		
		var loadingNode = document.createElement('img');
		loadingNode.setAttribute('src', '/resources_puma/image/ajax-loader.gif');

		$.ajax({
			dataType: 'json',
			url: saveurl,
			beforeSend: function(XMLHttpRequest) {
				$('#' +container).append(loadingNode);
				
			},
			success: function(data) {

				$(loadingNode).remove();

				_addClassificationItemToList(container, parentID);
			
			},
			error: function(req, status, e) {
				$(loadingNode).remove();

				alert("There seems to be an error in the ajax request, classifications.js::createSubSelect");
			}
		});
		
		// show item in list if database save request was successful
		
		// otherwise show error
		

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
		parentID : parentID,
		onclick: function(){

		addSaved(container, parentID, parent.text);
	},
	deleteChild: function(){

		if (this.child){
			
			if (this.child.deleteChild){
				this.child.deleteChild();
			}
			
			removeNode(this.child);
			this.child = null;
		}
	}
	});

	$('#' +container).append(s);
}


function createSubSelect(parent, data, classification, parentID, container){
	if (!data) {
		return;
	}

	$('#' +container +'_input').text(parentID);
	
	var options = [{tag: "option", value: "", text: classification}];
	
	$.each(data.children, function(i,item){
		options.push({tag: "option", value: item.id, text: item.id +" - " +item.description});
	});
	
	if (options.length == 1) {
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
		parentID : parentID,
		classification: classification,
		onchange: function(){
		this.deleteChild();
		
		if(this.value == "")
			return;
		
		var id = parentID +this.value;
		var url = classificationURL + "?classificationName=" +classification +"&id=" +id;
		
		var loadingNode = document.createElement('img');
		loadingNode.setAttribute('src', '/resources_puma/image/ajax-loader.gif');

		$.ajax({
			dataType: 'json',
			url: url,
			beforeSend: function(XMLHttpRequest) {
				$('#' +container).append(loadingNode);
				
			},
			success: function(data) {

				$(loadingNode).remove();
				createSubSelect(s,data,classification,id,container);
			},
			error: function(req, status, e) {
				$(loadingNode).remove();
				alert("There seems to be an error in the ajax request, classifications.js::createSubSelect");
			}
		});
	},
	deleteChild: function(){
		if (this.child){
			
			if (this.child.deleteChild){
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
	
	$('#' +container).append(s);
}


function sendAdditionalMetadataFields() {
	var saveMetadataButtonId = "saveMetadataButton";
	var metadatafields = Array();
	var i=0;
	metadatafields[i++] = "post.resource.openaccess.additionalfields.institution";
	metadatafields[i++] = "post.resource.openaccess.additionalfields.phdreferee";
	metadatafields[i++] = "post.resource.openaccess.additionalfields.phdreferee2";
	metadatafields[i++] = "post.resource.openaccess.additionalfields.phdoralexam";
	metadatafields[i++] = "post.resource.openaccess.additionalfields.sponsor";
	metadatafields[i++] = "post.resource.openaccess.additionalfields.additionaltitle";
	
	var collectedMetadataJSONText = '{ ';
	var collectedMetadataJSON = {};
	for(var i = 0; i < metadatafields.length; i++) {
		collectedMetadataJSONText += '"' + metadatafields[i] + '":"' + $("#"+(metadatafields[i].replace(/\./g,'\\.'))).val() +'", '; 
	}
	collectedMetadataJSONText += " } ";

	console.log(collectedMetadataJSONText);
	//collectedMetadataJSON = eval('(' + collectedMetadataJSONText + ')');
	
	// send item via ajax to database
	var saveurl = classificationURL + "?action=" +SAVE_CLASSIFICATION_ITEMS+"&hash="+publication_intrahash;
	var loadingNode = document.createElement('img');
	loadingNode.setAttribute('src', '/resources_puma/image/ajax-loader.gif');
	
	// send metadata
		$.ajax({
			dataType: 'json',
			url: saveurl,
			data: { "value" : collectedMetadataJSONText },
			type: 'POST',
			
			beforeSend: function(XMLHttpRequest) {
				$('#' +saveMetadataButtonId).append(loadingNode);
				
			},
			success: function(data) {
				$(loadingNode).remove();


			
			},
			error: function(req, status, e) {
				$(loadingNode).remove();
				alert("There seems to be an error in the ajax request, classifications.js::createSubSelect");
			}
		});
			
	// change button class (red to green)
	metadataUnChanged();
}


function loadAdditionalMetadataFields() {
	
	
}

function metadataOnChange() {
	saveMetadataButtonId = "saveMetadataButton";
	dataChangedClass = "dataChanged";

	if (!$("#"+saveMetadataButtonId).hasClass(dataChangedClass)) $("#"+saveMetadataButtonId).addClass("dataChanged")
}

function metadataUnChanged() {
	saveMetadataButtonId = "saveMetadataButton";
	dataChangedClass = "dataChanged";

	if ($("#"+saveMetadataButtonId).hasClass(dataChangedClass)) $("#"+saveMetadataButtonId).removeClass("dataChanged")
}

function loadStoredClassificationItems()
{
	// clear list 
	
	// get data
	// example data set: {"ACM":["C21","C22"],"JEL":["F41"]}
	var url = classificationURL + "?action="+GET_POST_CLASSIFICATION_LIST+"&hash="+publication_intrahash+"&key=ACM";
	// perform ajax request
	$.ajax({
		dataType: 'json',
		url: url,
		success: function(data) {
			// iterate over data
			$.each(data, function(classification,classificationData){
				$.each(classificationData, function(j,item){
					/*
					 * add only a new item, if it does not exist. 
					 * $().length / if length is 0, element does not exist 
					 */
					if (!$("#classificationListItemElement"+classification+item).length){
						_addClassificationItemToList(classification, item);
					}
				});		
			});
			// add item to list
			// addSaved()
		},
		error: function(req, status, e) {
			alert("There seems to be an error in the ajax request, openaccess.js::loadStoredClassificationItems");
		}
	});	
	
}