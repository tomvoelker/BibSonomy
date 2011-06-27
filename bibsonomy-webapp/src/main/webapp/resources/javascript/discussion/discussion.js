var ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR = 'input[name="abstractGrouping"]';
var OTHER_GROUPING_CLASS_SELECTOR = '.otherGroupsBox';
var DISCUSSION_TABS_SELECTOR = '#discussionForms';
var DISCUSSION_MENU_SELECTOR = '#discussionMainMenu';
var DISCUSSION_SELECTOR = '#discussion';
var REVIEW_REPLY_FORM_ID = 'replyReview';
var REVIEW_REPLY_FORM_SELECTOR = '#' + REVIEW_REPLY_FORM_ID;

// TODO: move constants
var BOOKMARK_LIST_SELECTOR = '#bookmarkList';
var PUBLICATION_LIST_SELECTOR = '#bibtexList';
var GROUPS_CLASS = 'groups';
var PUBLIC_GROUPING = 'public';
var PRIVATE_GROUPING = 'private';
var OTHER_GROUPING = 'other';
var FRIENDS_GROUP_NAME = 'friends';

var DISCUSSIONITEM_DATA_KEY = 'discussionItemHash';

$(function() {	
	// tabs for forms
	$(DISCUSSION_TABS_SELECTOR).tabs();
	if ($('#ownReview').length > 0) {
		// user has reviewed this resource hide all create review forms
		removeReviewActions();
	}
	
	var location = document.location.toString();
	var anchor = "#";
	if (location.match('#')) { // the URL contains an anchor
	  // click the navigation item corresponding to the anchor
	  anchor += location.split('#')[1];
	}
	
	// hide discussion on list pages
	if (anchor != DISCUSSION_SELECTOR && ($(PUBLICATION_LIST_SELECTOR).length > 0 || $(BOOKMARK_LIST_SELECTOR).length > 0)) {
		$(DISCUSSION_SELECTOR).hide();
		$(DISCUSSION_MENU_SELECTOR).hide();
		$(DISCUSSION_TABS_SELECTOR).hide();
	} else {
		$('#toggleDiscussion a').text(getString('post.resource.discussion.actions.hide'));
	}
	
	$('#toggleDiscussion a').click(function() {
		var visible = $(DISCUSSION_SELECTOR).is(':visible');
		$(DISCUSSION_SELECTOR).toggle('slow');
		$(DISCUSSION_TABS_SELECTOR).toggle('slow');
		$(DISCUSSION_MENU_SELECTOR).toggle('slow');
		
		var text = getString('post.resource.discussion.actions.show');
		if (!visible) {
			text = getString('post.resource.discussion.actions.hide');
		}
		
		$(this).text(text);
	});
	
	/*
	 * TODO: implement me
	$('#toggleReviews').click(function() {
		var nonReviews = $('.subdiscussionItems li').not('.review');
		var onlyReviews = nonReviews.is(':visible');
		if (onlyReviews) {
			$(this).html("show all");
		} else {
			$(this).html("show only reviews");
		}
		nonReviews.toggle('slow');
	});
	
	$('#collapseAllThreads').click(function() {
		// TODO: change label
		// TODO: implement me
		return false;
	});
	*/
	
	// TODO: move and use in post edit views
	$(ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR).click(onAbstractGroupingClick);
	 
	$.each($('.abstractGroupingGroup'), function(index, box) {
		toggleGroupBox(box);
	});
});

function removeReviewActions() {
	var forms = $(DISCUSSION_TABS_SELECTOR).tabs();
	$('a.createReview').parent().hide();
	$('li.createReview').addClass('ui-tabs-hide');
	// select comment tab
	forms.tabs('select', 1);
	
	// reply review form
	$(REVIEW_REPLY_FORM_SELECTOR).remove();
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
				if (group == 'friends') {
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

function updateHash(element, newHash) {
	$(element).find('div.info:first').data(DISCUSSIONITEM_DATA_KEY, newHash);
	$(element).find('input[name="discussionItem\\.hash"]:first').attr('value', newHash);
}

// TODO: rename function
function getInterHash() {
	return $('#discussionForms').data("interHash");
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
	$('html,body').animate({scrollTop: $("#"+id).offset().top},'slow');
}