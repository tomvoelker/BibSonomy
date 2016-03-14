$(function() {

	$('.input-group.date').datepicker({
		autoclose : true
	});

	$('.toggleAdvanced').click(function(event) {
		event.preventDefault();
		$(this).parents('li.media').find('.advanced').toggle();
	});

	$('.post-popover, .help-popover').popover({
		html : true,
		trigger : "focus",
		container : 'body',
		placement : function() {
			return ($(window).width() >= 990) ? 'right' : 'auto';
		},
		title : function() {
			$(this).next().html();
		},
		content : function() {
			return $(this).next().next().html();
		},
		delay : 0
	});

	$('.system-tags-link').popover({
		animation : false,
		html : true,
		trigger : 'manual',
		placement : 'right',
		delay : 0,
		content : function() {
			return $(this).next().find('.popover-content').html();
		}

	});

	$('.system-tags-link').click(function(event) {
		event.preventDefault();
		$(this).popover('show');
	});

	/**
	 * publication details abstract and description more link
	 */
	var defaultMaxChars = 350;
    	dots = "&hellip;";
	moretext = "";
	lesstext = "";

	$('.show-more').each(function() {
		var maxChar = defaultMaxChars;
		var moreLink = $(document.createElement("a"));
		var contentContainer = $(this).children(".contentContainer")[0];

		if (contentContainer) {
			if ($(this).attr('data-max-chars')) {
				maxChar = parseInt($(this).data('max-chars'));
			}
			moreLink.data("text", contentContainer.innerHTML)
					.html("(" + getString("more") + ")")
					.addClass("moreLink").addClass($(this).data('extra-class'))
					.click(function(event) {
						event.preventDefault();
						var contentContainer = $(this.parentNode).children(".contentContainer")[0];

						if ($(this).hasClass('show-less')) {
							$(this).html("(" + getString("more") + ")")
									.removeClass("show-less")
									.addClass("show-more");
							
						} else {
							$(this).html("(" + getString("less") + ")")
									.removeClass("show-more")
									.addClass("show-less");
						}
						shortenContent(contentContainer, moreLink.data("text"), maxChar);
						return false;
					});

			this.appendChild(moreLink[0]);
			if (!shortenContent(contentContainer, moreLink.data("text"), maxChar)) {
				moreLink.hide();
			}
		} //if (contentContainer)
	});
	var defaultMaxItemsInMoreList = 4;
	var defaultMoreIcon = 'fa-caret-right';
	var defaultLessIcon = 'fa-caret-up';
	$('.more-list').each(function() {
		var maxItemsInMoreList = defaultMaxItemsInMoreList;
		var subItems = $(this).find("> li");
		if (subItems.length > maxItemsInMoreList) {
			var moreLessLink = $('<li class="more-link-item"></li>');
			subItems.slice(maxItemsInMoreList).hide();
			var link = $('<a class="more"><span class="fa fa-caret-right"></span><span class="desc">' + getString("more") + '...</span></a>');
			link.click(function() {
				var icon = $(this).find('.fa');
				var descText;
				var classToRemove;
				var classToAdd;
				if (icon.hasClass(defaultMoreIcon)) {
					classToAdd = defaultLessIcon;
					classToRemove = defaultMoreIcon;
					descText = getString("less");
					$(this).parent().parent().find('li').show();
				} else {
					classToAdd = defaultMoreIcon;
					classToRemove = defaultLessIcon;
					descText = getString("more");
					$(this).parent().parent().find('li:not(.more-link-item)').slice(maxItemsInMoreList).hide();
				}
				icon.removeClass(classToRemove).addClass(classToAdd);
				$(this).find('.desc').text(descText + "...");
			});
			moreLessLink.append(link);
			$(this).append(moreLessLink);
		}
	});

	/**
	 * SYSTEM TAGS HANDLING
	 */
	// FIXME: this duplicates code from the context definition
	// maybe the info should be returned by the controller, or extra info in the
	// view
	var isSystemTag = function(item) {
		var systemTags = [
			//TODO: check whether this list is complete.
			'sys:relevantfor:.+',
			'relevantfor:.+',
			'sent:.+',
			'myown',
			'unfiled',
			'jabref',
			'sys:hidden:.+',
			'hidden:.+',
			'sys:external:.+',
			'external',
			'sys:reported:.+',
			'reported:.+'
		];

		for(var i = 0; i < systemTags.length; ++i) {
			pattern = new RegExp(systemTags[i]);
			if(!pattern.test(item)) {
				continue;
			}
			return true;
		}
		
	};

	$('.edit-tagsinput').tagsinput({
		confirmKeys : [ 32, 13 ], // space and return
		trimValue : true,
		freeInput : true,
		tagClass : function(item) {
			return isSystemTag(item) ? 'label label-warning' : 'label label-primary';
		},
		delimiter : ' '
	});

	$('.edit-tags-form').submit(function(e) {
		e.preventDefault();
		var submitButton = $(this).find('button[type=submit]');
		var url = $(this).attr('action');
		var data = $(this).serialize();
		var resourceHash = $(this).data('resource-hash');
		var tagField = $(this).find('input.edit-tagsinput');
		var tags = $(tagField).tagsinput('items');
		var responseMsg = $(this).find('.response-msg');
		
		$(responseMsg).empty(); //clear previous response message
		$(submitButton).attr("disabled", "disabled"); //disable submit button

		$.ajax({
			url : url,
			type : 'POST',
			data : data
		}).done(function(result) {
			// remove old tags and old system tags
			$('#list-item-' + resourceHash + ' .ptags span.label').remove();
			$('#list-item-' + resourceHash + ' .hiddenSystemTag ul.tags li').remove();
			
			// append current tags
			$(tags).each(function(i, tag) {
				if (!isSystemTag(tag)) {
					var tagView = viewForTag(tag, "grey");
					var tagContainer = $('#list-item-' + resourceHash + ' .ptags');
					tagContainer.append(tagView);
				} else {
					var tagView = viewForTag(tag, "warning");
					var tagListItem = $('<li></li>');
					tagListItem.append(tagView);
					$('#list-item-' + resourceHash + ' .hiddenSystemTag ul.tags').append(tagListItem);
				}
			});
			
			// if there are no systags, hide systag button
			var systags = $('#list-item-' + resourceHash + ' .hiddenSystemTag ul.tags li');
			if ($(systags).size() <= 0) {
				$('#system-tags-link-' + resourceHash).hide();
			} else {
				$('#system-tags-link-' + resourceHash).show();
			}
			
			// success message
			$(responseMsg).append('<div class="alert alert-success" role="alert">' + getString('edittags.update.success') + '</div>');
			$(submitButton).removeAttr("disabled");
		}).fail(function(result) {
			// fail message
			$(responseMsg).append('<div class="alert alert-danger" role="alert">' + getString('edittags.update.error') + '</div>');
			$(submitButton).removeAttr("disabled");
		});
		
		return false;
	});

	$('.rename-tags-btn').click(function() {
		$(this).parent().prev().focus().next().show().hide();
	});

	var sidebarAdjustments = function sidebarAdjusts() {
		if ($('#sidebar').prev().hasClass('content')) {

			var contentContainer = $('#sidebar').prev();
			var contentHeight = contentContainer.height();
			var sidebarHeight = $('#sidebar').height();

			if (contentHeight > sidebarHeight && $('#sidebar').is(':visible')) {
				$('#sidebar').css('height', contentHeight + 20);
			}
		}
	};

	sidebarAdjustments();

	$(window).resize(sidebarAdjustments);

	function shortenContent(el, text, maxChar) {
		var shortened = false;
		if (el.innerHTML.length > maxChar + dots.length) {
			text = text.substr(0, maxChar) + dots;
			shortened = true;
    
		}
		$(el).html(text);
		return shortened;
	}

	$('.community-page-user-list li a.show-less').click(function(event) {
		event.preventDefault();
		$(this).parent().parent().find('.shown').each(function() {
			$(this).removeClass('shown').addClass('hidden');
		});
	});

	$('.community-page-user-list li a.show-more').click(function(event) {
		event.preventDefault();
		$(this).parent().parent().find('.hidden').each(function() {
			$(this).removeClass('hidden').addClass('shown');
		});
	});

	/** MOBILE FUNCTIONS * */

	$('#hide-bookmarks').click(
			function(event) {
				event.preventDefault();
				$('.bookmarksContainer').is(':visible') ? $(this).text(
						getString("list.hide")) : $(this).text(
						getString("list.show"));
				$('.bookmarksContainer').slideToggle();

			});

	$('#hide-publications').click(
			function(event) {
				event.preventDefault();
				$('.publicationsContainer').is(':visible') ? $(this).text(
						getString("list.hide")) : $(this).text(
						getString("list.show"));
				$('.publicationsContainer').slideToggle();
			});
	
	$('#loginModal').on('shown.bs.modal', function (e) {
		$('#loginModal').find('input:first').focus();
	})
});

function viewForTag(tag, label) {
	var item = $('<span class="label label-tag"></span>');
	item.addClass('label-' + label);
	var link = $('<a></a>');
	link.attr('href', '/user/' + encodeURIComponent(currUser) + '/' + encodeURIComponent(tag));
	link.text(tag);
	item.append(link);
	return item;
}

function dummyDownHandler(e) {
	var event = e || window.event;

	var character = String
			.fromCharCode((96 <= e.keyCode && e.keyCode <= 105) ? e.keyCode - 48
					: e.keyCode);
	if (!(character.toLowerCase() == 'c' && event.ctrlKey)) {
		e.preventDefault();
		e.stopPropagation();
		return false;
	}
}

function dummyHandler(e) {
	e.preventDefault();
	e.stopPropagation();
	return false;
}

function dummyUpHandler(e) {
	e.preventDefault();
	e.stopPropagation();
	return false;
}

function activateAffixEntry(el) {
	$(el).addClass("active").siblings().each(function(h, g) {
		$(g).removeClass("active");
	});
}

function findBootstrapEnvironment() {
	var envs = [ 'xs', 'sm', 'md', 'lg' ];

	$el = $('<div>');
	$el.appendTo($('body'));

	for (var i = envs.length - 1; i >= 0; i--) {
		var env = envs[i];

		$el.addClass('hidden-' + env);
		if ($el.is(':hidden')) {
			$el.remove();
			return env;
		}
	}
}