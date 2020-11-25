package dk.kvalitetsit.keycloak.sd.qa;

import java.util.UUID;

import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;

public class FormsLoginTest extends AbstractIntegrationTest {

	
	@Rule
	public BrowserWebDriverContainer<?> chrome = createChrome();

	
	@Test
	public void testSuccessfulLoginWithExistingUser() {
		
		// Given
		String uniqueUserName = "user"+UUID.randomUUID().toString();
		String password = "qwerty:-)";
		createNewUserInFormsRealm(uniqueUserName, password);

		RemoteWebDriver webdriver = chrome.getWebDriver();

		// When
		String result = doFormLoginFlow(webdriver, getNginxUrl("auth/realms/broker/protocol/openid-connect/auth?client_id=account&redirect_uri=http://localhost:8787/auth/realms/broker/account/login-redirect&state=0/d27e373c-9402-428c-ba24-52476feba201&response_type=code&scope=openid"), uniqueUserName, password);

		
		
	}

	
	public String doFormLoginFlow(RemoteWebDriver webdriver, String url, String username, String password) {
		webdriver.get(url);
		webdriver.findElementByName("username").sendKeys(username);
		webdriver.findElementByName("password").sendKeys(password);
		webdriver.findElementByName("login").click();;

		String source = webdriver.getPageSource();

		return source;
	}


}
