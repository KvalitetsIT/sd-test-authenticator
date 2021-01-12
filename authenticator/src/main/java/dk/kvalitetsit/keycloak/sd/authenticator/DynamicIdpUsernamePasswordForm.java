package dk.kvalitetsit.keycloak.sd.authenticator;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.broker.IdpUsernamePasswordForm;
import org.keycloak.forms.login.LoginFormsProvider;

public class DynamicIdpUsernamePasswordForm extends IdpUsernamePasswordForm {

	private static final Logger LOGGER = Logger.getLogger(DynamicIdpUsernamePasswordForm.class);

	protected Response challenge(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
		LoginFormsProvider forms = context.form();
		if (formData.size() > 0) {
			forms.setFormData(formData);
		}
		setAllowedLoginMethods(context, forms);

		return forms.createLoginUsernamePassword();
	}

	private void setAllowedLoginMethods(AuthenticationFlowContext context, LoginFormsProvider formsProvider) {
		// Extract header
		String clientIp = "";
		List<String> xForwardedForHeaderValues = context.getHttpRequest().getHttpHeaders().getRequestHeader("X-Forwarded-For");
		if(xForwardedForHeaderValues != null && !xForwardedForHeaderValues.isEmpty()) {
			clientIp = xForwardedForHeaderValues.get(0);
		}

		Cookie selectLoginCookie = context.getHttpRequest().getHttpHeaders().getCookies().get("selectlogin");
		String val = (selectLoginCookie != null ? selectLoginCookie.getValue() : "form");
		formsProvider.setAttribute("SELECTED_LOGIN", Arrays.asList(val));
		LOGGER.debug("Setting SELECTED_LOGIN: "+val);

		if(clientIp.equals("127.0.0.2")) {
			formsProvider.setAttribute("ALLOWED_LOGIN_METHODS", Arrays.asList("form", "nemid", "oiosaml"));


			//            formsProvider.setAttribute("FORM_LOGIN_ALLOWED", true);
			//            formsProvider.setAttribute("NEMID_LOGIN_ALLOWED", true);
			//            formsProvider.setAttribute("OIOSAML_LOGIN_ALLOWED", true);
		}
		else {
			formsProvider.setAttribute("ALLOWED_LOGIN_METHODS", Arrays.asList("form", "nemid"));

			//            formsProvider.setAttribute("FORM_LOGIN_ALLOWED", true);
			//            formsProvider.setAttribute("NEMID_LOGIN_ALLOWED", true);
		}
	}
}
