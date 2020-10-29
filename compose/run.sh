#!/bin/bash

echo "Starting services ..."
`docker-compose up -d --build`

docker attach compose_keycloak_1 &

echo "Waiting for Keycloak to be ready ..."

max_iterations=10
iterations=0
code=$(curl -s -o /dev/null -w "%{http_code}" localhost:8787/auth/)
while [ $code -ne 200 ];
do
    iterations=$iterations+1;
    if [ $iterations == $max_iterations ]; then
       echo "Maximum number of iterations exceeded, failing ..."
       exit 1
    fi
    
    echo "Keycloak not ready yet, sleeping 5s ..."
    sleep 5;
    code=$(curl -s -o /dev/null -w "%{http_code}" localhost:8787/auth/)
done;

echo "Keycloak ready ..."

echo "Creating user for testing ..."

# Get access token for the REST api
echo "Getting access token ..."
token=$(curl -s -d client_id=admin-cli -d username=medcom -d password=Test1234 -d grant_type=password http://localhost:8787/auth/realms/master/protocol/openid-connect/token | jq -r .access_token)
echo "Got access token $token, creating test user ..."

echo "Creating test user in form realm ..."
code=$(curl -s -d "{\"username\": \"test\", \"credentials\": [ { \"type\": \"password\", \"value\": \"Test1234\" } ], \"enabled\": true}" -H "Authorization: bearer $token" -X POST --header "Content-Type: application/json" -w "%{http_code}" http://localhost:8787/auth/admin/realms/form/users);
if [ $code -ne 201 ]; then
    echo "Could not create test user, got statuscode $code back. Failing ..."
    exit 1
fi

wait
