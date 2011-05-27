/**
 * jquery.dump.js
 * @author Torkild Dyvik Olsen
 * @version 1.0
 * 
 * A simple debug function to gather information about an object.
 * Returns a nested tree with information.
 * 
 */
(function($) {

$.fn.dump = function() {
   return $.dump(this);
}

$.dump = function(object) {
   var recursion = function(obj, level) {
      if(!level) level = 0;
      var dump = '', p = '';
      for(i = 0; i < level; i++) p += "\t";
      
      t = type(obj);
      switch(t) {
         case "string":
            return '"' + obj + '"';
            break;
         case "number":
            return obj.toString();
            break;
         case "boolean":
            return obj ? 'true' : 'false';
         case "date":
            return "Date: " + obj.toLocaleString();
         case "array":
            dump += 'Array ( \n';
            $.each(obj, function(k,v) {
               dump += p +'\t' + k + ' => ' + recursion(v, level + 1) + '\n';
            });
            dump += p + ')';
            break;
         case "object":
            dump += 'Object { \n';
            $.each(obj, function(k,v) {
               dump += p + '\t' + k + ': ' + recursion(v, level + 1) + '\n';
            });
            dump += p + '}';
            break;
         case "jquery":
            dump += 'jQuery Object { \n';
            $.each(obj, function(k,v) {
               dump += p + '\t' + k + ' = ' + recursion(v, level + 1) + '\n';
            });
            dump += p + '}';
            break;
         case "regexp":
            return "RegExp: " + obj.toString();
         case "error":
            return obj.toString();
         case "document":
         case "domelement":
            dump += 'DOMElement [ \n'
                  + p + '\tnodeName: ' + obj.nodeName + '\n'
                  + p + '\tnodeValue: ' + obj.nodeValue + '\n'
                  + p + '\tinnerHTML: [ \n';
            $.each(obj.childNodes, function(k,v) {
               if(k < 1) var r = 0;
               if(type(v) == "string") {
                  if(v.textContent.match(/[^\s]/)) {
                     dump += p + '\t\t' + (k - (r||0)) + ' = String: ' + trim(v.textContent) + '\n';
                  } else {
                     r--;
                  }
               } else {
                  dump += p + '\t\t' + (k - (r||0)) + ' = ' + recursion(v, level + 2) + '\n';
               }
            });
            dump += p + '\t]\n'
                  + p + ']';
            break;
         case "function":
            var match = obj.toString().match(/^(.*)\(([^\)]*)\)/im);
            match[1] = trim(match[1].replace(new RegExp("[\\s]+", "g"), " "));
            match[2] = trim(match[2].replace(new RegExp("[\\s]+", "g"), " "));
            return match[1] + "(" + match[2] + ")";
         case "window":
         default:
            dump += 'N/A: ' + t;
            break;
      }
      
      return dump;
   }
   
   var type = function(obj) {
      var type = typeof(obj);
      
      if(type != "object") {
         return type;
      }
      
      switch(obj) {
         case null:
            return 'null';
         case window:
            return 'window';
         case document:
            return 'document';
         case window.event:
            return 'event';
         default:
            break;
      }
      
      if(obj.jquery) {
         return 'jquery';
      }
      
      switch(obj.constructor) {
         case Array:
            return 'array';
         case Boolean:
            return 'boolean';
         case Date:
            return 'date';
         case Object:
            return 'object';
         case RegExp:
            return 'regexp';
         case ReferenceError:
         case Error:
            return 'error';
         case null:
         default:
            break;
      }
      
      switch(obj.nodeType) {
         case 1:
            return 'domelement';
         case 3:
            return 'string';
         case null:
         default:
            break;
      }
      
      return 'Unknown';
   }
   
   return recursion(object);
}

function trim(str) {
   return ltrim(rtrim(str));
}

function ltrim(str) {
   return str.replace(new RegExp("^[\\s]+", "g"), "");
}

function rtrim(str) {
   return str.replace(new RegExp("[\\s]+$", "g"), "");
}

})(jQuery);






















var oaBaseUrl = "/ajax/checkOpenAccess";
var classificationURL = "/ajax/classificatePublication";
var swordURL = "/ajax/swordService";
var GET_AVAILABLE_CLASSIFICATIONS = "AVAILABLE_CLASSIFICATIONS";
var SAVE_CLASSIFICATION_ITEM = "SAVE_CLASSIFICATION_ITEM";
var SAVE_ADDITIONAL_METADATA = "SAVE_ADDITIONAL_METADATA";
var GET_ADDITIONAL_METADATA = "GET_ADDITIONAL_METADATA";
var REMOVE_CLASSIFICATION_ITEM = "REMOVE_CLASSIFICATION_ITEM";
var GET_POST_CLASSIFICATION_LIST = "GET_POST_CLASSIFICATION_LIST";
var GET_CLASSIFICATION_DESCRIPTION = "GET_CLASSIFICATION_DESCRIPTION";
var GET_SENT_REPOSITORIES = "GET_SENT_REPOSITORIES";
var publication_intrahash = ""; // will be set during initialisation
var publication_interhash = ""; // will be set during initialisation
var metadataChanged = false; // flag to remember if metadata has changend
var metadatafields = Array();
var autoSaveMetadataCounter = 0;

function setMetadatafields(mdf) {
	metadatafields = mdf;
}

function getMetadatafields(){
	return metadatafields;
}


function empty (mixed_var) {
    // !No description available for empty. @php.js developers: Please update the function summary text file.
    // 
    // version: 1103.1210
    // discuss at: http://phpjs.org/functions/empty    // +   original by: Philippe Baumann
    // +      input by: Onno Marsman
    // +   bugfixed by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // +      input by: LH
    // +   improved by: Onno Marsman    // +   improved by: Francesco
    // +   improved by: Marc Jansen
    // +   input by: Stoyan Kyosev (http://www.svest.org/)
    // *     example 1: empty(null);
    // *     returns 1: true    // *     example 2: empty(undefined);
    // *     returns 2: true
    // *     example 3: empty([]);
    // *     returns 3: true
    // *     example 4: empty({});    // *     returns 4: true
    // *     example 5: empty({'aFunc' : function () { alert('humpty'); } });
    // *     returns 5: false
    var key;
     if (mixed_var === "" || mixed_var === 0 || mixed_var === "0" || mixed_var === null || mixed_var === false || typeof mixed_var === 'undefined') {
        return true;
    }
 
    if (typeof mixed_var == 'object') {        for (key in mixed_var) {
            return false;
        }
        return true;
    } 
    return false;
}

function _removeSpecialChars(s) {
	s = s.replace(/[^a-zA-Z0-9]/g,'');
   return s;
}

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
	publication_interhash = $("#openAccessCurrentPublicationInterHash").val();
	
	// init Classification 
	initClassifications(divClassificationSelectName, divClassificationListName);
	
}

function sentPublicationToRepository (elementId, intraHash){
	if (document.getElementById('authorcontractconfirm').checked) {
		var loadingNode = document.createElement('img');
		loadingNode.setAttribute('src', '/resources_puma/image/ajax-loader.gif');
		
		var url = swordURL +"?resourceHash=" + intraHash;

		if (isMetadataChanged()) sendAdditionalMetadataFields(false);

		$.ajax({
			url: url,
			dataType: 'json',
			beforeSend: function(XMLHttpRequest) {
			
				// remove node #swordresponse
				$('#swordresponse').remove();
				$('#pumaSword').append(loadingNode);
				$('#oasendtorepositorybutton').addClass("oadisabledsend2repositorybutton");
				document.getElementById(elementId).disabled = true; 
				
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
						// FIXME: Show error without alert box
						//alert ("unknown response error");
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
						
						// on error enable button
						if (data.response.statuscode == 0) {				
							$(elementId).removeClass("oadisabledsend2repositorybutton");
							document.getElementById(elementId).disabled = false; 
						}

						// show response text
						
					}
				});


			},
			error: function(req, status, e) {
				$(loadingNode).remove();
				// FIXME: Show error without alert box
				//alert("Unable to send data to reposity: " + status);
			}
		});
	} // end of if ($('#authorcontractconfirm').checked)

	


}


/* open access check */
/* TODO: add error handling, check apicontrol and outcome in response. */
function checkOpenAccess () {
	
	if ($("#oasherparomeo").length>0) {		
		var container = $("#oasherparomeo");	
		
		// TODO: add progress animation
		var url = oaBaseUrl+$("#oaRequestPublisherUrlParameter").val();
	
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
				// FIXME: Show error without alert box
				//alert("check open access: " + status);
			}
		});
	}
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
			// FIXME: Show error without alert box
			//alert("There seems to be an error in the ajax request, classifications.js::init");
		}
	});
}

function doInitialise(divClassificationSelectName, divClassificationListName, data) {
	$.each(data.available, function(i,it){

		var saveNode = document.createElement('div');
		saveNode.setAttribute('id', it.name +'saved');
		
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
		input.setAttribute('id',it.name +'_input');
		input.setAttribute("class", "classificationInput");

		mainNode.appendChild(input);
		mainNode.appendChild(helpNode);

		$('#' +divClassificationListName).append(saveNode);
		$('#' +divClassificationSelectName).append(mainNode);
		
		populate(it.name,it.name);
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
			// FIXME: Show error without alert box
			//alert("There seems to be an error in the ajax request, classifications.js::populate");
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
			// FIXME: Show error without alert box
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
	node.className = "classificationListItemContainer";
	var saveListItem = document.createElement('div');
	var classificationId=_removeSpecialChars(classificationName+ClassificationValue);
	saveListItem.setAttribute('id', "classificationListItemElement"+classificationId);
	saveListItem.setAttribute('class', 'classificationListItem');
	
	var remove = document.createElement('input');
	remove.type = 'button';
	remove.className = 'ajaxButton btnspace';
	remove.value = getString("post.resource.openaccess.button.removeclassification");
	remove.id	= "classificationListItemRemove"+classificationId;

		
	var dCnode = document.createElement('div');
	dCnode.setAttribute("class", "classificationListItemDescriptionContainer");

	var description = document.createElement('div');
	description.setAttribute("id", "classificationListItemElementDescription"+classificationId);
	description.setAttribute("class", "classificationListItemDescription");

	node.appendChild(saveListItem);
	dCnode.appendChild(description);
	dCnode.appendChild(remove);
	node.appendChild(dCnode);

	$('#'+classificationName +'saved').append(node);

	$('#'+"classificationListItemElement"+classificationId).text(classificationName +' ' +ClassificationValue +' ');
	
	var descriptionurl = classificationURL + "?action=" +GET_CLASSIFICATION_DESCRIPTION+"&key="+classificationName+"&value="+ClassificationValue;
	$.ajax({
		dataType: 'json',
		url: descriptionurl,
		success: function(data) {
			$('#classificationListItemElementDescription'+_removeSpecialChars(data.name+data.value)).text(data.description);
		},
		error: function(req, status, e) {
			$('#classificationListItemElementDescription'+classificationId).text("-");
		}
	});					

	
	
	remove.onclick = function() {
		var loadingNode = document.createElement('img');
		loadingNode.setAttribute('src', '/resources_puma/image/ajax-loader.gif');
		
		var removeurl = classificationURL + "?action=" +REMOVE_CLASSIFICATION_ITEM+"&hash="+publication_intrahash+"&key="+classificationName+"&value="+ClassificationValue;
		
		$.ajax({
			dataType: 'json',
			url: removeurl,
			beforeSend: function(XMLHttpRequest) {
				$('#classificationListItemRemove'+classificationId).parent().append(loadingNode);
				
			},
			success: function(data) {
				$(node).remove();
				$(loadingNode).remove();
			
			},
			error: function(req, status, e) {
				$(loadingNode).remove();
				// FIXME: Show error without alert box
				//alert("There seems to be an error in the ajax request, classifications.js::createSubSelect");
			}
		});					
		
		
		
	};
}


function addSaved(container, parentID, description) {

	/*
	 * add only a new item, if it does not exist. 
	 * $().length / if length is 0, element does not exist 
	 */
	if (!$("#classificationListItemElement"+_removeSpecialChars(container+parentID)).length){

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
				// FIXME: Show error without alert box
				//alert("There seems to be an error in the ajax request, classifications.js::createSubSelect");
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
				// FIXME: Show error without alert box
				//alert("There seems to be an error in the ajax request, classifications.js::createSubSelect");
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
	sendAdditionalMetadataFields(true);
}
function sendAdditionalMetadataFields(async) {
	if ( async != true ) async = false;
	
	var ElementId = "sendMetadataMarker";
	var mdf = Array();
	var i=0;
	
	mdf = getMetadatafields();
	
	var collectedMetadataJSONText = '{ ';
	var collectedMetadataJSON = {};
	for(var i = 0; i < mdf.length; i++) {
		collectedMetadataJSONText += '"' + mdf[i] + '":"' + $("#"+(mdf[i].replace(/\./g,'\\.'))).val() +'", '; 
	}
	collectedMetadataJSONText += " } ";
	
	// send item via ajax to database
	var saveurl = classificationURL;
	var loadingNode = document.createElement('img');
	loadingNode.setAttribute('src', '/resources_puma/image/ajax-loader.gif');
	
	// send metadata
		$.ajax({
			dataType: 'json',
			url: saveurl,
			async: async,
			data: { "action" : SAVE_ADDITIONAL_METADATA, 
			        "hash"   : publication_intrahash,
					"value"  : collectedMetadataJSONText 
			},
			type: 'post',
			
			beforeSend: function(XMLHttpRequest) {
				$('#' +elementId).append(loadingNode);
				
			},
			success: function(data) {
				$(loadingNode).remove();
				setMetadataChanged(false);

			
			},
			error: function(req, status, e) {
				$(loadingNode).remove();
				// FIXME: Show error without alert box
				//alert("There seems to be an error in the ajax request, classifications.js::createSubSelect");
			}
		});

}


function loadAdditionalMetadataFields() {
	// get data
	// example data set: {"ACM":["C21","C22"],"JEL":["F41"]}
	var url = classificationURL + "?action="+GET_ADDITIONAL_METADATA+"&hash="+publication_intrahash;
	// perform ajax request
	$.ajax({
		dataType: 'json',
		url: url,
		success: function(data) {
			// iterate over data
			$.each(data, function(classification,classificationData){
				$.each(classificationData, function(j,item){
					$("#"+(classification.replace(/\./g,'\\.'))).val(item);
				});		
			});
		},
		error: function(req, status, e) {
			// FIXME: Show error without alert box
			//alert("There seems to be an error in the ajax request, openaccess.js::loadStoredClassificationItems");
		}
	});		
	
}

/* Load send to repository dates of publication 
 * Result may be from another similar publication (interhash), if another user has sent this publication alreadey to. 
 * */
function loadSentRepositories() {
	// get data
	/* {"posts":
			{"5098b2741b211a04f2d3de9b48d8ff37":
				{
					"repositories":[
						{
						"date":{"date":1,"day":2,"hours":18,"minutes":25,"month":1,"seconds":39,"time":1296581139000,"timezoneOffset":-60,"year":111},
						"id":"REPOSITORY_1"
						},
						{
						"date":{"date":1,"day":2,"hours":18,"minutes":25,"month":1,"seconds":39,"time":1296581139000,"timezoneOffset":-60,"year":111},
						"id":"REPOSITORY_1"
						}

					],
					"selfsent": 1,
					"intrahash":"5098b2741b211a04f2d3de9b48d8ff37"
				}
			},
			...
		}
			
		*/
	var url = oaBaseUrl + "?action="+GET_SENT_REPOSITORIES+"&interhash="+publication_interhash;
	// perform ajax request
	$.ajax({
		dataType: 'json',
		url: url,
		success: function(data) {
			// iterate over data
			if (!empty(data.posts)) { 
				$("#oaRepositorySent").append('<div id="oaRepositorySentHeader">'+getString("post.resource.openaccess.repository.sent.info")+':</div>');
				$.each(data.posts, function(intrahash,post){
					$.each(post.repositories, function(key,item){
						var sentDate = new Date(item.date.time);
						var sentDateFormatted = sentDate.getDate() + "." + (sentDate.getMonth()+1) + "." +  sentDate.getFullYear();
						var publicationsVersions =  getString("post.resource.openaccess.repository.sent.versions");
						$("#oaRepositorySent").append('<div>'+getString("post.resource.openaccess.repository.sent.date")+': '+sentDateFormatted+'. <a href="/bibtex/2'+intrahash+'">'+ publicationsVersions +'</a>'+(post.selfsent==1?"":+getString("post.resource.openaccess.repository.sent.other"))+'</div');
					});		
				});
				// set message in headline of open access box and close box
				$("#oaRepositorySentInfo").append('<div>'+getString("post.resource.openaccess.repository.sent.info")+'.</div>');
				foldUnfold('openAccessContainer');
			}			
		},
		error: function(req, status, e) {
			// FIXME: Show error without alert box
			//alert("There seems to be an error in the ajax request, openaccess.js::loadStoredClassificationItems");
		}
	});		
	
}

function autoSaveMetadata(value) {
	booleanValue = value?true:false;
	//console.log("autoSaveMetadata:"+autoSaveMetadataCounter);
	if (booleanValue) {
		// is counter already runnning?
		if (autoSaveMetadataCounter > 0 ) {
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
		autoSaveMetadataCounter=0;
	} else {
		setTimeout("autoSaveMetadata()",700);
	}
	
}


function setMetadataChanged(value) {
	booleanValue = value?true:false;
	elementId = "sendMetadataMarker";
	elementClass = "highlight";
	if (booleanValue==true) {
		// add class to tag
		if (!$("#"+elementId).hasClass(elementClass)) $("#"+elementId).addClass(elementClass);
		// start autosave counter
		setTimeout("autoSaveMetadata(true)", 100);
	} else {
		// remove class from tag
		if ($("#"+elementId).hasClass(elementClass)) $("#"+elementId).removeClass(elementClass);
	}

	metadataChanged = booleanValue;
}
function isMetadataChanged() {
	return metadataChanged;
}

function loadStoredClassificationItems()
{
	// clear list 
	
	// get data
	// example data set: {"ACM":["C21","C22"],"JEL":["F41"]}
	var url = classificationURL + "?action="+GET_POST_CLASSIFICATION_LIST+"&hash="+publication_intrahash;
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
					if (!$("#classificationListItemElement"+_removeSpecialChars(classification+item)).length){
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

function setBackgroundColor( container, color ) {
	$("#" +container).css("background-color", color);
}


function checkauthorcontractconfirm() {

	if (document.getElementById('authorcontractconfirm').checked) {
		if ($('#oasendtorepositorybutton').hasClass("oadisabledsend2repositorybutton")) {
			$('#oasendtorepositorybutton').removeClass("oadisabledsend2repositorybutton");
			document.getElementById('oasendtorepositorybutton').disabled=false;
		}
	} 
	else
	{
		if (!$('#oasendtorepositorybutton').hasClass("oadisabledsend2repositorybutton")) {
			$('#oasendtorepositorybutton').addClass("oadisabledsend2repositorybutton");
			document.getElementById('oasendtorepositorybutton').disabled=true;
		}
	}
	
	
}
