package dk.kvalitetsit.keycloak.sd.authenticator;

import dk.kvalitetsit.keycloak.sd.authenticator.model.LdapAuthenticationRequest;
import dk.kvalitetsit.keycloak.sd.authenticator.model.LdapAuthenticationResponse;
import org.apache.http.HttpStatus;
import org.checkerframework.checker.units.qual.A;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.broker.IdpUsernamePasswordForm;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class LdapRestAuthenticator extends UsernamePasswordForm {
    private static final Logger LOGGER = Logger.getLogger(LdapRestAuthenticator.class);

    @Override
    public void action(AuthenticationFlowContext context) {
        LOGGER.info("Action in LdapRestAuthenticator proceeding.");

        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        if (formData.containsKey("cancel")) {
            context.cancelLogin();
            return;
        }

        // TODO: Is Medarbejdernet channel (determine from Referer-header?)? Fetch user data. Figure out what what we need said data for.

        // FORM login allowed for institution?
        if(!loginAllowedForUser(formData)) {
            // Redirect to "Decline" page
            context.failure(AuthenticationFlowError.INVALID_USER, context.form().setError("Form login not allowed.").createErrorPage(Response.Status.FORBIDDEN));
            return;
        }

        // Authenticate in LDAP
        LdapAuthenticationRequest authenticationRequest = new LdapAuthenticationRequest();
        authenticationRequest.setUsername(formData.getFirst("username"));
        authenticationRequest.setPassword(formData.getFirst("password"));
        SimpleHttp sh = SimpleHttp.doPost("http://ldap-rest:1080/authenticate", context.getSession()).json(authenticationRequest);

        try {
            int status = sh.asStatus();
            if(status != HttpStatus.SC_OK) {
                context.failure(AuthenticationFlowError.INTERNAL_ERROR, context.form().setError("Error during authentication").createErrorPage(Response.Status.INTERNAL_SERVER_ERROR));
            }

            LdapAuthenticationResponse authenticationResponse = sh.asJson(LdapAuthenticationResponse.class);
            // Authentication successful?
            if(authenticationResponse.getStatus().equals("OK")) {
                // Authentication ok

                UserModel user = context.getSession().users().getUserByUsername(formData.getFirst("username"), context.getRealm());
                if(user == null) {
                    user = context.getSession().users().addUser(context.getRealm(), formData.getFirst("username"));
                    user.setEnabled(true);
                }

                context.setUser(user);
                context.success();

                // Add some data to login event
                context.getEvent().detail("INST", "Initech");
            }
            else {
                // Authentication failed, redirect to error pages

                switch(authenticationResponse.getMessage()) {
                    // Is password invalid?
                    case "INVALID_PASSWORD":
                        // Redirect to change password page
                        context.failure(AuthenticationFlowError.INVALID_USER, context.form().setError("Password is expired, please change password").createErrorPage(Response.Status.FORBIDDEN));
                        break;
                    // Is invalid credentials?
                    case "INVALID_CREDENTIALS":
                        // Redirect to login page with error
                        context.failure(AuthenticationFlowError.INVALID_USER, context.form().setError("Username or password is invalid.").createErrorPage(Response.Status.FORBIDDEN));
                        break;
                    // Is user account disabled?
                    case "ACCOUNT_DISABLED":
                        // Redirect to user account disabled page
                        context.failure(AuthenticationFlowError.INVALID_USER, context.form().setError("Account is disabled.").createErrorPage(Response.Status.FORBIDDEN));
                        break;
                    // Is user account locked?
                    case "ACCOUNT_LOCKED":
                        // Redirect to user account temporarily locked page
                        context.failure(AuthenticationFlowError.INVALID_USER, context.form().setError("Account is temporarily locked.").createErrorPage(Response.Status.FORBIDDEN));
                        break;
                    default:
                        // Redirect to login page with 'unknown' error
                        context.failure(AuthenticationFlowError.INTERNAL_ERROR, context.form().setError("Unknown error during authentication").createErrorPage(Response.Status.INTERNAL_SERVER_ERROR));
                }
            }
        }
        catch(IOException e) {
            context.failure(AuthenticationFlowError.INTERNAL_ERROR, context.form().setError("Error during authentication").createErrorPage(Response.Status.INTERNAL_SERVER_ERROR));
        }
    }

    private boolean loginAllowedForUser(MultivaluedMap<String, String> formData) {
        if(!formData.containsKey("username")) {
            throw new IllegalStateException("Expected to find username-key in formData!");
        }
        String username = formData.getFirst("username");
        return !username.equals("forbidden-user");
    }
}
