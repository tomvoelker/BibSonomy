var ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR = 'input[name="abstractGrouping"]';
var OTHER_GROUPING_CLASS_SELECTOR = '.otherGroupsBox';

var DISCUSSION_MENU_SELECTOR = '#discussionMainMenu';
var DISCUSSION_SELECTOR = '#discussion';
var REVIEW_INFO_SELECTOR = '#review_info_rating';
var DISCUSSION_TOGGLE_LINK_SELECTOR = '#toggleDiscussion';

var REVIEW_OWN_ID = 'ownReview';
var REVIEW_OWN_SELECTOR = '#' + REVIEW_OWN_ID;

var REVIEW_UPDATE_FORM_SELECTOR = 'form.editreview';
var REPLY_FORM_ID = 'replyForm';
var REPLY_FORM_SELECTOR = '#' + REPLY_FORM_ID;

var EDIT_COMMENT_FORM_ID = 'editcomment';
var EDIT_FORM_SELECTOR = '#' + EDIT_COMMENT_FORM_ID;

var CREATE_REVIEW_LINKS_SELECTOR = 'a.createReview';
var COMMENT_CREATE_FORM = '#createCommentInstance';
var REVIEW_CREATE_FORM_SELECTOR = 'form.createreview';

var ANONYMOUS_CLASS = 'anonymous';

var ANONYMOUS_SELECTOR = 'input[name=discussionItem\\.anonymous]';

// TODO: move constants
var BOOKMARK_LIST_SELECTOR = '#bookmarkList';
var PUBLICATION_LIST_SELECTOR = '#bibtexList';
var GROUPS_CLASS = 'groups';
var PUBLIC_GROUPING = 'public';
var PRIVATE_GROUPING = 'private';
var OTHER_GROUPING = 'other';
var FRIENDS_GROUP_NAME = 'friends';

var DISCUSSIONITEM_DATA_KEY = 'discussionItemHash';

var PUBLIC_POST_SELECTOR = 'input#publicInput';

var pub = true;

$(function() {	
	// remove all create review links if user already reviewed resource
	if ($(REVIEW_OWN_SELECTOR).length > 0) {
		// user has reviewed this resource hide all create review forms
		removeReviewActions();
		$(COMMENT_CREATE_FORM).show();
		
		$(document).ready(function() {
			createStandaloneReply($("#createCommentInstance"));
		});
	}
	
	$(DISCUSSION_TOGGLE_LINK_SELECTOR).click(function() {
		$(REVIEW_INFO_SELECTOR).toggle('slow');
		$(DISCUSSION_SELECTOR).toggle('slow', updateDiscussionToggleLink);
		return false;
	});
	
	// TODO: move and use in post edit views
	$(ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR).click(onAbstractGroupingClick);
	 
	$.each($('.abstractGroupingGroup'), function(index, box) {
		toggleGroupBox(box);
	});
	var publicValue = $(PUBLIC_POST_SELECTOR).val();
	// if there is a publicPost item use it to determine warnings
	if ((publicValue != undefined) && (publicValue=='false')) {
		pub = false;
	}
	if (!pub) {
		alert(getString('post.resource.discusssion.warning.goldstandard'));
	}

});

function showAppendixForm(o) {
	o.parent = o.menuItem.parents(".fsOuter");
	o.bgFrame = o.menuItem.parent().parent().children(".discussionAdditionalControls");
	
	if(o.bgFrame[0]==undefined) return;
	
	if(o.bgFrame.css("display") != "none") {
		o.menuItem
		.removeClass("linkButton")
		.parent()
		.children(".controlsContainer")
		.hide()
		.parent()
		.css("z-index",0); 
		
		return o.bgFrame.hide();
	}

	var frameClickCallback = function() {
		o.bgFrame.unbind("click", frameClickCallback);
		o.menuItem.trigger("click");
	};
	
	o.bgFrame.bind("click", frameClickCallback);
	o.menuItem.addClass("linkButton").parent().css("z-index",5);
	o.ctrlsContainer = o.menuItem.parent().children(".controlsContainer");
	o.bgFrame.width(o.parent.width()).height(o.parent.height()).css({"top":0,"left":0}).show();
	o.ctrlsContainer.show().css("left", ""+((o.menuItem.position().left+o.menuItem.width()/2)-o.ctrlsContainer.width()/2)+"px")
	.css({
		"left": ""+((o.menuItem.position().left+o.menuItem.width()/2)-o.ctrlsContainer.width()/2)+"px", 
		"top":""+(o.menuItem.position().top+o.menuItem.height()+10)+"px"
	});

	if(o.callback!=undefined) o.callback(
			{
				bgFrame:o.bgFrame, 
				menuItem:o.menuItem, 
				ctrlsContainer: o.ctrlsContainer
			}
	);
}

$(document).ready(function() {
	$('.descriptiveLabel').each(function() {
		$(this).descrInputLabel({});
	});
	$('.textAreaAutoresize').each(function() {
		$(this).autosize({}).focus(showMenu);
	});
	$('.reviewrating').stars({split:2});
});

function setUpLinkbox(o) {
	o.textArea = o.bgFrame.parents('.fsOuter').children('.textBoxContainer').find(".textAreaAutoresize");
	o.textArea.css({"z-index":5,"position":"relative"});
	o.ctrlsContainer.find('input').trigger("focus");
	o.menuItem.css("position","");
	o.refInput = o.ctrlsContainer.find(".referenceAutocompletion");
	var callback = function() {
		o.refInput.val('');
		o.textArea.css({"z-index":0,"position":""});	
		o.menuItem.unbind("click", callback);
	};
	
	o.menuItem.bind("click", callback);
}

function showMenu(e) {
	$(e.target).unbind("focus", showMenu).removeClass('textAreaWithMinHeightSmall').addClass('textAreaWithMinHeightLarge').height('').parent().parent().parent();
	
	var parent = $(e.target).unbind("focus", showMenu).parent().parent().parent();
	var frames = ["discussionControlsFrame", "discussionPostbuttonFrame"];
	var rating = getOwnReviewRating!=undefined && !isNaN((rating = getOwnReviewRating()))?parseFloat(rating):0;
	var reviewRating = parent.find(".reviewrating");
	
	for(var i = 0; i < frames.length; i++) parent.children("."+frames[i]).css("display", "block").hide().fadeIn();
	
	if(!reviewRating.hasClass('ratingDisabled'))	reviewRating.stars("select", rating.toFixed(1));
}

function hasGoldstandardCreationPermission() {
	if (!pub) {
		// we need to ask whether or not a goldstandard is to be created.
		if (!confirm(getString('post.resource.discusssion.warning.goldstandard.continue'))) {
			return false;
		}
	}
	return true;
}

function createStandaloneReply(parent) {
		removeAllOtherDiscussionForms();
		var parentHash = getHash($("#discussionRef"));
		var form = parent.find("form");
		form.append($('<input />').attr('name', 'discussionItem.parentHash').attr('type', 'hidden').attr('value', parentHash));
		
		// bind some actions (submit, group switch)
		form.submit(createComment);
		form.find(ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR).click(onAbstractGroupingClick);
		addAutocompletionToLinkBox(form);
}

function updateDiscussionToggleLink() {
	var visible = $(DISCUSSION_SELECTOR).is(':visible');
	var text = getString('post.resource.discussion.actions.show');
	if (visible) {
		text = getString('post.resource.discussion.actions.hide');
	}
	
	$(DISCUSSION_TOGGLE_LINK_SELECTOR).text(text);
}

function showDiscussion() {
	$(DISCUSSION_SELECTOR).show();
	$(REVIEW_INFO_SELECTOR).show();
	updateDiscussionToggleLink();
}

// TODO: rename
function removeAllOtherDiscussionForms() {
	$(EDIT_FORM_SELECTOR).remove();
	$(REVIEW_UPDATE_FORM_SELECTOR).hide();
	//$(REVIEW_CREATE_FORM_SELECTOR).parent().hide();
	$(REPLY_FORM_SELECTOR).remove();
}

function showReviewForm() {
	$(REVIEW_CREATE_FORM_SELECTOR).parent().show();
}

function removeReviewActions() {
	$(CREATE_REVIEW_LINKS_SELECTOR).parent().hide();
	// create review form
	$(REVIEW_CREATE_FORM_SELECTOR).parent().hide();
}

function addReviewActions() {
	$(CREATE_REVIEW_LINKS_SELECTOR).parent().show();
}

// TODO: move and use in post edit views
function onAbstractGroupingClick() {
	toggleGroupBox($(this).parent());
}

// TODO: move and use in post edit views
function toggleGroupBox(radioButtonGroup) {
	// find the checked abstract grouping
	var selectedAbstractGrouping = $(radioButtonGroup).children('input:checked');
	
	// find otherGroupsBox of the abstract grouping
	var otherBox = $(radioButtonGroup).siblings(OTHER_GROUPING_CLASS_SELECTOR);
	
	// disable groups select if private or public is checked or enable
	// if other is checked
	if (!selectedAbstractGrouping.hasClass('otherGroups')) {
		otherBox.attr('disabled', 'disabled');
	} else {
		otherBox.removeAttr('disabled');
	}
}

function populateFormWithGroups(form, abstractGrouping, groups) {
	// populate form with found values
	form.find(ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR).removeAttr('checked');
	form.find(ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR + '[value="' + abstractGrouping + '"]').attr('checked', 'checked');
	
	var otherBox = form.find(OTHER_GROUPING_CLASS_SELECTOR);
	// and other groups if present
	if (groups.length > 0) {
		otherBox.removeAttr('disabled');
		// clear
		otherBox.find('input').removeAttr('selected');
		$.each(groups, function(index, group) {
			otherBox.find('[value="' + group + '"]').attr('selected', 'selected');
		});
	} else {
		otherBox.attr('disabled', 'disabled');
	}
}

function getGroups(item) {
	var groupContainer = item.find('.groups:first');
	var groupingText = groupContainer.text();
	
	var groups = new Array();
	// if text empty => public group
	if (groupingText != '') {			
		var groupViews = groupContainer.find('a');
		
		if ((groupViews.length == 0) && (groupingText.indexOf(getString('post.groups.private')) != -1)) {
			// private
			return groups;
		}
		
		// friends
		if (groupingText.indexOf(getString('post.groups.friends')) != -1) {
			groups.push(FRIENDS_GROUP_NAME);
		}
		
		$.each(groupViews, function(index, groupView) {
			groups.push($(groupView).text());
		});
		
		return groups;		
	}
	
	return groups;
}

function getAbstractGrouping(item) {
	var groupContainer = item.find('.groups:first');
	var groupingText = groupContainer.text();
	
	// if text empty => public group
	if (groupingText != '') {			
		var groupViews = groupContainer.find('a');
		
		if (groupViews.length == 0) {
			// private or friends	
			if (groupingText.indexOf(getString('post.groups.private')) != -1) {
				return PRIVATE_GROUPING;
			}
			// friends
			return OTHER_GROUPING;
		}
		
		// multiple groups
		return OTHER_GROUPING;			
	}
	
	return PUBLIC_GROUPING;
}

function buildGroupView(abstractGrouping, groups) {
	// check if not public
	if (abstractGrouping !== 'public') {
		var container = $('<span></span>').addClass(GROUPS_CLASS);
		container.append(getString('post.resource.comment.groups') + ' ');
		// if private only private group
		if (abstractGrouping == 'private') {
			container.append(getString('post.groups.private'));
		} else {
			// other group box
			var groupLength = groups.length;
			$.each(groups, function(index, group) {		
				// special handling for friends
				if (group == FRIENDS_GROUP_NAME) {
					container.append(getString('post.groups.friends'));
					return;
				}
				var groupUrl = $('<a></a>').attr('href', '/group/' + group);
				groupUrl.html(group);
				container.append(groupUrl);
				
				// separate groups with ', '
				if (groupLength != 1 && (groupLength - 1) == index) {
					container.append(', ');
				}
			});
		}
		return container;
	}
	return '';
}

function deleteDiscussionItemView(discussionItemView, success) {
	if (discussionItemView.find('ul.subdiscussionItems:first li').length > 0) {
		discussionItemView.removeAttr('id');
		discussionItemView.removeClass();
		discussionItemView.addClass('discussionitem');
		
		discussionItemView.find('img:first').remove();
		discussionItemView.find('.details:first').remove();
		discussionItemView.find('.createReview:first').parent().remove();
		discussionItemView.find('.deleteInfo:first').parent().remove();
		discussionItemView.find('a.editLink:first').parent().remove();
		discussionItemView.find('a.reply:first').parent().remove();
		
		var info = $('<div class="deletedInfo"></div').text(getString('post.resource.discussion.info'));
		discussionItemView.prepend(info);
		
		highlight(info);
		
		if (success != undefined) {
			success();
		}
	} else {
		discussionItemView.fadeOut(1000, function() {
				$(this).remove();
				if (success != undefined) {
					success();
				}
		});
	}
}

function updateHash(element, newHash) {
	$(element).find('div.info:first').data(DISCUSSIONITEM_DATA_KEY, newHash);
	$(element).find('input[name="discussionItem\\.hash"]:first').attr('value', newHash);
}

// TODO: rename function
function getInterHash() {
	return $(DISCUSSION_SELECTOR).data("interhash");
}

// TODO: rename function
function getHash(menuElement) {
	return $(menuElement).parent().parent().siblings('.details').find('.info').data(DISCUSSIONITEM_DATA_KEY);
}

function highlight(element) {
	$(element).css('background-color', '#fff735').animate({ backgroundColor: '#ffffff' }, 1000);
}

/*
 * scrolls to the specified id
 * TODO: move function for reuse
 */
function scrollTo(id){
	var element = $("#" + id);
	if (element.length) {
		$('html,body').animate({scrollTop: element.offset().top - 100 },'slow');
	}
}

function parseLinks(reviewText) {
	var originalText = reviewText;
	var matches = new Array();
	var links = new Array();
	var reg = /\[\[(?:(bookmark|url|bibtex|publication)(?:\/))?([0-9a-fA-F]{32,33})(?:\/(.*?))?\]\]/gi;
	var match;
	var changed = false;
	while (match = reg.exec(reviewText)) {
		if(matches.indexOf(match[0]) != -1) {
			continue;
		}
		changed = true;
		var url;
		if(match[1] == "url" || match[1] == "bookmark") {
			url = "/url/"
		} else {
			url = "/bibtex/";
		}
		url += match[2];
		var name = match[3];
		if(typeof name == "undefined") {
			name = "";
		} else  {
			name = "/" + name;
			url += name;
		}
		matches.push(match[0]);
		links.push("<a class=\"postlink\" id=\"" + match[2] + name + "\" href=\"" + url + "\">" + match[0] + "</a>");
	}
	for (var i = 0; i < matches.length; i++) {
		reviewText = reviewText.replaceAll(matches[i], links[i], true);
	}
	
	var ret =  "<div class=\"review text\" itemprop=\"reviewBody\">" + reviewText + "</div>\n";
	if(changed) {
		ret += "<div class=\"originalText\" style=\"display:none\">" + originalText + "</div>";
	}
	return ret;
}


//functions for redesigned page  
$(function(){
	var hash = window.location.hash;
	var gsPresent = ($("#gs_present").val()=="true");
	if(hash=="#discussionbox" && !gsPresent) {
		$("#hideableContent").hide();
		$("#imgExpandDiscussion").hide();
		$("#imgCollapseDiscussion").show();
		
		$("#textExpandDiscussion").hide();
		$("#textCollapseDiscussion").show();
		
		
		$(".imgCollapse").each(function(){
			if($(this).attr("id") == "imgCollapseContent") {
				$(this).hide();
			}
		});
		
		$(".imgExpand").each(function(){
			if($(this).attr("id") == "imgExpandContent") {
				$(this).show();
			}
		});
		
		//Fix to redefine the Sidebar height
		$('#sidebar').height($('#postcontainer').height() + 11);
		
	} else if (!gsPresent){
		$("div#discussion").hide();
		$("#imgExpandDiscussion").show();
		$("#imgCollapseDiscussion").hide();
		$("#textExpandDiscussion").show();
		$("#textCollapseDiscussion").hide();
		$("#imgExpandContent").hide();
		$("#imgCollapseContent").show();
		
		//Fix to redefine the Sidebar height
		$('#sidebar').height($('#postcontainer').height() + 11);

	}
});


$(document).ready(function() {
	
	numberOfBookmarkLists = $(".bookmarksContainer").size(); // every id bookmarks_* must have a class bookmarksContainer
	numberOfPublicationLists = $(".publicationsContainer").size(); // every id publications_* must have a class publicationsContainer

	if (numberOfBookmarkLists != 0) {
		$("#bookmarks_"+(numberOfBookmarkLists-1)).height("auto");
	}
	
	if (numberOfBookmarkLists != 0) {
		$("#publications_"+(numberOfPublicationLists-1)).height("auto");
	}

	$("a.foldUnfold").click(function(){
		$('#sidebar').height("auto");
		$(".posts, .wide").height("auto");

		var selector = $(this).attr("href");
		var resource = $(selector);
		if(resource.is(":visible")) {
			resource.hide();
			$(this).find(".imgCollapse").hide();
			$(this).find(".imgExpand").show();
			return false;
		}
		resource.show();
		$(this).find(".imgCollapse").show();
		$(this).find(".imgExpand").hide();
		
		//Fix to redefine the Sidebar height
		$('#sidebar').height($('#postcontainer').height() + 11);
		return false;
	});
	
	initCSLSugestions($("input.referenceAutocompletion"));

});

function initCSLSugestions(el) {
	el.each(function(index){ $(this).autocomplete({
		source: function( request, response ) {

			$.ajax({
				url: "/json/tag/" + createParameters(request.term),
				data: {items: 10,resourcetype: 'publication', duplicates: 'no'},
				dataType: "jsonp",
				success: function( data ) {
					response( $.map( data.items, function( item ) {
						return {
							label: (highlightMatches(item.label, request.term)+' ('+item.year+')'),
							value: item.interHash,
							url: 'hash='+item.intraHash+'&user='+item.user+'&copytag='+item.tags,
							author: (concatArray(item.author, 40, ' '+getString('and')+' ')),
							user: item.user,
							tags: item.tags
						};
					}));
				}
			});
		},
		minLength: 3,
		select: function( event, ui ) {
			var item = ui.item;
			var textArea = $(event.target);
			var text = "[[publication/" + item.value + "/" + item.user + "]]";
			textArea.val(text);
			textArea.select();
			return false;
		},
		focus: function( event, ui ) {
			return false;
		}
	})
	.data( 'autocomplete' )._renderItem = function( ul, item ) {
		return $('<li></li>')
		.data( 'item.autocomplete', item )
		.append(
				$('<a></a>')
				.html(	item.label+'<br><span class="ui-autocomplete-subtext">' 
						+item.author+' '+getString('by')+' '
						+item.user+'</span>'))
						.appendTo( ul );
	};
	});
};


function highlightMatches(text, input) {
	var terms = input.split(" ");
	for ( var i = 0; i < terms.length; i++) {
		text = highlightMatch(text, terms[i]);
	}
	return text;
};

function highlightMatch(text, term) {
	return text.replace(new RegExp("(?![^&;]+;)(?!<[^<>]*)(" + $.ui.autocomplete.escapeRegex(term) + ")(?![^<>]*>)(?![^&;]+;)", "gi"), "<strong>$1</strong>");
};