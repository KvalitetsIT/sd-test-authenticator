package dk.kvalitetsit.keycloak.sd.qa;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.remote.RemoteWebDriver;

public class FormsLoginIT extends AbstractIT {


	@Test
	public void testSuccessfulLoginWithExistingUser() throws IOException {

		// Given
		String uniqueUserName = "test";
		String password = "Test1234";
		createNewUserInFormsRealm(uniqueUserName, password);

		RemoteWebDriver webdriver = getWebDriver();

		// When
		doFormLoginFlow(webdriver, getNginxUrl("auth/realms/broker/protocol/openid-connect/auth?client_id=account&redirect_uri=http://nginx:8787/auth/realms/broker/account/login-redirect&state=0/d27e373c-9402-428c-ba24-52476feba201&response_type=code&scope=openid"), uniqueUserName, password);


		// Then
		Assert.assertEquals("Keycloak Account Management", webdriver.getTitle());
	}

	public void doFormLoginFlow(RemoteWebDriver webdriver, String url, String username, String password) {
		webdriver.get(url);

		checkPageIsReady(webdriver);

		chooseFormsLoginMethod(webdriver);
		
		switchToLoginFrame(webdriver);
		
		webdriver.findElementByName("username").sendKeys(username);
		webdriver.findElementByName("password").sendKeys(password);
		webdriver.findElementById("kc-login").click();
		
		checkPageIsReady(webdriver);

	}
}
