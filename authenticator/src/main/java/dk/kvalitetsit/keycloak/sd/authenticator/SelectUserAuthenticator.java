package dk.kvalitetsit.keycloak.sd.authenticator;

import dk.kvalitetsit.keycloak.sd.authenticator.config.ConfigPropertyExtractor;
import dk.kvalitetsit.keycloak.sd.authenticator.constants.ConfigProperty;
import dk.kvalitetsit.keycloak.sd.authenticator.constants.UserAttribute;
import dk.kvalitetsit.keycloak.sd.authenticator.model.HentBrugerResponse;
import dk.kvalitetsit.keycloak.sd.authenticator.model.LoginChoicesResponse;
import dk.kvalitetsit.keycloak.sd.authenticator.model.User;
import org.apache.http.HttpStatus;
import org.checkerframework.checker.units.qual.C;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SelectUserAuthenticator implements Authenticator {
    private static final Logger LOGGER = Logger.getLogger(SelectUserAuthenticator.class);

    public static final String CHANNEL_ATTRIBUTE = "channel";

    public static final String USERS_ATTRIBUTE = "users";

    public static final String USER_ATTRIBUTE = "user";

    private ConfigPropertyExtractor configPropertyExtractor = new ConfigPropertyExtractor();

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        LOGGER.debug("Authenticate in SelectUserAuthenticator proceeding ...");

        // TODO - determine and set channel somewhere else
        context.getSession().setAttribute(CHANNEL_ATTRIBUTE, "medarbejdernet");

        // Retrieve users
        List<User> users = retrieveUsers(context);

        // Store the result for later verification
        context.getSession().setAttribute(USERS_ATTRIBUTE, users);

        // Check users:
        //  - none: Redirect to error page
        //  - at least one locked: Redirect to another error page
        //  - more than one: Make the user select one
        //  - exactly one: Choose that one (add as sd_userId attribute)

        // If no users exist, redirect to error page
        if(users.isEmpty()) {
            context.failure(AuthenticationFlowError.INVALID_USER, context.form().setError("No associated users found.").createErrorPage(Response.Status.FORBIDDEN));
            return;
        }

        // If an associated user is locked, redirect to error page
        for(User u : users) {
            if(u.isLocked()) {
                context.failure(AuthenticationFlowError.INVALID_USER, context.form().setError(String.format("Associated user %s is locked, login therefore fails.", u.getUsername())).createErrorPage(Response.Status.FORBIDDEN));
                return;
            }
        }

        // If more than one user is associated, prompt the user to select which one to use.
        if(users.size() > 1) {
            LoginFormsProvider loginFormsProvider = context.form();

            loginFormsProvider.setAttribute(USERS_ATTRIBUTE, users);

            context.challenge(loginFormsProvider.createForm("select-user.ftl"));
            return;
        }

        // Exactly one user was associated, so just use that one.
        context.getUser().setSingleAttribute(USER_ATTRIBUTE, users.get(0).getUsername());

        context.success();
    }

    private List<User> retrieveUsers(AuthenticationFlowContext context) {
        String username = context.getUser().getFirstAttribute("oiosaml-username");
        String endpoint = configPropertyExtractor.getEndpoint(context);
        String channel = context.getSession().getAttribute(CHANNEL_ATTRIBUTE, String.class);

        SimpleHttp sh = SimpleHttp.doGet(String.format("%s/rest/user-1.0/hentBruger/%s/%s", endpoint, username, channel), context.getSession());
        int status = -1;
        try {
            status = sh.asStatus();
            if(status != HttpStatus.SC_OK) {
                throw new IllegalStateException(String.format("Could not retrieve users. Status code: %d", status));
            }

            HentBrugerResponse response = sh.asJson(HentBrugerResponse.class);
            return response.getUsers();
        }
        catch(IOException e) {
            throw new IllegalStateException("Error getting statuscode from response.", e);
        }

    }

    @Override
    public void action(AuthenticationFlowContext context) {
        LOGGER.debug("Action in SelectUserAuthenticator proceeding ...");

        // Extract selected username
        String username = getSelectedUsername(context);

        // TODO - Verify that it was in fact one of the possible values
        //List<User> users = context.getSession().getAttribute(USERS_ATTRIBUTE, List.class);

        // Store it
        context.getUser().setSingleAttribute(USER_ATTRIBUTE, username);

        context.success();
    }

    private String getSelectedUsername(AuthenticationFlowContext context) {
        String username = context.getHttpRequest().getDecodedFormParameters().getFirst("username");
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("No username provided!");
        }
        return username;
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