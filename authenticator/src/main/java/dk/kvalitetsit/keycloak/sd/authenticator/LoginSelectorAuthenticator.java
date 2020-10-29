package dk.kvalitetsit.keycloak.sd.authenticator;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class LoginSelectorAuthenticator implements Authenticator {
    private static final Logger LOGGER = Logger.getLogger(LoginSelectorAuthenticator.class);

    public LoginSelectorAuthenticator() {
    }

    public boolean requiresUser() {
        return false;
    }

    public void authenticate(AuthenticationFlowContext context) {
        LOGGER.info("Authentication in LoginSelectorAuthenticator proceeding.");

        KeycloakSession session = context.getSession();

        context.getAuthenticationSession().setUserSessionNote("foo", "bar");


        context.success();
    }

    public void action(AuthenticationFlowContext context) {
        LOGGER.info("Action in LoginSelectorAuthenticator proceeding.");

        context.success();
    }

    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    public void close() {
    }
}
