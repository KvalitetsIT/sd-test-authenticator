package dk.kvalitetsit.keycloak.sd.authenticator;

import javax.ws.rs.core.Response;

import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;

public class FinalizeLoginProcessAction implements RequiredActionProvider {

	private static final String LOGIN_FINAL = "LOGIN_FINAL";

	@Override
	public void close() {

	}

	@Override
	public void evaluateTriggers(RequiredActionContext context) {
		
		if (!sessionHasBeenFinalize(context)) {
			context.getUser().addRequiredAction(FinalizeLoginProcessActionFactory.FINALIZELOGIN);
		}
	}

	private boolean sessionHasBeenFinalize(RequiredActionContext context) {
		return context.getSession().getAttribute(LOGIN_FINAL) != null && context.getSession().getAttribute(LOGIN_FINAL).equals("true");
	}

	private void finalize(RequiredActionContext context) {
		context.getSession().setAttribute(LOGIN_FINAL, "true");
	}

	
	@Override
	public void requiredActionChallenge(RequiredActionContext context) {
		Response challenge = context.form().createForm("finalizelogin.ftl");
		context.challenge(challenge);
	}

	@Override
	public void processAction(RequiredActionContext context) {
		finalize(context);
		context.success();		
	}
}
