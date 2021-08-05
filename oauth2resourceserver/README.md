# OAuth2.0 Resource Server

This is a dummy authorization server intended for _"securing"_ my Car Service API.

The server is very simple, thus exposing only two endpoints:

- TOKEN ENDPOINT - used to obtain the [JWT-encoded access token](https://en.wikipedia.org/wiki/JSON_Web_Token) that will be used to access the secured Car Service API
- JWK SET ENDPOINT: used to obtain the public key (in [JWK format](https://openid.net/specs/draft-jones-json-web-key-03.html)) that should be used to verify the signature of the
  access token obtained from the token endpoint

Note that the key used for signing the access token is created automatically during server startup.

## Usage

By default, the server listens for HTTP traffic on port `8888`.

### Obtaining the Access Token

To obtain the access token from the token endpoint, use the following request:

```shell
$ curl -s http://localhost:8888/token
{
    "access_token": "eyJraWQiOiJ2ZEVsdDRSYnZ2bDNlQ0sxVXRISkxFS01Xd2doN0pRWlJGTHBrRDRaRTRjIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJBbGljZSIsImF1ZCI6Imh0dHBzOlwvXC9naXRodWIuY29tXC9zb3VuZHdhbGsiLCJpc3MiOiJodHRwczpcL1wvZ2l0aHViLmNvbVwvc291bmR3YWxrIiwiZXhwIjoxNjI4MTA3MTcxLCJpYXQiOjE2MjgxMDY1NzEsImp0aSI6Imp5ODdGaXNxSXdaV3Z2aGpJRmtEYkllcmU1bjNzbHV4cHJxRVNieHctOVkiLCJjbGllbnRfaWQiOnsidmFsdWUiOiJSem9zU3ZVRFNnVlJzQzdrbEdaVlNHT2hPREsxNXdiS182N0l3OXdBV3o0In19.DkLpDcJT7oPosdyPqSW3nyVi0OWaZZOy14DJrJaasdFq4QbYF-JZRNlxZsrCoWnc4xG1txrD02DVcf7kuIHmLDoXPCf0CXJPq1pZb4C9GGWm1sEtCA4or_trz3HC-gCuwLBUkAQMGKhmDyjhlEY6Ghpqo7djWVMYQOufMP_T3uBNKUybG_RRkq7ZNJ5pSAN4VR0lxi8c_rPiv_cduhEbmev4dp9mpiYtKFhUskUZ8vIJCuHIZ6e1E8XFNj9SYpaI7PORgHdceK3LjG7QZsf8FPpQL_zBjWM1aOogwycSVJJZN64oZXxrkCet9iGY1rM7HtP9_rKlzI-lcoCwR9NRIw",
    "token_type": "Bearer",
    "expires_in": 600
}
```

As previously mentioned, the access token is JWT-encoded. To inspect its claims service like [jwt.io](https://jwt.io/)
can be used. The access token from the previous example has the following header:

```shell
{
  "kid": "vdElt4Rbvvl3eCK1UtHJLEKMWwgh7JQZRFLpkD4ZE4c",
  "alg": "RS256"
}
```

While the payload is:

```shell
{
    "sub": "Alice",
    "aud": "https://github.com/soundwalk",
    "iss": "https://github.com/soundwalk",
    "exp": 1628107171,
    "iat": 1628106571,
    "jti": "jy87FisqIwZWvvhjIFkDbIere5n3sluxprqESbxw-9Y",
    "client_id": {
        "value": "RzosSvUDSgVRsC7klGZVSGOhODK15wbK_67Iw9wAWz4"
    }
}
```

Note that, by default, the issued access token is valid for **10 minutes** and has **no scope** attached to it. If an
access token with a specific scope is required, it can be obtained by specifying the `scope` parameter in the request to
the token endpoint:

```shell
$ curl -s http://localhost:8888/token?scope=ADMIN
{
    "access_token": "eyJraWQiOiJ2ZEVsdDRSYnZ2bDNlQ0sxVXRISkxFS01Xd2doN0pRWlJGTHBrRDRaRTRjIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJCb2IiLCJhdWQiOiJodHRwczpcL1wvZ2l0aHViLmNvbVwvc291bmR3YWxrIiwic2NvcGUiOiJBRE1JTiIsImlzcyI6Imh0dHBzOlwvXC9naXRodWIuY29tXC9zb3VuZHdhbGsiLCJleHAiOjE2MjgxMDc1MDcsImlhdCI6MTYyODEwNjkwNywianRpIjoiMHNYdjZXOW54M2wtVFNrOFd2RFlGNDZDbE9IYVFhTzloUFFVNWVjTTQ5TSIsImNsaWVudF9pZCI6eyJ2YWx1ZSI6Ik01SUxsUXJySThwcElfREhUak0xWDJmVm82T1F1YW1YNXNpTEZTeEx1cWsifX0.XKlNGAv4zS56LdCRayyp3u9ROPkeh5VEtONHuN3bSLQSfPDJRs-zXWfAonSxmyV8lps0Q3HeJsaaRx1oKV7UnJz1wcwdmiweBauFxcOhtqOsdiMnrVx6moPtBUIVBaunxf0YtF2y6rTKEQbTG1izENNF8c5XyE8WFqIvdb42ZCMdzQTrJ6gdGad-LI4nAfkDTII45_-kKqwivwoKmUlpAjl5Hs8-c2Y44ZutItodG8txM_79bQLKfNq5X7966NQDOnqArweeCMj_odqXQ40giIjCAbAB1QpI94tQy9YuiPu9Bk6wP8i8Hv2NHPZYiRIHR9aSmyYW3okFGFOJyeqO4Q",
    "scope": "ADMIN",
    "token_type": "Bearer",
    "expires_in": 600
}
```

In this case, the access token has the following payload:

```shell
{
    "sub": "Bob",
    "aud": "https://github.com/soundwalk",
    "scope": "ADMIN",
    "iss": "https://github.com/soundwalk",
    "exp": 1628107507,
    "iat": 1628106907,
    "jti": "0sXv6W9nx3l-TSk8WvDYF46ClOHaQaO9hPQU5ecM49M",
    "client_id": {
        "value": "M5ILlQrrI8ppI_DHTjM1X2fVo6OQuamX5siLFSxLuqk"
    }
}
```

### Verifying the Access Token Signature

JWT signature should be verified using the appropriate public key. The key currently used for signing access tokens is
available on the JWK set endpoint:

```shell
$ curl -s http://localhost:8888/jwks.json
{
    "keys": [
        {
            "kty": "RSA",
            "e": "AQAB",
            "use": "sig",
            "kid": "vdElt4Rbvvl3eCK1UtHJLEKMWwgh7JQZRFLpkD4ZE4c",
            "alg": "RS256",
            "n": "193L1hptuuSlK8SK0PbonDrNRH8LMLV6t99NRq9_gNatj-P-bjhX1JS80J4XoZkTTccVIo9kWlKyVGQ5pqkhtuB-moR2AsAGJCQTc1v2bzhQ-7Dt9gqmBCJWhi_rZqV9AyOLEhtFGsi1SpJiZlWAFvPfkj6m-fwGCm6ILj81owj4bB5LZMYtzrsC2rgLpNf9jxXSyIbvACKOOZ9t3Nu9R5jq4HFP-krOctMl3jtfsRGe3yJYbbzEYr8dbpLL5Y-LhHjWDAQ68FCgMkw4V4YQNE-GFoVt3IRt5iSOD6rmDtzAn5wcFKA7GZUWhIk-91FtrtVK5sK5MG0dpmLR5aTplw"
        }
    ]
}
```

## Dockerizing :hammer:

Using the Spring Boot's built-in [Paketo Buildpacks](https://paketo.io/):

```shell
$ ./gradlew bootBuildImage --info
```

we are able to generate `soundwalk/oauth2-rs:latest` Docker image.
