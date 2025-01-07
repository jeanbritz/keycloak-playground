# Keycloak Playground

## Technologies used
- Java 21+
- [Keycloak](https://www.keycloak.org/)
- [Jersey Framework (using Jakarta RESTful Web Services API 3.1.0)](https://eclipse-ee4j.github.io/jersey/)
- [Jakarta Servlet 6.1](https://jakarta.ee/specifications/servlet/6.1/)
- [HK2 Dependency Injection Framework](https://javaee.github.io/hk2/api-overview.html)
- [Jetty Embedded](https://jetty.org/index.html)
- [Nimbus OAuth2 OIDC SDK](https://connect2id.com/products/nimbus-oauth-openid-connect-sdk)

## Notable specifications
- [RFC 6749 - OAuth 2.0 Authorization Framework](https://datatracker.ietf.org/doc/html/rfc6749)
- [RFC 8414 - OAuth 2.0 Authorization Server Metadata](https://datatracker.ietf.org/doc/html/rfc8414)
- [RFC 8693 - OAuth 2.0 Token Exchange](https://datatracker.ietf.org/doc/html/rfc8693)
- [OpenID Connect Core 1.0](https://openid.net/specs/openid-connect-core-1_0.html)

## OIDC Implemented Flows

### Initial OIDC Flow
![Initial OIDC Flow](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/jeanbritz/keycloak-playground/refs/heads/main/docs/uml/initial-oidc-flow.plantuml)