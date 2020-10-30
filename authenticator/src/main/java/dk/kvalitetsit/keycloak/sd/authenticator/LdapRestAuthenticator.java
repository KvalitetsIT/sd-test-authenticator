package dk.kvalitetsit.keycloak.sd.authenticator;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.broker.IdpUsernamePasswordForm;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class LdapRestAuthenticator extends UsernamePasswordForm {
    private static final Logger LOGGER = Logger.getLogger(LdapRestAuthenticator.class);

    @Override
    public void action(AuthenticationFlowContext context) {
        LOGGER.info("Action in LdapRestAuthenticator proceeding.");

        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        if (formData.containsKey("cancel")) {
            context.cancelLogin();
        }
        else if(!loginAllowedForUser(formData)) {
            context.failure(AuthenticationFlowError.INVALID_USER, context.form().setError("Form login not allowed.").createErrorPage(Response.Status.FORBIDDEN));
        }
        else if (this.validateForm(context, formData)) {
            context.success();
            context.getEvent().detail("INST", "Initech");
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
