Simple playground application using Spring Security with OAuth2 and JWT to
* secure a resource with OAuth2
* provide an endpoint to supply JWT access tokens to the resource

Usage:

* run with '$ gradle bootRun'
* try to access resource w/o authorization, which will fail: '$ curl localhost:8080/foobar'
* request OAuth2 access token by supplying user credentials:
    '$ curl -X 'POST'
        -u my_client_username:my_client_password
        --data "username=hdampf
            &password=wert123$
            &client_id=my_client_username
            &grant_type=password
            &scope=foobar_scope"
        localhost:8080/oauth/token'
* the endpoint replies with an access token, which is a Json Web Token (JWT) provided as a base64-encoded string
* go to http://jwt.io/ and paste the token there to see its contents. the _secret_ used is "foobar" (as set in AuthorizationServer)
* access resource with access token: '$ curl -H "Authorization: Bearer $TOKEN" localhost:8080/foobar'

