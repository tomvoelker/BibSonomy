/**
 * implementation for the sample container
 */

/**
 * Public Shindig namespace with samplecontainer object
 */
var shindig = shindig || {};
shindig.samplecontainer = {};

/**
 * Hide our functions and variables from other javascript
 */
(function(){

  /**
   * Private Variables
  */
  var parentUrl = document.location.href;
  var baseUrl   = parentUrl.substring(0, parentUrl.indexOf('samplecontainer.html'));
  var useDebug  = true;

  // TODO: This is gross, it needs to use the config just like the gadget js does
  var socialDataPath = document.location.protocol + "//" + document.location.host
    + "/social/rest/samplecontainer/";

  var gadgetUrlMatches = /[?&]url=((?:[^#&]+|&amp;)+)/.exec(parentUrl);
  var gadgetUrl = (gadgetUrlMatches)
      ? gadgetUrlMatches[1]
      : baseUrl + 'examples/SocialHelloWorld.xml';

  var gadgetUrlCookie = 'sampleContainerGadgetUrl';


  var gadget;

  var viewerId = "john.doe";
  var ownerId = "canonical";

  var viewMatches = /[?&]view=((?:[^#&]+|&amp;)+)/.exec(parentUrl);
  var current_view = (viewMatches)
      ? viewMatches[1]
      : "default";

  /**
   * Public Variables
   */

  /**
   * Private Functions
   */

  function generateSecureToken() {
    // TODO: Use a less silly mechanism of mapping a gadget URL to an appid
    var appId = 0;
    for (var i = 0; i < gadgetUrl.length; i++) {
      appId += gadgetUrl.charCodeAt(i);
    }
    var fields = [ownerId, viewerId, appId, "shindig", gadgetUrl, "0", "default"];
    for (var i = 0; i < fields.length; i++) {
      // escape each field individually, for metachars in URL
      fields[i] = escape(fields[i]);
    }
    return fields.join(":");
  }

  SampleContainerGadget = function(opt_params) {
    shindig.BaseIfrGadget.call(this, opt_params);

    // mix-in IfrGadget functions
    for (var name in shindig.IfrGadget) if (shindig.IfrGadget.hasOwnProperty(name)) {
      SampleContainerGadget[name] = shindig.IfrGadget[name];
    }
  };

  SampleContainerGadget.inherits(shindig.BaseIfrGadget);

  SampleContainerGadget.prototype.getAdditionalParams = function() {
    var params = '';

    if (useDebug) {
      params += "&debug=1";
    }
    return params;
  };

  shindig.container.gadgetClass = SampleContainerGadget;


  function sendRequestToServer(url, method, opt_postParams, opt_callback, opt_excludeSecurityToken) {
    // TODO: Should re-use the jsoncontainer code somehow
    opt_postParams = opt_postParams || {};

    var makeRequestParams = {
      "CONTENT_TYPE" : "JSON",
      "METHOD" : method,
      "POST_DATA" : opt_postParams};

    if (!opt_excludeSecurityToken) {
      url = socialDataPath + url + "?st=" + gadget.secureToken;
    }

    gadgets.io.makeNonProxiedRequest(url,
      function(data) {
        data = data.data;
        if (opt_callback) {
            opt_callback(data);
        }
      },
      makeRequestParams,
      "application/javascript"
    );
  };

  function generateGadgets(metadata) {
    // TODO: The gadget.js file should really have a clearGadgets method
    shindig.container.view_ = current_view;
    shindig.container.gadgets_ = {};
    for (var i = 0; i < metadata.gadgets.length; i++) {
      gadget = shindig.container.createGadget({'specUrl': metadata.gadgets[i].url,
          'title': metadata.gadgets[i].title, 'userPrefs': metadata.gadgets[i].userPrefs});
      // Shindigs rpc code uses direct javascript calls when running on the same domain
      // to simulate cross-domain when running sample container we replace 
      // 'localhost' with '127.0.0.1' 
      var iframeBaseUrl = baseUrl.replace("localhost", "127.0.0.1") + '../../../gadgets/';

      gadget.setServerBase(iframeBaseUrl);
      gadget.secureToken = escape(generateSecureToken());
      shindig.container.addGadget(gadget);
    }

    shindig.container.layoutManager.setGadgetChromeIds(['gadget-chrome']);
    shindig.container.renderGadgets();
  };

  function refreshGadgets(metadata) {
    // TODO: The gadget.js file should really have a getGadgets method
    for (var gadget in shindig.container.gadgets_) {
      var gadgetMetadata = metadata.gadgets[0];
      shindig.container.gadgets_[gadget].title = gadgetMetadata.title;
      shindig.container.gadgets_[gadget].specUrl = gadgetMetadata.url;
      shindig.container.gadgets_[gadget].userPrefs = gadgetMetadata.userPrefs;
      shindig.container.gadgets_[gadget].secureToken = escape(generateSecureToken());
    }
    shindig.container.refreshGadgets();
  }

  function requestGadgetMetaData(opt_callback) {
    var request = {
      context: {
        country: "default",
        language: "default",
        view: current_view,
        container: "default"
      },
      gadgets: [{
        url: gadgetUrl,
        moduleId: 1
      }]
    };

    sendRequestToServer("/gadgets/metadata", "POST",
        gadgets.json.stringify(request), opt_callback, true);
  }

  /**
   * Public Functions
   */
  shindig.samplecontainer.initSampleContainer = function() {
     // Upon initial load, check for the cache query parameter (we don't want
     // to overwrite when clicking "refresh all")
     var cacheUrlMatches = /[?&]cache=([01])/.exec(parentUrl);
     gadgets.pubsubrouter.init(function() { return gadgetUrl; });
  };

  shindig.samplecontainer.initGadget = function() {
    // Fetch cookies
    var cookieGadgetUrl = decodeURIComponent(shindig.cookies.get(gadgetUrlCookie));
    if (cookieGadgetUrl && cookieGadgetUrl != "undefined") {
      gadgetUrl = cookieGadgetUrl;
    }


    // Render gadget
    document.getElementById("gadgetUrl").value = gadgetUrl;

    // Viewer and Owner
    document.getElementById("viewerId").value = viewerId;
    document.getElementById("ownerId").value = ownerId;

    requestGadgetMetaData(generateGadgets);
  };

  /**
   * reload new gadget
   */
  shindig.samplecontainer.changeGadgetUrl = function() {
    shindig.container.nocache_ = true;

    viewerId  = document.getElementById("viewerId").value;
    ownerId   = document.getElementById("ownerId").value;
    gadgetUrl = document.getElementById("gadgetUrl").value;

    shindig.cookies.set(gadgetUrlCookie, encodeURIComponent(gadgetUrl));

    requestGadgetMetaData(refreshGadgets);
  };

  osapi.requestShareApp = function(request, callback) {
    alert("osapi.requestShareApp called");
    callback({});
  };

  osapi.requestPermission = function(request, callback) {
    alert("osapi.requestPermission called");
    callback({});
  };

})();
