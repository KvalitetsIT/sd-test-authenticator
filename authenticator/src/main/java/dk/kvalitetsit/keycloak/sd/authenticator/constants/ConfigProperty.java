package dk.kvalitetsit.keycloak.sd.authenticator.constants;

public enum ConfigProperty {
    METHOD,
    ENDPOINT;

    public String toString() {
        return super.toString().toLowerCase();
    }
}
