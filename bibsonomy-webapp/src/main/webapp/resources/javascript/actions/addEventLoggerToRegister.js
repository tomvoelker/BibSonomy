var regLogger = new RegLogger();

try {
	var regloggerTmp = JSON.parse($("#registerUser\\.registrationLog").val());
	if (regloggerTmp) {
		for(var k in regloggerTmp) regLogger[k]=regloggerTmp[k];
	}
} catch (err) {
	// do nothing: on first load of page there is no parsable RegLogger object.
}
regLogger.checkErrors();
regLogger.addReferer();
regLogger.addUserAgent();

regLogger.setListener(window);

$("#command").submit(function(event) {
	$("#registerUser\\.registrationLog").val(JSON.stringify(regLogger))
	return true;
});