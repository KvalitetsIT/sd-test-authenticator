#!/bin/bash

echo "Creating users for testing ..."

# Get access token for the REST api
echo "Getting access token ..."
token=$(curl -s -d client_id=admin-cli -d username=sdadmin -d password=Test1234 -d grant_type=password http://nginx:8787/auth/realms/master/protocol/openid-connect/token | jq -r .access_token)
echo "Got access token $token, creating test users ..."

echo "Creating test user in form realm ..."
code=$(curl -s -d "{\"username\": \"test\", \"credentials\": [ { \"type\": \"password\", \"value\": \"Test1234\" } ], \"enabled\": true}" -H "Authorization: bearer $token" -X POST --header "Content-Type: application/json" -w "%{http_code}" http://nginx:8787/auth/admin/realms/form/users);
#if [ $code -ne 201 ]; then
#    echo "Could not create test user, got statuscode $code back. Failing ..."
  #  exit 1
#fi

echo "Creating test user in organisation-a realm ..."
code=$(curl -s -d "{\"username\": \"testa\", \"firstName\": \"Testa\", \"lastName\": \"Testesen\", \"email\": \"testa@organisationa.dk\", \"credentials\": [ { \"type\": \"password\", \"value\": \"Test1234\" } ], \"enabled\": true}" -H "Authorization: bearer $token" -X POST --header "Content-Type: application/json" -w "%{http_code}" http://nginx:8787/auth/admin/realms/oiosaml-organisation-a/users);
#if [ $code -ne 201 ]; then
#    echo "Could not create test user, got statuscode $code back. Failing ..."
 #   exit 1
#fi


echo "Creating test user in organisation-b realm ..."
code=$(curl -s -d "{\"username\": \"testb\", \"firstName\": \"Testb\", \"lastName\": \"Karlsen\", \"email\": \"testb@organisationb.dk\", \"credentials\": [ { \"type\": \"password\", \"value\": \"Test1234\" } ], \"enabled\": true}" -H "Authorization: bearer $token" -X POST --header "Content-Type: application/json" -w "%{http_code}" http://nginx:8787/auth/admin/realms/oiosaml-organisation-b/users);
#if [ $code -ne 201 ]; then
  #  echo "Could not create test user, got statuscode $code back. Failing ..."
##    exit 1
#fi
