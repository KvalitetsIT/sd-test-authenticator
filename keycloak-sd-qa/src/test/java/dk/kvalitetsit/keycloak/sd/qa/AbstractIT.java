package dk.kvalitetsit.keycloak.sd.qa;

import java.io.IOException;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import javax.ws.rs.core.Response;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestConfiguration.class } ) 
public abstract class AbstractIT {

	public static final String FORM_LOGIN_CHOICE_ID = "a-form";
	public static final String OIOSAML_LOGIN_CHOICE_ID = "a-oiosaml";
	public static final String SD_LOGIN_IFRAME_ID = "sdloginiframe";

	
	private static final String KEYCLOAK_ADMIN_USER = "sdadmin";
	private static final String KEYCLOAK_ADMIN_PASSWD = "Test1234";


	private static final String FORM_REALM = "form";
	private static Logger logger = LoggerFactory.getLogger(AbstractIT.class);
	private static Logger nginxLogger = LoggerFactory.getLogger("nginx-logger");
	private static Logger keycloakLogger = LoggerFactory.getLogger("keycloak-logger");


	private static Network n;

	private static BrowserWebDriverContainer<?> browser;

	private static Integer keycloakPort;
	private static String keycloakHost;


	@BeforeClass
	public static void setupTestEnvironment() throws IOException {

		if (n != null) {
			// Done already
			return;
		}
		
		n = Network.newNetwork();

		logger.debug("Setting up test environment");

		System.setProperty("javax.net.debug", "all");


		// Mock SD adgang
		GenericContainer sdAdgang = new GenericContainer("mockserver/mockserver")
				.withClasspathResourceMapping("compose/sd-adgang/initializerJson.json", "/config/initializerJson.json", BindMode.READ_ONLY)
				.withCommand("-logLevel",  "DEBUG",  "-serverPort", "1081")
				.withEnv("MOCKSERVER_INITIALIZATION_JSON_PATH", "/config/initializerJson.json")
				.withNetwork(n)
				.withNetworkAliases("sd-adgang");
		sdAdgang.start();

		// Mock SD adgang
		GenericContainer sdUsermgr = new GenericContainer("mockserver/mockserver")
				.withClasspathResourceMapping("compose/sd-usermgr/initializerJson.json", "/config/initializerJson.json", BindMode.READ_ONLY)
				.withCommand("-logLevel",  "DEBUG",  "-serverPort", "1082")
				.withEnv("MOCKSERVER_INITIALIZATION_JSON_PATH", "/config/initializerJson.json")
				.withNetwork(n)
				.withNetworkAliases("sd-usermgr");
		sdUsermgr.start();


		// Create browser
		logger.debug("Starting selenium browser");
		browser = createChrome(n);
		browser.start();

		// Start keycloak service
		logger.debug("Starting test keycloak");
		GenericContainer<?> keycloakContainer = geKeycloakContainer(n);
		logContainerOutput(keycloakContainer, keycloakLogger);

		// Start nginx service
		logger.debug("Starting test nginx");
		GenericContainer<?> nginxContainer = geNginxContainer(n);
		nginxContainer.start();
		logContainerOutput(nginxContainer, nginxLogger);		

	}

	private static GenericContainer<?> geKeycloakContainer(Network n) throws IOException {

		GenericContainer<?> keycloackContainer = new GenericContainer<>("kvalitetsit/keycloak-sd-qa:latest")
				.withClasspathResourceMapping("compose/realms/broker-realm.json", "/tmp/broker-realm.json", BindMode.READ_ONLY)
				.withClasspathResourceMapping("compose/realms/form-realm.json", "/tmp/form-realm.json", BindMode.READ_ONLY)
				.withClasspathResourceMapping("compose/realms/nemid-realm.json", "/tmp/nemid-realm.json", BindMode.READ_ONLY)
				.withClasspathResourceMapping("compose/realms/oiosaml-realm.json", "/tmp/oiosaml-realm.json", BindMode.READ_ONLY)
				.withClasspathResourceMapping("compose/realms/oiosaml-organisation-a-realm.json", "/tmp/oiosaml-organisation-a-realm.json", BindMode.READ_ONLY)
				.withClasspathResourceMapping("compose/realms/oiosaml-organisation-b-realm.json", "/tmp/oiosaml-organisation-b-realm.json", BindMode.READ_ONLY)
				.withClasspathResourceMapping("compose/scripts/disable-theme-cache.cli", "/opt/jboss/startup-scripts/disable-theme-cache.cli", BindMode.READ_ONLY)

				.withEnv("KEYCLOAK_USER", KEYCLOAK_ADMIN_USER)
				.withEnv("KEYCLOAK_PASSWORD", KEYCLOAK_ADMIN_PASSWD)
				.withEnv("KEYCLOAK_LOGLEVEL", "TRACE")
				.withEnv("PROXY_ADDRESS_FORWARDING", "true")
				.withEnv("KEYCLOAK_IMPORT", "/tmp/broker-realm.json,/tmp/form-realm.json,/tmp/nemid-realm.json,/tmp/oiosaml-realm.json,/tmp/oiosaml-organisation-a-realm.json,/tmp/oiosaml-organisation-b-realm.json")

				.withNetwork(n)
				.withNetworkAliases("keycloak")
				.withExposedPorts(8080)
				.waitingFor(Wait.forHttp("/auth/realms/broker/protocol/saml/descriptor").withStartupTimeout(Duration.ofMinutes(3)));

		keycloackContainer.start();
		logContainerOutput(keycloackContainer, keycloakLogger);
		keycloakPort = keycloackContainer.getMappedPort(8080);
		keycloakHost = keycloackContainer.getContainerIpAddress();

		// Find the IDP certificate from keycloak and save it to temporary file for use in trust
		/*	Not used in this test
		 * RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> idpMetadata = restTemplate.getForEntity("http://"+keycloakHost+":"+keycloakPort+"/auth/realms/broker/protocol/saml/descriptor", String.class);
		String metadata = idpMetadata.getBody();

		final String TAG_CERTIFICATE_START = "<dsig:X509Certificate>";
		final String TAG_CERTIFICATE_END = "</dsig:X509Certificate>";
		int startIndex = metadata.indexOf(TAG_CERTIFICATE_START);
		int endIndex = metadata.indexOf(TAG_CERTIFICATE_END);
		String certificateContent = metadata.substring(startIndex + TAG_CERTIFICATE_START.length(), endIndex);

		TemporaryFolder folder= new TemporaryFolder(); // It's a junit thing
		folder.create();
		File createdFile= folder.newFile("keycloak-idp.cer");
		BufferedWriter writer = new BufferedWriter(new FileWriter(createdFile));
		writer.append("-----BEGIN CERTIFICATE-----\n");
		writer.append(certificateContent);
		writer.append("-----END CERTIFICATE-----\n");
		writer.close();
		 */
		return keycloackContainer;
	}

	private static BrowserWebDriverContainer<?> createChrome(Network n) {

		DesiredCapabilities caps = DesiredCapabilities.chrome();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.BROWSER, Level.ALL);
		caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
		caps.setJavascriptEnabled(true);
		
		caps.setCapability(CapabilityType.BROWSER_NAME, BrowserType.CHROME);
		
//		ChromeOptions chrome = new ChromeOptions();
	//	chrome.add
		
		BrowserWebDriverContainer<?> browser = new BrowserWebDriverContainer<>().withCapabilities(caps).withNetwork(n);
		return browser;
	}



	protected RemoteWebDriver getWebDriver() {
		return browser.getWebDriver();
	}




	public void createNewUserInFormsRealm(String uniqueUserName, String password) {

		Keycloak keycloak = Keycloak.getInstance(
				"http://"+keycloakHost+":"+keycloakPort+"/auth",
				"master",
				KEYCLOAK_ADMIN_USER,
				KEYCLOAK_ADMIN_PASSWD,
				"admin-cli");

		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setUsername(uniqueUserName);
		userRepresentation.setFirstName("Test");
		userRepresentation.setLastName("Test");
		userRepresentation.setEmail("test@test.dk");
		userRepresentation.setEmailVerified(true);
		userRepresentation.setEnabled(true);

		List<CredentialRepresentation> credentials = new LinkedList<CredentialRepresentation>();
		CredentialRepresentation passwordRepresenation = new CredentialRepresentation();
		passwordRepresenation.setValue(password);
		passwordRepresenation.setTemporary(false);
		passwordRepresenation.setType(CredentialRepresentation.PASSWORD);
		credentials.add(passwordRepresenation);
		userRepresentation.setCredentials(credentials);

		Response response = keycloak.realm(FORM_REALM).users().create(userRepresentation);
		if (response.getStatus() != 201) {
			throw new RuntimeException(response.getStatusInfo().getReasonPhrase());
		}

	}

	public String getKeycloakUrl(String relativePart) {
		return "http://keycloak:8080"+(relativePart.startsWith("/") ? "" : "/")+relativePart;
	}

	public String getNginxUrl(String relativePart) {
		return "http://nginx:8787"+(relativePart.startsWith("/") ? "" : "/")+relativePart;
	}

	public void checkPageIsReady(RemoteWebDriver driver) {

		JavascriptExecutor js = (JavascriptExecutor)driver;

		//This loop will rotate for 25 times to check If page Is ready after every 1 second.
		//You can replace your value with 25 If you wants to Increase or decrease wait time.
		for (int i=0; i<25; i++){ 
			try {
				Thread.sleep(1000);
			}catch (InterruptedException e) {} 
			//To check page ready state.
			if (js.executeScript("return document.readyState").toString().equals("complete")){ 
				break; 
			}   
		}
		
		return;
	}

	private static GenericContainer<?> geNginxContainer(Network n) {
		GenericContainer<?> nginxContainer = new GenericContainer<>("nginx:1.17.8")
				.withClasspathResourceMapping("compose/nginx/nginx.conf", "/etc/nginx/nginx.conf", BindMode.READ_ONLY)
				.withClasspathResourceMapping("compose/nginx/conf.d/8787_default.conf", "/etc/nginx/conf.d/8787_default.conf", BindMode.READ_ONLY)

				.withNetwork(n)
				.withNetworkAliases("nginx")
				.withExposedPorts(8787);

		return nginxContainer;
	}

	private static void logContainerOutput(GenericContainer<?> container, Logger logger) {
		logger.info("Attaching logger to container: " + container.getContainerInfo().getName());
		Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
		container.followOutput(logConsumer);
	}
	
	public void switchToLoginFrame(RemoteWebDriver webdriver) {
		webdriver.switchTo().frame(SD_LOGIN_IFRAME_ID);
		
	}

	public void chooseFormsLoginMethod(RemoteWebDriver webdriver) {
		chooseLoginMethod(webdriver, FORM_LOGIN_CHOICE_ID);
	}

	public void chooseOioSamlLoginMethod(RemoteWebDriver webdriver) {
		chooseLoginMethod(webdriver, OIOSAML_LOGIN_CHOICE_ID);
	}

	public void chooseLoginMethod(RemoteWebDriver webdriver, String loginMethodLinkId) {
	
		webdriver.switchTo().parentFrame();
		
		webdriver.findElementById(loginMethodLinkId).click();
		
		checkPageIsReady(webdriver);
	}
}
