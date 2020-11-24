package dk.kvalitetsit.keycloak.sd.authenticator.constants;

public enum UserAttribute {
    INSTITUTION;

    public String toString() {
        return super.toString().toLowerCase();
    }
}
