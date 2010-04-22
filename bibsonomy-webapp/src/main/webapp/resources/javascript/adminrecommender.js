
$(document).ready(function(){
	deselectAll(document.recConfig.activeRecs);
	deselectAll(document.recConfig.disabledRecs);
});

function pushSelectedOptions(from, to){
  while(from.selectedIndex != -1){
    selectedOption = from.options[from.selectedIndex];
    newOption = new Option(selectedOption.text, selectedOption.value, false, false);

    if(document.all) to.add(newOption, 0);  //for msie
    else to.add(newOption, to.options[0]);

    from.remove(from.selectedIndex);
    from.focus();
  }
}

function selectAll(selectField){
    selectField.focus();
    for(var i=0; i<selectField.length; i++)
    selectField.options[i].selected = true;
}

function deselectAll(selectField){
    selectField.focus();
    for(var i=0; i<selectField.length; i++)
        selectField.options[i].selected = false;
}

function openEditForm(){
	recommenderSelect = document.recommenderRemove.deletesid;
    selectedRecommender = recommenderSelect.options[recommenderSelect.selectedIndex];
    
    hiddenSettingId = document.getElementById("editId");
    hiddenSettingId.value = selectedRecommender.value;
  
    recurl = document.getElementById("editedRecurl");
    recurl.value = selectedRecommender.text.substr(selectedRecommender.text.indexOf('-') + 2);
    
	div = document.getElementById("recommenderEditDiv");
	div.style.display = "block";
	
}