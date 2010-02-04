/*****************************************************************************
 * 
 * @author mve
 * 
 * */

/**
 * @param method
 * 			the name of the method to call ('recommendTags' || 'findSimilarUrls')
 * @param url
 * 			the url to check
 */

var PIAObject = null;

function PIARequest(method, url) {
	this.callback = null;
	this.container = null;
	this.count = 10;
	this.serviceURL = 'http://www.biblicious.org/piaWebservice/';
	this.url = url;
	this.method = method;
	this.makeRequest = function() {
			var soapBody = new SOAPObject(method);
			soapBody.appendChild(new SOAPObject("url").val(this.url));
			soapBody.appendChild(new SOAPObject("count").val(this.count));

			var sr = new SOAPRequest(this.method, soapBody); //Request is now ready to be sent to a web-service
			SOAPClient.Proxy = this.serviceURL+this.method+'?url=' + this.url + '&count='+this.count; 
			SOAPClient.SendRequest(sr, this.callback); 
	}
	PIAObject = this;
}