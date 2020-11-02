package dk.kvalitetsit.keycloak.sd.authenticator;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public class LdapRestAuthenticatorFactory implements AuthenticatorFactory {
    public static final String PROVIDER_ID = "ldap-rest-authenticate";

    public LdapRestAuthenticatorFactory() {

    }

    public Authenticator create(KeycloakSession session) {
        return new LdapRestAuthenticator();
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
        return "Authenticate against LDAP";
    }

    public String getHelpText() {
        return "Verify that login is allowed, authenticate against LDAP, display various error pages in case of failure.";
    }

    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }

    public boolean isUserSetupAllowed() {
        return false;
    }
}
