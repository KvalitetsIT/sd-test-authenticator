package dk.kvalitetsit.keycloak.sd.authenticator.model;

public class User {
    private String username;
    private boolean locked;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
