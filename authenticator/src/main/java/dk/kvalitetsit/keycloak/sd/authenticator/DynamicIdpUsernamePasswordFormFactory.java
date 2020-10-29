package dk.kvalitetsit.keycloak.sd.authenticator;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public class DynamicIdpUsernamePasswordFormFactory implements AuthenticatorFactory {
    public static final String PROVIDER_ID = "dynamic-idp-username-password-form";

    public DynamicIdpUsernamePasswordFormFactory() {

    }

    public Authenticator create(KeycloakSession session) {
        return new DynamicIdpUsernamePasswordForm();
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
        return "Dynamic Username Password Form";
    }

    public String getHelpText() {
        return "Retrieves possible login methods for user, displays username/password form based on result.";
    }

    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }

    public boolean isUserSetupAllowed() {
        return false;
    }
}
