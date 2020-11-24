package dk.kvalitetsit.keycloak.sd.authenticator.constants;

public enum LoginMethod {
    FORM, NEMID, OIOSAML;

    public String toString() {
        return super.toString().toLowerCase();
    }
}
