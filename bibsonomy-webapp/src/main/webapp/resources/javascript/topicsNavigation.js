/*****************************************************************************
 * java script functions needed for navigation at cluster based views        *
 *****************************************************************************/

//----------------------------------------------------------
// resources view management 
//----------------------------------------------------------
/**
 * called when new resources arrived
 */
function loadContentCallback(response, status, xhr) {
}

/**
 * reload resources
 */
function reloadContent() {
	// display the waiting circle
	startWaiting('#wait_reloadContent');
	
	// determine in which ordering posts should be retrieved
	var ordering = currentResourceOrdering;

	// build query parameters
	var queryParams = "";
	for( i=0; i<numberOfClusters; i++ ) {
		queryParams += "clusters["+i+"].clusterID="+clusterSettings.clusters[i].clusterID;
		queryParams += "&clusters["+i+"].weight="+(clusterSettings.clusters[i].weight/100);
		queryParams += "&";
	};
	
	queryParams += "ordering="+ordering;

	// query server for resources
	$.getJSON(communityBaseUrl+'/queryTopics?'+queryParams+'&format=json', function(data) {
		clusterResources = data;
		updateContent();

		// hide the waiting circle
		stopWaiting('#wait_reloadContent');
		
		// enable sliders again
		for( i=0; i<numberOfClusters; i++ ) {
			var sliderName = "#slider"+i;
			$(document).ready(function() {
				$(sliderName).slider( "option", "disabled", false );
			});
		}
	});
};

/**
 * re-rank given cluster resources according to user settings
 */
function updateContent() {
	currentResourceRanking(clusterResources);
	
	var bt = renderJson(bibTexEntries.items, 'bibTexList');
	var bm = renderJson(bookmarkEntries.items, 'bookmarkList');
	
	//var encodedText = encodeURIComponent(bt);
	//content.document.location.replace("data:text/html,"+encodedText);

	document.getElementById("bmEntries").innerHTML = bm;
	document.getElementById("btEntries").innerHTML = bt;
}

/**
 * loads and renders given cluster's resources via ajax if 
 * necessary 
 */
function loadClusterResources(elementId, clusterPos) {
	if( clusterResources == undefined ) {
		alert("Clusters not loaded");
		return;
	} else if( clusterResources.clusters[clusterPos].bibtex == undefined ||
			clusterResources.clusters[clusterPos].bibtex.length == 0  ||
			clusterResources.clusters[clusterPos].bookmarks == undefined ||
			clusterResources.clusters[clusterPos].bookmarks.length == 0 ) {

		// query server for resources
		queryParams = "";
		queryParams += "clusters[0].clusterID="+clusterResources.clusters[clusterPos].clusterID;
		queryParams += "&clusters[0].weight="+(clusterResources.clusters[clusterPos].weight/100);
		queryParams += "&limit=6&offset=0";
		$.getJSON(communityBaseUrl+'/queryTopics?'+queryParams+'&format=json', function(data) {
			clusterResources.clusters[clusterPos].bibtex    = data.clusters[0].bibtex;
			clusterResources.clusters[clusterPos].bookmarks = data.clusters[0].bookmarks;

			// turn of progress indicator
			stopWaiting('wait_'+elementId);

			// render retrieved resources
			var cl = renderJson(clusterResources.clusters[clusterPos], 'resourcesPreview');
			document.getElementById(elementId).innerHTML = cl;
		});
	} 
}

/**
 * loads and renders given cluster's resources via ajax if 
 * necessary 
 */
function loadClusterResources(elementId, clusterPos, weight) {
	if( clusterResources == undefined ) {
		alert("Clusters not loaded");
		return;
	} else if( clusterResources.clusters[clusterPos].bibtex == undefined ||
			clusterResources.clusters[clusterPos].bibtex.length == 0  ||
			clusterResources.clusters[clusterPos].bookmarks == undefined ||
			clusterResources.clusters[clusterPos].bookmarks.length == 0 ) {

		// query server for resources
		queryParams = "";
		queryParams += "clusters[0].clusterID="+clusterResources.clusters[clusterPos].clusterID;
		queryParams += "&clusters[0].weight="+(weight/100);
		queryParams += "&limit=6&offset=0";
		$.getJSON(communityBaseUrl+'/queryTopics?'+queryParams+'&format=json', function(data) {
			clusterResources.clusters[clusterPos].bibtex    = data.clusters[0].bibtex;
			clusterResources.clusters[clusterPos].bookmarks = data.clusters[0].bookmarks;

			// turn of progress indicator
			stopWaiting('wait_'+elementId);

			// render retrieved resources
			var cl = renderJson(clusterResources.clusters[clusterPos], 'resourcesPreview');
			document.getElementById(elementId).innerHTML = cl;
		});
	} 
}

//----------------------------------------------------------
// resource rankings
//----------------------------------------------------------
/** all supported rankings */
var resourceRankings = {
		"weight" : rankPostsByWeight,
		"random" : rankPostsByRandom,
		"date"   : rankPostsByDate
};

var resourceOrderings = {
		"weight" : "POPULAR",
		"random" : "RANDOM",
		"date"   : "ADDED"
};

/** determines which ranking should be applied */
var currentResourceRanking  = rankPostsByWeight;
var currentResourceOrdering = resourceOrderings["weight"];

/**
 * sets the ranking to use
 */
function setRanking(rankingId) {
	if( rankingId in resourceRankings ) {
		currentResourceRanking  = resourceRankings[rankingId];
		currentResourceOrdering = resourceOrderings[rankingId];
	}
	reloadContent();
}

var myMaxClusterCount = 50;

/**
 * display the most important resources
 */
function rankPostsByWeight(clusterResources) {
	
	// helper for sorting resources
	function weightComparator(obj1, obj2) {
		return obj1.weight < obj2.weight ? 1 : (obj1.weight > obj2.weight ? -1 : 0)
	};

	// helper for merging lists of resources 
	function addPosts(arr1, arr2, clusterPos, weight) {
		for( i=0; i<arr2.length; i++ ) {
			normalizePost(arr2[i], clusterPos);
			arr2[i].weight = arr2[i].weight * weight;
			arr1.push(arr2[i]);
		}
	}

	// init data structures
	clusters = clusterResources.clusters;
	bibTexEntries.items   = new Array();
	bookmarkEntries.items = new Array();

	// read in weights from the sliders
	updateWeights();
	normalizeWeights(clusterResources);

	/*
	// sum up all weights for calculating sample size proportions
	weightSum = 0;
	for( i=0; i<numberOfClusters; i++ ) {
		weightSum += clusterSettings.clusters[i].weight;
	}

	for( j=0; j<clusters.length; j++ ) {
		cluster = clusters[j];

		// determine number of posts to display
		clusterPostCount     = Math.round(numberOfPosts * (clusterSettings.clusters[j].weight/weightSum));
		clusterBibTexCount   = Math.min(clusterPostCount, cluster.bibtex.length);
		clusterBookmarkCount = Math.min(clusterPostCount, cluster.bookmarks.length);

		// sort resources by weight and copy the most important posts
		// bibtex
		cluster.bibtex.sort(weightComparator);
		addPosts(bibTexEntries.items, cluster.bibtex.slice(0, clusterBibTexCount), j.toString(), clusterSettings.clusters[j].weight);
		// bookmark
		cluster.bookmarks.sort(weightComparator);
		addPosts(bookmarkEntries.items, cluster.bookmarks.slice(0, clusterBookmarkCount), j.toString(), clusterSettings.clusters[j].weight);
	}
	*/
	
	
	for( j=0; j<clusters.length; j++ ) {
		cluster = clusters[j];
		
		myWeight = cluster.weight;
		myBibtexCount = cluster.bibtex.length;
		myBookmarkCount = cluster.bookmarks.length;
		
		if (myBibtexCount > myMaxClusterCount) {
			addPosts(bibTexEntries.items, cluster.bibtex.slice(0, myMaxClusterCount), j.toString(), myWeight);
		} else {
			addPosts(bibTexEntries.items, cluster.bibtex.slice(0, myBibtexCount), j.toString(), myWeight);
		}
		
		if (myBookmarkCount > myMaxClusterCount) {
			addPosts(bookmarkEntries.items, cluster.bookmarks.slice(0, myMaxClusterCount), j.toString(), myWeight);
		} else {
			addPosts(bookmarkEntries.items, cluster.bookmarks.slice(0, myBookmarkCount), j.toString(), myWeight);
		}
	}

	// finally sort posts across clusters
	bibTexEntries.items.sort(weightComparator);
	bookmarkEntries.items.sort(weightComparator);
}

/**
 * get a random sample of the posts
 */
function rankPostsByRandom(clusterResources) {
	
	// helper for merging lists of resources 
	function addPosts(arr1, arr2, clusterPos, weight) {
		for( i=0; i<arr2.length; i++ ) {
			normalizePost(arr2[i], clusterPos);
			arr2[i].weight = arr2[i].weight * weight;
			arr1.push(arr2[i]);
		}
	}
	
	// init data structures
	clusters = clusterResources.clusters;
	bibTexEntries.items   = new Array();
	bookmarkEntries.items = new Array();

	// read in weights from the sliders
	updateWeights();

	/*
	// sum up all weights for calculating sample size proportions
	weightSum = 0;
	for( i=0; i<numberOfClusters; i++ ) {
		weightSum += clusterSettings.clusters[i].weight;
	}

	// draw a random sample from each cluster, sized
	// proportional to the user given weights
	for( i=0; i<clusters.length; i++ ) {
		cluster = clusters[i];
		// create random index sequence
		cluster.rndBibTexSeq   = new Array();
		cluster.rndBookmarkSeq = new Array();

		// determine number of posts to sample
		clusterPostCount     = Math.round(numberOfPosts * (clusterSettings.clusters[i].weight/weightSum));
		clusterBibTexCount   = Math.min(clusterPostCount, cluster.bibtex.length);
		clusterBookmarkCount = Math.min(clusterPostCount, cluster.bookmarks.length);

		// create BibTex sample
		var bibtexSampleCache = new Array();
		for( j=0; j<clusterBibTexCount; j++ ) {
			var next;
			while( ( !(next = Math.floor(Math.random()*cluster.bibtex.length)) in bibtexSampleCache) ){};

			bibtexSampleCache[next] = 1;

			post = cluster.bibtex[next];
			post.clusterPos = cluster.clusterPos.toString();
			if( post.journal == undefined ) {
				post.journal = "";
			}
			bibTexEntries.items.push(post);
		}

		// create Bookmark sample
		var bookmarkSampleCache = new Array();
		for( j=0; j<clusterBookmarkCount; j++ ) {
			var next;
			while( ( !(next = Math.floor(Math.random()*cluster.bookmarks.length)) in bookmarkSampleCache) ){};

			bookmarkSampleCache[next] = 1;

			post = cluster.bookmarks[next];
			post.clusterPos = cluster.clusterPos.toString();
			bookmarkEntries.items.push(post);
		}
	}
	*/

	for( j=0; j<clusters.length; j++ ) {
		cluster = clusters[j];
		
		myWeight = cluster.weight;
		myBibtexCount = cluster.bibtex.length;
		myBookmarkCount = cluster.bookmarks.length;
		
		if (myBibtexCount > myMaxClusterCount) {
			addPosts(bibTexEntries.items, cluster.bibtex.slice(0, myMaxClusterCount), j.toString(), myWeight);
		} else {
			addPosts(bibTexEntries.items, cluster.bibtex.slice(0, myBibtexCount), j.toString(), myWeight);
		}
		
		if (myBookmarkCount > myMaxClusterCount) {
			addPosts(bookmarkEntries.items, cluster.bookmarks.slice(0, myMaxClusterCount), j.toString(), myWeight);
		} else {
			addPosts(bookmarkEntries.items, cluster.bookmarks.slice(0, myBookmarkCount), j.toString(), myWeight);
		}
	}
	
	// finally shuffle all posts
	shuffleArray(bibTexEntries.items);
	shuffleArray(bookmarkEntries.items);
}
/**
 * display newest posts first
 */
function rankPostsByDate(clusterResources) {
	// helper for sorting resources
	function dateComparator(obj1, obj2) {
		var retVal = compareDates(obj2.date,dateFormat,obj1.date,dateFormat);
		return retVal;
	};

	// helper for merging lists of resources 
	function addPosts(arr1, arr2, clusterPos, weight) {
		for( i=0; i<arr2.length; i++ ) {
			normalizePost(arr2[i], clusterPos);
			arr2[i].weight = arr2[i].weight * weight;
			arr1.push(arr2[i]);
		}
	}

	// init data structures
	clusters = clusterResources.clusters;
	bibTexEntries.items   = new Array();
	bookmarkEntries.items = new Array();

	// read in weights from the sliders
	updateWeights();

	/*
	// sum up all weights for calculating sample size proportions
	weightSum = 0;
	for( i=0; i<numberOfClusters; i++ ) {
		weightSum += clusterSettings.clusters[i].weight;
	}

	for( j=0; j<clusters.length; j++ ) {
		cluster = clusters[j];

		// determine number of posts to display
		clusterPostCount     = Math.round(numberOfPosts * (clusterSettings.clusters[j].weight/weightSum));
		clusterBibTexCount   = Math.min(clusterPostCount, cluster.bibtex.length);
		clusterBookmarkCount = Math.min(clusterPostCount, cluster.bookmarks.length);

		// sort resources by weight and copy the most important posts
		// bibtex
		// cluster.bibtex.sort(dateComparator);
		addPosts(bibTexEntries.items, cluster.bibtex.slice(0, clusterBibTexCount), j.toString(), clusterSettings.clusters[j].weight);
		// bookmark
		// cluster.bookmarks.sort(dateComparator);
		addPosts(bookmarkEntries.items, cluster.bookmarks.slice(0, clusterBookmarkCount), j.toString(), clusterSettings.clusters[j].weight);
	}
	*/

	for( j=0; j<clusters.length; j++ ) {
		cluster = clusters[j];
		
		myWeight = cluster.weight;
		myBibtexCount = cluster.bibtex.length;
		myBookmarkCount = cluster.bookmarks.length;
		
		if (myBibtexCount > myMaxClusterCount) {
			addPosts(bibTexEntries.items, cluster.bibtex.slice(0, myMaxClusterCount), j.toString(), myWeight);
		} else {
			addPosts(bibTexEntries.items, cluster.bibtex.slice(0, myBibtexCount), j.toString(), myWeight);
		}
		
		if (myBookmarkCount > myMaxClusterCount) {
			addPosts(bookmarkEntries.items, cluster.bookmarks.slice(0, myMaxClusterCount), j.toString(), myWeight);
		} else {
			addPosts(bookmarkEntries.items, cluster.bookmarks.slice(0, myBookmarkCount), j.toString(), myWeight);
		}
	}
	
	// finally sort posts across clusters
	bibTexEntries.items.sort(dateComparator);
	bookmarkEntries.items.sort(dateComparator);
}

/**
 * ensure that all required fields are defined
 */
function normalizePost(post, clusterPos) {
	post.clusterPos = clusterPos;
	if( post.journal == undefined ) {
		post.journal = "";
	}
}

/**
 * normalizes cluster weights for better comparibility
 */
function normalizeWeights(clusterResources) {
	// init data structures
	clusters = clusterResources.clusters;

	for( var i=0; i<clusters.length; i++ ) {
		cluster = clusters[i];

		// determine max. weight
		var maxBibTexWeight   = 0;
		var maxBookmarkWeight = 0;
		for( var j=0; j<cluster.bibtex.length; j++ ) {
			if( clusters[i].bibtex[j].weight>maxBibTexWeight ) { 
				maxBibTexWeight = clusters[i].bibtex[j].weight
			}
		}
		for( var j=0; j<cluster.bookmarks.length; j++ ) {
			if( clusters[i].bookmarks[j].weight>maxBookmarkWeight ) { 
				maxBookmarkWeight = clusters[i].bookmarks[j].weight
			}
		}

		// normalize weight
		if( maxBibTexWeight>0 ) {
			for( var j=0; j<cluster.bibtex.length; j++ ) {
				clusters[i].bibtex[j].weight = clusters[i].bibtex[j].weight/maxBibTexWeight
			}
		};
		if( maxBookmarkWeight>0 ) {
			for( var j=0; j<cluster.bookmarks.length; j++ ) {
				clusters[i].bookmarks[j].weight = clusters[i].bookmarks[j].weight/maxBookmarkWeight
			}
		}
	}
}

//----------------------------------------------------------
// settings view management 
//----------------------------------------------------------
/**
 * called when new settings were received
 *  - initialize slider for each received cluster
 */
function initializeSettings() {
	// little hack for dealing with call by reference semantics 
	function createContext(idx) {
		id = idx;
		weight = clusterSettings.clusters[idx].weight;
		return {
			value: weight,
			change: function(event, ui) { changeClusterWeight(id, ui.value) }
		}
	}

	// initialize sliders for each cluster
	for( i=0; i<numberOfClusters; i++ ) {
		var sliderName = "#slider"+i;
		$(document).ready(function() {
			$(sliderName).slider(createContext(i));
		});
	}
}

/**
 * adopt current cluster settings according to user input
 */
function changeClusterWeight(i, value) {
	clusterSettings.clusters[i].weight = value;
	//updateContent();

	// temporary disable sliders
	for( i=0; i<numberOfClusters; i++ ) {
		var sliderName = "#slider"+i;
		$(document).ready(function() {
			$(sliderName).slider( "option", "disabled", true );
		});
	}
	
	// search kd-tree
	reloadContent();
}

/** 
 * read in values from cluster sliders and stores them globally
 */
function updateWeights() {
	for( i=0; i<numberOfClusters; i++ ) {
		sliderName = "#slider"+i;
		value = $(sliderName).slider( "option", "value" );
		clusterSettings.clusters[i].weight = value;
	}
}

/** 
 * fetch new cluster settings from server
 */
function reloadSettings() {
	reloadSettings(false);
}

/** 
 * fetch new cluster settings from server
 */
function reloadSettings(loadContentFlag) {
	startWaiting('#wait_reloadSettings');

	// first check whether topic search is available
	$.getJSON(communityBaseUrl+'/queryTopics?format=json', function(data) {
		clusterResources = data;
		if (clusterResources.clusters.length > 0 && clusterResources.clusters[0].clusterID == -1) {
			// show error page
			stopWaiting('#wait_reloadSettings');
			alert("topic search down");
		} else {
			// fetch json cluster settings
			$.getJSON(communityBaseUrl+'/topicsSettings?format=json', function(data) {
				clusterSettings = data;
				normalizeClusterSettings(clusterSettings.clusters);
	
				numberOfClusters = clusterSettings.clusters.length;
	
				// render cluster representation
				var cl = renderJson(data, 'topicsSettings');
				document.getElementById("topicsSettings").innerHTML = cl;
	
				// render sliders
				initializeSettings();
	
				stopWaiting('#wait_reloadSettings');
	
				// reload resources if needed
				if( loadContentFlag ) {
					reloadContent();
				}
	
				if( clusterSettings.clusters.length < maxClusterCount ) {
					$('#addClustersNavigation').show();
				} else {
					$('#addClustersNavigation').hide();
				}
			});
		}
	});
}


/**
 * fills and calculates all needed attributes for cluster settings 
 */
function normalizeClusterSettings(clusters) {
	for( var i=0; i<clusters.length; i++ ) {
		clusters[i]['clusterPos'] = i;
	}
}

/**
 * fills and calculates all needed attributes for tag clouds
 */
function normalizeTags(clusters) {
	// calculate maxTagCounts
	for( var i=0; i<clusters.length; i++ ) {
		var maxTagCount = -1;
		for( var j=0; j<clusters[i].tags.length; j++ ) {
			if( maxTagCount < clusters[i].tags[j].globalcount ) {
				maxTagCount = clusters[i].tags[j].globalcount;
			}
		}
		for( var j=0; j<clusters[i].tags.length; j++ ) {
			clusters[i].tags[j]['maxTagCount'] = maxTagCount;
		}
	}
}

/**
 * fills and calculates all needed attributes for tag clouds
 */
function normalizeUsers(clusters) {
	// calculate maxTagCounts
	for( var i=0; i<clusters.length; i++ ) {
		var maxUserWeight = -1;
		for( var j=0; j<clusters[i].users.length; j++ ) {
			if( maxUserWeight < clusters[i].users[j].weight ) {
				maxUserWeight = clusters[i].users[j].weight;
			}
		}
		for( var j=0; j<clusters[i].users.length; j++ ) {
			clusters[i].users[j]['maxUserWeight'] = maxUserWeight;
		}
	}
}

//----------------------------------------------------------
// cluster view navigation
//----------------------------------------------------------
/**
 * resets viex index for displaying all clusters from the beginning
 */
function resetClusterView() {
	if( clusterResources != undefined && 'listView' in clusterResources ) {
		delete clusterResources.listView;
	}
}

/**
 * show cluster overview 
 */
function showClusterPage() {
	if( clusterResources != undefined && clusterResources.listView != undefined ) {
		limit =  clusterResources.listView.limit;
		offset = clusterResources.listView.offset;
		showClusters(offset, limit);
	} else {
		showClusters(0, 6);
	}
}

/**
 * show cluster overview 
 */
function showClusters(offset, limit) {
	startWaiting('#wait_showClusterPage'); 
	$.getJSON(communityBaseUrl+'/topicsList?limit='+limit+'&offset='+offset+'&format=json', function(data) {
		clusterResources = data;

		normalizeTags(clusterResources.clusters);
		normalizeUsers(clusterResources.clusters);

		var cl = renderJson(data, 'topicsOverview');
		document.getElementById("bmEntries").innerHTML = cl;
		document.getElementById("btEntries").innerHTML = "";

		stopWaiting('#wait_showClusterPage');
	});
}

/**
 * show next cluster page
 */
function showNextClusters() {
	var limit  = clusterResources.listView.limit; 
	var offset = 1.0*clusterResources.listView.offset + 1.0*limit;

	showClusters(offset, limit);					
}

/**
 * show previous cluster page
 */
function showPreviousClusters() {
	var limit  = clusterResources.listView.limit; 
	var offset = clusterResources.listView.offset - 1.0*limit;

	showClusters(offset, limit);					
}

/**
 * resource view for cluster tooltips
 */
function showClusterTooltip(cluster) {
}
//----------------------------------------------------------
// cluster settings
//----------------------------------------------------------
/**
 * add given cluster to logged in user's settings
				<a href="bibsonomy-community-servlet/clusterSettings?action=ADDCLUSTERS&clusters[0]={clusterID|html-attr-value}">[add cluster]</a>
 */
function addCluster(clusterId, weight) {
	$.getJSON('/bibsonomy-community-servlet/topicsSettings?action=ADDCLUSTERS&clusters[0].clusterID='+clusterId+'&clusters[0].weight='+weight+'&format=json', function(data) {
		clusterSettings = data;
		reloadSettings(true);
		showClusterPage();
	});
}

/**
 * adds the most appropriate community to the given users settings (if not included already)
 */
function addRecommendedCluster() {
	var res = confirm('This will replace the currently selected clusters. Do you want to proceed?');
	if (res) {
		$.getJSON('/bibsonomy-community-servlet/topicsSettings?action=ADDRECOMMENDEDCLUSTER&format=json', function(data) {
			clusterSettings = data;
			reloadSettings(true);
		});
	}
}

/**
 * remove given cluster from logged in user's settings
 */
function removeCluster(clusterId) {
	$.getJSON('/bibsonomy-community-servlet/topicsSettings?action=REMOVECLUSTERS&clusters[0].clusterID='+clusterId+'&format=json', function(data) {
		clusterSettings = data;
		reloadSettings();
		showClusterPage();
	});
}

/**
 * change clustering
 */
function reloadClustering() {
	$.getJSON('/bibsonomy-community-servlet/topicsSettings?action=CHANGEALGORITHM&format=json', function(data) {
		clusterSettings = data;
		reloadSettings();
		showClusterPage();
	});
}

/**
 * add given cluster to logged in user's settings
				<a href="bibsonomy-community-servlet/clusterSettings?action=ADDCLUSTERS&clusters[0]={clusterID|html-attr-value}">[add cluster]</a>
 */
function saveClusterSettings() {
	// build query parameters
	var queryParams = "";
	for( i=0; i<numberOfClusters; i++ ) {
		queryParams += "clusters["+i+"].clusterID="+clusterSettings.clusters[i].clusterID;
		queryParams += "&clusters["+i+"].weight="+clusterSettings.clusters[i].weight;
		if( i<numberOfClusters-1 ) {
			queryParams += "&";
		}
	}
	
	// query server for resources
	$.getJSON(communityBaseUrl+'/topicsSettings?action=SAVECLUSTERSETTINGS&'+queryParams+'&format=json', function(data) {
	});
}

//----------------------------------------------------------
// common helper functions
//----------------------------------------------------------
function shuffleArray ( myArray ) {
	i = myArray.length;
	if ( i == 0 ) return false;
	while ( --i ) {
		var j = Math.floor( Math.random() * ( i + 1 ) );
		var tempi = myArray[i];
		var tempj = myArray[j];
		myArray[i] = tempj;
		myArray[j] = tempi;
	}
}

/**
 * shows given waiting elements
 */
function startWaiting(elementId) {
	$(elementId).show()
}

/**
 * hides given waiting elements
 */
function stopWaiting(elementId) {
	$(elementId).hide()
}

/**
 * initializes
 */
function setUp() {
	reloadSettings(true);
}

// 'main'
$(document).ready(function() {
	setUp();
});
