package dk.kvalitetsit.keycloak.sd.authenticator;

import dk.kvalitetsit.keycloak.sd.authenticator.constants.ConfigProperty;
import dk.kvalitetsit.keycloak.sd.authenticator.constants.LoginMethod;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Arrays;
import java.util.List;

public class SelectUserAuthenticatorFactory implements AuthenticatorFactory {
    public static final String PROVIDER_ID = "select-user-authenticator";

    public SelectUserAuthenticatorFactory() {

    }

    public Authenticator create(KeycloakSession session) {
        return new SelectUserAuthenticator();
    }

    public void init(Config.Scope config) {
    }

    public void postInit(KeycloakSessionFactory factory) {
    }

    public void close() {
    }

    public String getId() {
        return PROVIDER_ID;
    }

    public String getReferenceCategory() {
        return PROVIDER_ID;
    }

    public boolean isConfigurable() {
        return true;
    }

    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    public String getDisplayType() {
        return "Select user from sd-adgang";
    }

    public String getHelpText() {
        return "Check and select user from sd-adgang, prompting the client for input if required.";
    }

    public List<ProviderConfigProperty> getConfigProperties() {
        // Endpoint to use for checking
        ProviderConfigProperty endpointProperty = new ProviderConfigProperty();
        endpointProperty.setHelpText("Set endpoint to retrieve users from.");
        endpointProperty.setLabel(ConfigProperty.ENDPOINT.toString().toLowerCase());
        endpointProperty.setName(ConfigProperty.ENDPOINT.toString());
        endpointProperty.setSecret(false);
        endpointProperty.setType(ProviderConfigProperty.STRING_TYPE);

        return Arrays.asList(endpointProperty);
    }

    public boolean isUserSetupAllowed() {
        return false;
    }
}