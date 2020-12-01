package dk.kvalitetsit.keycloak.sd.qa;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.Logs;
import org.openqa.selenium.remote.RemoteWebDriver;

public class FormsLoginIT extends AbstractIT {

	
	@Test
	public void testSuccessfulLoginWithExistingUser() {
		
		// Given
		String uniqueUserName = "user";//+UUID.randomUUID().toString();
		String password = "Test1234";
		createNewUserInFormsRealm(uniqueUserName, password);

		RemoteWebDriver webdriver = getWebDriver();

		// When
		String result = doFormLoginFlow(webdriver, getNginxUrl("auth/realms/broker/protocol/openid-connect/auth?client_id=account&redirect_uri=http://nginx:8787/auth/realms/broker/account/login-redirect&state=0/d27e373c-9402-428c-ba24-52476feba201&response_type=code&scope=openid"), uniqueUserName, password);

		LogEntries logEntries = webdriver.manage().logs().get(LogType.BROWSER);
        for (LogEntry entry : logEntries) {
            System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
            //do something useful with the data
        }
		Logs logs = webdriver.manage().logs();
		
		System.out.println(result);
		
	}

	
	public String doFormLoginFlow(RemoteWebDriver webdriver, String url, String username, String password) {
		webdriver.get(url);
		
		String source = webdriver.getPageSource();
		System.out.println(source);
		
		webdriver.findElementByName("username").sendKeys(username);
		webdriver.findElementByName("password").sendKeys(password);
		webdriver.findElementById("kc-login").click();

		String source2 = webdriver.getPageSource();

		return source2;
	}
}
