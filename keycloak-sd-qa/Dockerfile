FROM kvalitetsit/medarbejdernet-theme:latest AS medarbejdernet-theme

FROM kvalitetsit/silkeborgdata-theme:latest AS silkeborgdata-theme

FROM kvalitetsit/keycloak-sd-authenticator:latest AS keycloak-sd-authenticator


FROM jboss/keycloak:9.0.2
COPY --from=medarbejdernet-theme /module/*.jar /opt/jboss/keycloak/standalone/deployments/
COPY --from=silkeborgdata-theme /module/*.jar /opt/jboss/keycloak/standalone/deployments/
COPY --from=keycloak-sd-authenticator /module/*.ear /opt/jboss/keycloak/standalone/deployments/