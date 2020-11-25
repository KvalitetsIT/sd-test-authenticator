package dk.kvalitetsit.keycloak.sd.qa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestConfiguration.class } ) 
public abstract class AbstractIntegrationTest {

	private static final String FORM_REALM = "form";
	private static Logger logger = LoggerFactory.getLogger(AbstractIntegrationTest.class);
	private static Logger nginxLogger = LoggerFactory.getLogger("nginx-logger");
	private static Logger keycloakLogger = LoggerFactory.getLogger("keycloak-logger");

	private static Network n;

	private static Integer keycloakPort;
	private static String keycloakHost;


	@BeforeClass
	public static void setupTestEnvironment() throws IOException {

		logger.debug("Setting up test environment");

		System.setProperty("javax.net.debug", "all");

		n = Network.newNetwork();

		// Start keycloak service
		logger.debug("Starting test keycloak");
		GenericContainer<?> keycloakContainer = geKeycloakContainer(n);
		keycloakContainer.start();
		logContainerOutput(keycloakContainer, keycloakLogger);

		// Start nginx service
		logger.debug("Starting test nginx");
		GenericContainer<?> nginxContainer = geNginxContainer(n);
		nginxContainer.start();
		logContainerOutput(nginxContainer, nginxLogger);
	}

	private static GenericContainer<?> geKeycloakContainer(Network n) throws IOException {

		GenericContainer<?> keycloackContainer = new GenericContainer<>("jboss/keycloak:9.0.2")
				.withClasspathResourceMapping("compose/realms/broker-realm.json", "/tmp/broker-realm.json", BindMode.READ_ONLY)
				.withClasspathResourceMapping("compose/realms/form-realm.json", "/tmp/form-realm.json", BindMode.READ_ONLY)
				.withClasspathResourceMapping("compose/realms/nemid-realm.json", "/tmp/nemid-realm.json", BindMode.READ_ONLY)
				.withClasspathResourceMapping("compose/realms/oiosaml-realm.json", "/tmp/oiosaml-realm.json", BindMode.READ_ONLY)
				.withClasspathResourceMapping("compose/realms/oiosaml-organisation-a-realm.json", "/tmp/oiosaml-organisation-a-realm.json", BindMode.READ_ONLY)
				.withClasspathResourceMapping("compose/realms/oiosaml-organisation-b-realm.json", "/tmp/oiosaml-organisation-b-realm.json", BindMode.READ_ONLY)
				.withClasspathResourceMapping("compose/scripts/disable-theme-cache.cli", "/opt/jboss/startup-scripts/disable-theme-cache.cli", BindMode.READ_ONLY)

				.withEnv("KEYCLOAK_USER", "sdadmin")
				.withEnv("KEYCLOAK_PASSWORD", "Test1234")
				.withEnv("KEYCLOAK_LOGLEVEL", "DEBUG")
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
		RestTemplate restTemplate = new RestTemplate();
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

		return keycloackContainer;
	}

	protected static BrowserWebDriverContainer<?> createChrome() {
		return (BrowserWebDriverContainer<?>) new BrowserWebDriverContainer<>().withCapabilities(new ChromeOptions()).withNetwork(n);
	}



	public void createNewUserInFormsRealm(String uniqueUserName, String password) {

		Keycloak keycloak = Keycloak.getInstance(
				"http://"+keycloakHost+":"+keycloakPort+"/auth",
				"master",
				"sd",
				"Test1234",
				"admin-cli");

		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setUsername(uniqueUserName);
		List<CredentialRepresentation> credentials = new LinkedList<CredentialRepresentation>();
		CredentialRepresentation passwordRepresenation = new CredentialRepresentation();
		passwordRepresenation.setValue(password);
		passwordRepresenation.setTemporary(false);
		credentials.add(passwordRepresenation);
		userRepresentation.setCredentials(credentials);
		keycloak.realm(FORM_REALM).users().create(userRepresentation);
		
		List<UserRepresentation> users = keycloak.realm("test").users().list();

		if (users != null) {
			for (UserRepresentation user : users) {
				System.out.println("Found user: "+user.getUsername());
			}
		}

		// TODO Auto-generated method stub

	}

	public String getKeycloakUrl(String relativePart) {
		return "http://keycloak:8080"+(relativePart.startsWith("/") ? "" : "/")+relativePart;
	}

	public String getNginxUrl(String relativePart) {
		return "http://nginx:80"+(relativePart.startsWith("/") ? "" : "/")+relativePart;
	}


	private static GenericContainer<?> geNginxContainer(Network n) {
		GenericContainer<?> nginxContainer = new GenericContainer<>("nginx:1.17.8")
				.withClasspathResourceMapping("compose/nginx/nginx.conf", "/etc/nginx/nginx.conf", BindMode.READ_ONLY)
				.withClasspathResourceMapping("compose/nginx/conf.d/80_default.conf", "/etc/nginx/conf.d/80_default.conf", BindMode.READ_ONLY)

				.withNetwork(n)
				.withNetworkAliases("nginx")
				.withExposedPorts(80)
				.waitingFor(Wait.forHttp("/").withStartupTimeout(Duration.ofMinutes(3)));

		return nginxContainer;
	}

	private static void logContainerOutput(GenericContainer<?> container, Logger logger) {
		logger.info("Attaching logger to container: " + container.getContainerInfo().getName());
		Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
		container.followOutput(logConsumer);
	}

}
