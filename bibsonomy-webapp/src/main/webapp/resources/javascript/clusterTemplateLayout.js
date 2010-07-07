/*****************************************************************************
 * java script functions needed for cluster templates                        *
 *****************************************************************************/
/**
 * convert array to parameter list 
 */
function htmlParameterList(list) {
	var retVal = "";
	for( var i=0; i<list.length; i++ ) {
		retVal += encodeURIComponent(list[i].toString());
		if( i<list.length-1 ) {
			retVal += "+";
		}
	};

	return retVal;
}

/**
 * check if given cluster is already enabled by the logged in user
 */
function isClusterEnabled(x) {
	if( clusterSettings == undefined ) {
		alert("No cluster settings loaded");
		return false;
	} else {
		for( var i=0; i<clusterSettings.clusters.length; i++ ) {
			if( clusterSettings.clusters[i].clusterID == x ) {
				return true;
			} 
		}
		return false;
	}
}

/**
 * returns the css Class for a given tag
 * @param tag the current Tag
 * @return the css class for the tag
 */
function getTagSize(tag) {
	var tagCount    = tag.globalcount;
	var maxTagCount = tag.maxTagCount;

	/*
	 * catch incorrect values
	 */
	if (tagCount == 0 || maxTagCount == 0) return "tagtiny";

	var percentage = ((tagCount * 100) / maxTagCount);

	if (percentage < 25) {
		return  "tagtiny";
	} else if (percentage >= 25 && percentage < 50) {
		return  "tagnormal";
	} else if (percentage >= 50 && percentage < 75) {
		return  "taglarge";
	} else if (percentage >= 75) {
		return  "taghuge";
	}

	return "";
}

/**
 * returns the css Class for a given user
 * @param user the current User
 * @return the css class for the user
 */
function getUserSize(user) {
	var userWeight    = user.weight;
	var maxUserWeight = user.maxUserWeight;

	/*
	 * catch incorrect values
	 */
	if (userWeight == 0 || maxUserWeight == 0) return "usertiny";

	var percentage = ((userWeight * 100) / maxUserWeight);

	if (percentage < 25) {
		return  "usertiny";
	} else if (percentage >= 25 && percentage < 50) {
		return  "usernormal";
	} else if (percentage >= 50 && percentage < 75) {
		return  "userlarge";
	} else if (percentage >= 75) {
		return  "userhuge";
	}

	return "";
}

/**
 * compute the resulting tag font size
 */
function getTagFontSize(tag) {
	var tagFrequency    = tag.globalcount;
	var tagMaxFrequency = tag.maxTagCount;
	/*
	 * we expect 0 < tagFrequency < tagMaxFrequency 
	 * and normalize f to 0 < f 100 as percentage of tagMaxFrequency
	 */
	f = 1.0*tagFrequency / tagMaxFrequency * 100;
	t = f > 100 ? 100 : f;
	t /= 15;
	t = Math.log(t) * 100;
	return (Math.round(t)<100) ? 100 : Math.round(t);		
}	

/**
 * print list of direct links to pages
 */	
function generateListAnchors(listView) {
	var listNavigation = "";
	var cnt=1;
	for( var i=0; i<listView.total; i+=1.0*listView.limit ) {
		listNavigation +="<a class='clusterNavigation";
		if( i>=listView.offset && i<listView.offset+listView.limit ) {
			listNavigation += " clusterNavigationActive";
		}
		listNavigation +="' onclick='showClusters("+i+","+listView.limit+");'>"+cnt+"</a>";
		if( i<listView.total ) {
			listNavigation +="&nbsp;|&nbsp;";
		}
		cnt++;
	}

	return listNavigation;
} 
