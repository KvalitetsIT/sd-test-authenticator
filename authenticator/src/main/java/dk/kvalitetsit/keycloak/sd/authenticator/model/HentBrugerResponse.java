package dk.kvalitetsit.keycloak.sd.authenticator.model;

import java.util.List;

public class HentBrugerResponse {
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    private List<User> users;
}
