package dk.kvalitetsit.keycloak.sd.authenticator;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public class LoginSelectorAuthenticatorFactory implements AuthenticatorFactory {
    public static final String PROVIDER_ID = "login-selector-authenticate";

    public LoginSelectorAuthenticatorFactory() {

    }

    public Authenticator create(KeycloakSession session) {
        return new LoginSelectorAuthenticator();
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
        return false;
    }

    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    public String getDisplayType() {
        return "Retrieve available login methods";
    }

    public String getHelpText() {
        return "Retrieves possible login methods for user, puts result on session.";
    }

    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }

    public boolean isUserSetupAllowed() {
        return false;
    }
}
