var oaBaseUrl = "/ajax/checkOpenAccess";
var classificationURL = "/ajax/classificatePublication";
var swordURL = "/ajax/swordService";
var GET_AVAILABLE_CLASSIFICATIONS = "AVAILABLE_CLASSIFICATIONS";

function initialiseOpenAccess(divName, intraHash) {
	var sword = document.createElement('div');
	sword.setAttribute('id','pumaSword');
	
	var saveSword = document.createElement('input');
	saveSword.type = 'button';
	saveSword.value = 'Save to repository';
	sword.appendChild(saveSword);
	
	var url = swordURL +"?resourceHash=" +intraHash;

	var loadingNode = document.createElement('img');
	loadingNode.setAttribute('src', '/resources_puma/image/ajax-loader.gif');
	
	saveSword.onclick = function() {
		$.ajax({
			url: url,
			dataType: 'json',
			beforeSend: function(XMLHttpRequest) {
				$('#pumaSword').append(loadingNode);
				
			},
			success: function(data) {
				$(loadingNode).remove();
			},
			error: function(req, status, e) {
				$(loadingNode).remove();
				alert("Unable to send data to reposity " + status);
			}
		});
	};
	

	var classificate = document.createElement('div');
	classificate.setAttribute('id','classification');

	$('#' +divName).append(sword);
	$('#' +divName).append(classificate);
	
	initClassifications('classification');
	
}

/* open access check */
/* TODO: add error handling, check apicontrol and outcome in response. */
function checkOpenAccess () {
	var container = $("#openAccess");	
	container.empty();

	// TODO: add progress animation
	var url;
	if ($("#post\\.resource\\.entrytype").val() == "article")
		url = oaBaseUrl + "?jTitle=" + $("#post\\.resource\\.journal").val();
	else
		url = oaBaseUrl + "?publisher=" + $("#post\\.resource\\.publisher").val();
	
	$.ajax({
		url: url,
		dataType: 'json',
		success: function(data) {
			/*
			 * build list with publishers
			 */
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
		},
		error: function(req, status, e) {
			alert("check open access: " + status);
		}
	});
}

function initClassifications(mainContainer) {
	var url = classificationURL + "?action=" +GET_AVAILABLE_CLASSIFICATIONS;
	
	$.ajax({
		dataType: 'json',
		url: url,
		success: function(data) {
			doInitialise(mainContainer, data);
		},
		error: function(req, status, e) {
			alert("There seems to be an error in the ajax request, classifications.js::init");
		}
	});
}

function doInitialise(mainContainer, data) {
	$.each(data.available, function(i,item){

		var saveNode = document.createElement('div');
		saveNode.setAttribute('id', item +'saved');

		
		var mainNode = document.createElement('div');
		mainNode.setAttribute('id', item);
		
		var input = document.createElement('input');
		input.setAttribute('type', 'text');
		input.setAttribute('size', '10');
		input.setAttribute('readonly', 'readonly');
		input.setAttribute('id',item +'_input');
		
		mainNode.appendChild(input);

		$('#' +mainContainer).append(saveNode);
		$('#' +mainContainer).append(mainNode);
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

function addSaved(container, parentID, description) {
	var node = document.createElement('div');
	
	var save = document.createElement('input');
	save.type = 'text';
	save.value = container +' ' +parentID +' ';
	
	save.name = 'classification';
	save.setAttribute('readonly', "readonly");
	
	saveName = document.createElement('input');
	saveName.setAttribute('type', 'text');
	saveName.setAttribute('disabled', 'true');
	saveName.setAttribute('value', description);
	
	var remove = document.createElement('input');
	remove.type = 'button';
	remove.value = 'remove';

	node.appendChild(save);
	node.appendChild(remove);
	
	$('#' +container +'saved').append(node);
	
	remove.onclick = function() {
		$(node).remove();
	}
	
}

function createSaveButton(parent, parentID, container) {

	var s = createNode({
		tag: 'input',
		parent: parent,
		child: null,
		type: 'button',
		value: 'save',
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

	$('#' +container +'_input')[0].setAttribute('defaultValue', parentID);
	$('#' +container +'_input')[0].setAttribute('value', parentID);
	
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