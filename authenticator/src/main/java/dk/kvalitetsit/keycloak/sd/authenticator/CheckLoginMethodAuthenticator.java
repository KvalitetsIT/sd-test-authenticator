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

public class CheckLoginMethodAuthenticator implements Authenticator {
    private static final Logger LOGGER = Logger.getLogger(CheckLoginMethodAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        LOGGER.debug("Authenticate in CheckLoginMethodAuthenticator proceeding ...");

        // Determine allowed login methods based on institution
        List<String> allowedLoginMethods = getAllowedLoginMethods(context);

        // Extract attempted login method from config
        String attemptedloginMethod = getLoginMethod(context);

        // If not allowed, redirect to error page
        if(!loginMethodAllowed(allowedLoginMethods, attemptedloginMethod)) {
            context.failure(AuthenticationFlowError.INVALID_USER, context.form().setError(String.format("Login method %s not allowed.", attemptedloginMethod)).createErrorPage(Response.Status.FORBIDDEN));
            return;
        }

        context.success();
    }

    private List<String> getAllowedLoginMethods(AuthenticationFlowContext context) {
        String endpoint = getEndpoint(context);
        String institution = getInstitution(context);

        // Invoke sd-adgang service
        SimpleHttp sh = SimpleHttp.doGet(String.format("%s/rest/1.0/loginChoices/%s", endpoint, institution), context.getSession());
        int status = -1;
        try {
            status = sh.asStatus();
            if(status != HttpStatus.SC_OK) {
                throw new IllegalStateException(String.format("Could not retrieve login methods. Status code: %d", status));
            }

            LoginChoicesResponse response = sh.asJson(LoginChoicesResponse.class);
            return response.getChoices();
        }
        catch(IOException e) {
            throw new IllegalStateException("Error getting statuscode from response.", e);
        }
    }

    private String getLoginMethod(AuthenticationFlowContext context) {
        return getPropertyFromConfig(context, ConfigProperty.METHOD);
    }

    private String getEndpoint(AuthenticationFlowContext context) {
        return getPropertyFromConfig(context, ConfigProperty.ENDPOINT);
    }

    private String getPropertyFromConfig(AuthenticationFlowContext context, ConfigProperty property) {
        Map<String, String> config = context.getAuthenticatorConfig().getConfig();
        if(!config.containsKey(property.toString())) {
            throw new IllegalStateException(String.format("Config property %s was not configured!", property));
        }
        return config.get(property.toString());
    }

    private String getInstitution(AuthenticationFlowContext context) {
        String institution = context.getUser().getFirstAttribute(UserAttribute.INSTITUTION.toString());
        if(institution == null || institution.isEmpty()) {
            throw new IllegalStateException(String.format("User did not have attribute of name %s!", UserAttribute.INSTITUTION.toString()));
        }
        return institution;
    }

    private boolean loginMethodAllowed(List<String> allowedMethods, String attemptedMethod) {
        return allowedMethods.contains(attemptedMethod);
    }

    @Override
    public void action(AuthenticationFlowContext context) {

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
