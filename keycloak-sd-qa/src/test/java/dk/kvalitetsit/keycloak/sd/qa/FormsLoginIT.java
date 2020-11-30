package dk.kvalitetsit.keycloak.sd.qa;

import java.util.UUID;

import org.junit.Test;
import org.openqa.selenium.remote.RemoteWebDriver;

public class FormsLoginIT extends AbstractIT {

	
	@Test
	public void testSuccessfulLoginWithExistingUser() {
		
		// Given
		String uniqueUserName = "user"+UUID.randomUUID().toString();
		String password = "qwerty:-)";
		createNewUserInFormsRealm(uniqueUserName, password);

		RemoteWebDriver webdriver = getWebDriver();

		// When
		String result = doFormLoginFlow(webdriver, getNginxUrl("auth/realms/broker/protocol/openid-connect/auth?client_id=account&redirect_uri=http://nginx:8787/auth/realms/broker/account/login-redirect&state=0/d27e373c-9402-428c-ba24-52476feba201&response_type=code&scope=openid"), uniqueUserName, password);

		System.out.println(result);
		
	}

	
	public String doFormLoginFlow(RemoteWebDriver webdriver, String url, String username, String password) {
		webdriver.get(url);
		
		String source = webdriver.getPageSource();
		System.out.println(source);
		
		webdriver.findElementByName("username").sendKeys(username);
		webdriver.findElementByName("password").sendKeys(password);
		webdriver.findElementByName("login").click();;

		String source2 = webdriver.getPageSource();

		return source2;
	}
}
