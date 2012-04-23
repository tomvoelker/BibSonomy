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

function hasGoldstandardCreationPermission() {
	if (!pub) {
		// we need to ask whether or not a goldstandard is to be created.
		if (!confirm(getString('post.resource.discusssion.warning.goldstandard.continue'))) {
			return false;
		}
	}
	return true;
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
	$(REVIEW_CREATE_FORM_SELECTOR).parent().hide();
	$(REPLY_FORM_SELECTOR).remove();
}

function showReviewForm() {
	$(REVIEW_CREATE_FORM_SELECTOR).parent().show();
}

function removeReviewActions() {
	$(CREATE_REVIEW_LINKS_SELECTOR).parent().hide();
	// create review form
	$(REVIEW_CREATE_FORM_SELECTOR).parent().remove();
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
	return $(DISCUSSION_SELECTOR).data("interHash");
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
	var matches = new Array();
	var links = new Array();
	var reg = /\[\[(?:(bookmark|url|bibtex|publication)(?:\/))?([0-9a-fA-F]{32,33})(?:\/(.*?))?\]\]/gi;
	var match;// = reg.exec(reviewText);
	while (match = reg.exec(reviewText)) {
		var url;
		if(match[1] == "url" || match[1] == "bookmark") {
			url = "/url/"
		} else {
			url = "/bibtex/";
		}
		url += match[2];
		if(typeof match[3] != "undefined") {
			url += "/" + match[3];
		}
		matches.push(match[0]);
		links.push("<a href=\"" + url + "\">" + match[0] + "</a>");
	}
	for (var i = 0; i < matches.length; i++) {
		reviewText = reviewText.replace(matches[i], links[i]);
	}
	//TODO handle csl
	return $("<div class=\"review text\" itemprop=\"reviewBody\">" + reviewText + "<div>");
}
