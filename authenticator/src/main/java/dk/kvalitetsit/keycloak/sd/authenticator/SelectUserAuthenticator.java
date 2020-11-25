package dk.kvalitetsit.keycloak.sd.authenticator;

import dk.kvalitetsit.keycloak.sd.authenticator.constants.ConfigProperty;
import dk.kvalitetsit.keycloak.sd.authenticator.constants.UserAttribute;
import dk.kvalitetsit.keycloak.sd.authenticator.model.LoginChoicesResponse;
import org.apache.http.HttpStatus;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SelectUserAuthenticator implements Authenticator {
    private static final Logger LOGGER = Logger.getLogger(SelectUserAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        LOGGER.debug("Authenticate in SelectUserAuthenticator proceeding ...");

        // Get username to retrieve users for

        // Retrieve users

        // Check users:
        //  - none: Redirect to error page
        //  - at least one locked: Redirect to another error page
        //  - more than one: Make the user select one
        //  - exactly one: Choose that one (add as sd_userId attribute)


        context.success();
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        LOGGER.debug("Action in SelectUserAuthenticator proceeding ...");

    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {

    }

    @Override
    public void close() {

    }
}