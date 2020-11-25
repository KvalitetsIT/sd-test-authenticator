package dk.kvalitetsit.keycloak.sd.authenticator.config;

import dk.kvalitetsit.keycloak.sd.authenticator.constants.ConfigProperty;
import org.keycloak.authentication.AuthenticationFlowContext;

import java.util.Map;

public class ConfigPropertyExtractor {
    public String getLoginMethod(AuthenticationFlowContext context) {
        return getPropertyFromConfig(context, ConfigProperty.METHOD);
    }

    public String getEndpoint(AuthenticationFlowContext context) {
        return getPropertyFromConfig(context, ConfigProperty.ENDPOINT);
    }

    public String getPropertyFromConfig(AuthenticationFlowContext context, ConfigProperty property) {
        Map<String, String> config = context.getAuthenticatorConfig().getConfig();
        if(!config.containsKey(property.toString())) {
            throw new IllegalStateException(String.format("Config property %s was not configured!", property));
        }
        return config.get(property.toString());
    }
}
