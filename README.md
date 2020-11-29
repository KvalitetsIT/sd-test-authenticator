# sd-test-authenticator

## Build

```
mvn clean install
```

## Run
```
cd compose
./run.sh
```

## Stop
```
./stop.sh
```

## Log in
Two clients are exposed:

http://nginx:8787/auth/realms/broker/protocol/openid-connect/auth?client_id=account&redirect_uri=http://nginx:8787/auth/realms/broker/account/login-redirect&state=0/d27e373c-9402-428c-ba24-52476feba201&response_type=code&scope=openid
http://nginx:8787/auth/realms/broker/protocol/openid-connect/auth?client_id=account2&redirect_uri=http://nginx:8787/auth/realms/broker/account/login-redirect&state=0/d27e373c-9402-428c-ba24-52476feba201&response_type=code&scope=openid

These correspond to medarbejdernet- and SD-channels.


To log in, click 'form', and enter one of the following passwords, along with password 'Test1234'

 * test - login succeeds
 * test2 - password invalid
 * test3 - credentials invalid
 * test4 - account disabled
 * test5 - account locked

Logging in roughly corresponds to the 'form' flow.
