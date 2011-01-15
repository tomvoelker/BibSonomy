var oaBaseUrl = "/ajax/classificatePublication";

function populate(classification, container) {

	var url = oaBaseUrl + "?classificationName=" +classification;
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

function addSaved(container, parentID ) {
	var node = document.createElement('div');
	
	var save = document.createElement('input');
	save.type = 'text';
	save.value = parentID;
	save.readonly = 'true';
	
	var remove = document.createElement('input');
	remove.type = 'button';
	remove.value = 'remove';

	node.appendChild(save);
	node.appendChild(remove);
	
	$('#classis').append(node);
	
	remove.onclick = function() {
		$(node).remove();
	}
	
}

function createSaveButton(parent, parentID, container) {
//	var node = document.createElement('input');
//	node.value = 'save';
//	
//	node.type = 'button';
//	node.parent = parent;
//	node.onclick = function() {
//		addSaved(container, parentID);
//	}


	var s = createNode({
		tag: 'input',
		parent: parent,
		child: null,
		type: 'button',
		value: 'save',
		childNodes: null,
		parentID : parentID,
		onclick: function(){

		addSaved(container, parentID);
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

	$('#' +container +'_input')[0].value = parentID;
	
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
		childNodes: options,
		parentID : parentID,
		classification: classification,
		onchange: function(){
		this.deleteChild();
		
		var id = parentID +this.value;
		var url = oaBaseUrl + "?classificationName=" +classification +"&id=" +id;
		
		$.ajax({
			dataType: 'json',
			url: url,
			success: function(data) {
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


//
//function test(classification, id ,next) {
//	alert("check open access: " +id);
//
//	var x = document.getElementById("jelClassification");
//	alert(x);
//	for (var i=0;i<x.length;i++) {
//		alert(i);
//		alert(x.elements[i].value);
//	}
//	
//	alert("deon");
//
//	var url = oaBaseUrl + "?classificationName=" +classification +"&id=" +id;
//	// perform ajax request
//	$.ajax({
//		dataType: 'json',
//		url: url,
//		success: function(data){
//			var html = []
//			
//			
//			// build the options
//			$.each(data.children, function(i,item){
//				html.push('<option value="'+item.id+'">' +item.id + item.description + '</option>');
//			});
//
//			$("#"+next).html(html.join(''));
//
//		},
//		error: function(req, status, e) {
//			alert("check open access: " + status);
//		}
//	});
//}

//function classificate() {
//	
//	var container = $("#classification");	
//	container.empty();
//
//	var url = oaBaseUrl + "?classificationName=JEL";
//	
//	var selectors = [], params;
//	
//	// build a selector for each select box in this context
//	for(var x=0, len=selects.length; x<len; x++){
//		selectors.push('select[name="'+selects[x]+'"]');
//	}
//
//	// take those selectors and serialize the data in them
//	params = $( selectors.join(','), $context ).serialize();
//
//	// disable this select box, add loading msg
//	$select.attr('disabled', 'disabled').html('<option value="">' + o.loadingMessage + '</option>');
//
//	// perform ajax request
//	$.ajax({
//		dataType: 'json',
//		url: url,
//		success: function(data){
//			var html = [], defaultOptionText = $select.data('defaultOption');
//			
//			// set the default option in the select.
//			if(defaultOptionText.length > 0){
//				html.push('<option value="" selected="selected">' + defaultOptionText + '</option>');
//			}
//			
//			// if the value returned from the ajax request is valid json and isn't empty
//			if(o.dataType === 'json' && typeof data === 'object' && data){
//			
//				// build the options
//				$.each(data, function(i,item){
//					html.push('<option value="'+i+'">' + item + '</option>');
//				});
//
//				$select.html( html.join('') ).removeAttr('disabled');
//		
//			// html datatype
//			} else if(o.dataType === 'html' && $.trim(data).length > 0){
//				html.push($.trim(data));
//				$select.html( html.join('') ).removeAttr('disabled');
//		
//			// if the response is invalid/empty, reset the default option and fire the onEmptyResult callback
//			} else {
//				$select.html( html.join('') );
//				if(!o.disableIfEmpty){ $select.removeAttr('disabled'); }
//				o.onEmptyResult.call($caller);
//			}
//		},
//		error: function(req, status, e) {
//			alert("check open access: " + status);
//		}
//	});
//	
//}