package dk.kvalitetsit.keycloak.sd.authenticator;

import org.keycloak.Config.Scope;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class FinalizeLoginProcessActionFactory implements RequiredActionFactory {

	public static final String FINALIZELOGIN = "FINALIZELOGIN";

	@Override
	public RequiredActionProvider create(KeycloakSession session) {
		return new FinalizeLoginProcessAction();
	}

	@Override
	public void init(Scope config) {
	}

	@Override
	public void postInit(KeycloakSessionFactory factory) {
	}

	@Override
	public void close() {
	}

	@Override
	public String getId() {
		return FINALIZELOGIN;
	}

	@Override
	public String getDisplayText() {
		return "Finalize Login";
	}
}
