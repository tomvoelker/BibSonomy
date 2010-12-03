package tags;


/**
 * Some taglib functions
 * 
 * @author folke
 */
public class AuthTagLibFunctions  {
	
	/**
	 * check wheter given authentication method is configured
	 * @param authConfig authentication configuration
	 * @param methodName authentication method name
	 * @return
	 */
	public static Boolean containsAuthMethod(org.bibsonomy.webapp.config.AuthConfig authConfig, String methodName) {
		return authConfig.containsAuthMethod(methodName);
	}
}
