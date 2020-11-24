package dk.kvalitetsit.keycloak.sd.authenticator;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Arrays;
import java.util.List;

public class CheckLoginMethodAuthenticatorFactory implements AuthenticatorFactory {
    public static final String PROVIDER_ID = "check-login-method-authenticator";

    public CheckLoginMethodAuthenticatorFactory() {

    }

    public Authenticator create(KeycloakSession session) {
        return new CheckLoginMethodAuthenticator();
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
        return "Check login method";
    }

    public String getHelpText() {
        return "Verify that the used login method is allowed for the user.";
    }

    public List<ProviderConfigProperty> getConfigProperties() {
        // Login method to be checked
        ProviderConfigProperty loginMethodProperty = new ProviderConfigProperty();
        loginMethodProperty.setDefaultValue("form");
        loginMethodProperty.setHelpText("Choose login method to verify");
        loginMethodProperty.setLabel("method");
        loginMethodProperty.setName("method");
        loginMethodProperty.setOptions(Arrays.asList("form", "nemid", "oiosaml"));
        loginMethodProperty.setSecret(false);
        loginMethodProperty.setType(ProviderConfigProperty.LIST_TYPE);

        // Endpoint to use for checking
        ProviderConfigProperty endpointProperty = new ProviderConfigProperty();
        endpointProperty.setHelpText("Set endpoint to verify login method against.");
        endpointProperty.setLabel("endpoint");
        endpointProperty.setName("endpoint");
        endpointProperty.setSecret(false);
        endpointProperty.setType(ProviderConfigProperty.STRING_TYPE);

        return Arrays.asList(loginMethodProperty, endpointProperty);
    }

    public boolean isUserSetupAllowed() {
        return false;
    }
}