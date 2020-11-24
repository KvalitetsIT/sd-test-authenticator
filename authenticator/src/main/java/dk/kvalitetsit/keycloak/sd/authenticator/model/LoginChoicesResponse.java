package dk.kvalitetsit.keycloak.sd.authenticator.model;

import java.util.List;

public class LoginChoicesResponse {
    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    private List<String> choices;
}
