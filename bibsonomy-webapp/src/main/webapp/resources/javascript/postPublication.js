// methods for postPublication page

// setup jQuery to update recommender with form data
var tagRecoOptions = { 
   url:  '/ajax/getPublicationRecommendedTags', 
   success: function showResponse(responseText, statusText) { 
	 handleRecommendedTags(responseText);
   } 
}; 