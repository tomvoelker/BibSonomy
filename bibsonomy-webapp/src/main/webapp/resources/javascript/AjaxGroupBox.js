var ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR = 'input[name="abstractGrouping"]';
var OTHER_GROUPING_CLASS_SELECTOR = '.otherGroupsBox';

var GROUPS_CLASS = 'groups';
var PUBLIC_GROUPING = 'public';
var PRIVATE_GROUPING = 'private';
var OTHER_GROUPING = 'other';
var FRIENDS_GROUP_NAME = 'friends';


$(document).ready(function () {	
	
	// TODO: move and use in post edit views
	$(ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR).change(onAbstractGroupingClick);
	$.each($('.abstractGroupingGroup'), function(index, box) {
		toggleGroupBox(box);
	});
	
});


// TODO: rename

// TODO: move and use in post edit views
function onAbstractGroupingClick() {
	toggleGroupBox($(this));
}

// TODO: move and use in post edit views
function toggleGroupBox(radioButton) {
	// find the checked abstract grouping
	var selectedAbstractGrouping = $(radioButton).val();
	if(selectedAbstractGrouping!='other'){
		$(radioButton).parents('div').next().find('select[name=groups]').attr('disabled', 'disabled');
	} else {
		$(radioButton).parents('div').next().find('select[name=groups]').removeAttr('disabled');
	}
}
function clearSelected(element){
	var Options = element.options;
 
    for(var i = 0; i < Options.length; i++){
      Options[i].selected = false;
    }
  };