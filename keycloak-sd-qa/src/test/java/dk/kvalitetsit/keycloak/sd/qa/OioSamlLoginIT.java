package dk.kvalitetsit.keycloak.sd.qa;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.remote.RemoteWebDriver;

public class OioSamlLoginIT extends AbstractIT {

	private static final String ORGANISATION_A = "organisation-a";
	private static final String ORGANISATION_B = "organisation-b";

	@Test
	public void testSelectOioSamlLoginWithOrganisationA() {
		
		// Given
		RemoteWebDriver webdriver = getWebDriver();

		// When
		doSelectOioSamlFlow(webdriver, getNginxUrl("auth/realms/broker/protocol/openid-connect/auth?client_id=account&redirect_uri=http://nginx:8787/auth/realms/broker/account/login-redirect&state=0/d27e373c-9402-428c-ba24-52476feba201&response_type=code&scope=openid"), ORGANISATION_A);
		
		// Then
		Assert.assertEquals("Log in to oiosaml-organisation-a", webdriver.getTitle());
	}

	@Test
	public void testSelectOioSamlLoginWithOrganisationB() {
		
		// Given
		RemoteWebDriver webdriver = getWebDriver();

		// When
		doSelectOioSamlFlow(webdriver, getNginxUrl("auth/realms/broker/protocol/openid-connect/auth?client_id=account&redirect_uri=http://nginx:8787/auth/realms/broker/account/login-redirect&state=0/d27e373c-9402-428c-ba24-52476feba201&response_type=code&scope=openid"), ORGANISATION_B);
		
		// Then
		Assert.assertEquals("Log in to oiosaml-organisation-b", webdriver.getTitle());
	}

	public void doSelectOioSamlFlow(RemoteWebDriver webdriver, String url, String orgToSelect) {
		webdriver.get(url);

		checkPageIsReady(webdriver);

		webdriver.findElementById("a-oiosaml").click();

		checkPageIsReady(webdriver);
	}

}
