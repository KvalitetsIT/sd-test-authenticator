package dk.kvalitetsit.keycloak.sd.authenticator;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.broker.IdpUsernamePasswordForm;
import org.keycloak.forms.login.LoginFormsProvider;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class DynamicIdpUsernamePasswordForm extends IdpUsernamePasswordForm {

    protected Response challenge(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
        LoginFormsProvider forms = context.form();
        if (formData.size() > 0) {
            forms.setFormData(formData);
        }
        forms.setAttribute("foo", "bar");

        return forms.createLoginUsernamePassword();
    }
}
