# Many Auths

[ ![Codeship Status for douglasrlee/many-auths](https://codeship.com/projects/3a4a8cf0-4601-0134-f1f6-4abc7e84eef0/status?branch=master)](https://codeship.com/projects/168796)
[![Coverage Status](https://coveralls.io/repos/github/douglasrlee/many-auths/badge.png?branch=master)](https://coveralls.io/github/douglasrlee/many-auths?branch=master)

Many Auths is designed to make authentication requests in java easier.

Many Auths will take your client information and your authorization server information and build the redirect request to the authorization endpoint. Then in your callback controller it will take the code and then retrieve the token.

## OAuth2Auth Usage
OAuth2Auth follows the [Authorization Code Grant](https://tools.ietf.org/html/rfc6749#section-4.1).
##### Initialization
Standard Initialization
```java
ManyAuths manyAuths = new OAuth2Auth(authorizationURL,
                                     tokenURL,
                                     clientID,
                                     clientSecret,
                                     callbackURL);
```
Initialization with Profile Endpoint
```java
ManyAuths manyAuths = new OAuth2Auth(authorizationURL,
                                     tokenURL,
                                     clientID,
                                     clientSecret,
                                     callbackURL,
                                     profileURL);
```
Spring Bean Initialization
```java
@Bean
public ManyAuths manyAuths() {
  return new OAuth2Auth(authorizationURL,
                        tokenURL,
                        clientID,
                        clientSecret,
                        callbackURL);
}
```
##### Authentication
The `authenticate()` method will simply return the redirect URL that you will need to redirect to to start the authentication flow.
```java
return "redirect:" + manyAuths.authenticate();
```

In your callback controller you will use the `authenticate(request)` method to get the code provided to you and call the token endpoint.
```java
return manyAuths.authenticate(request).toString();
```
The response from this method will be a `JSONObject` of the token response.

If you want to return the profile data instead of the token response then you simply call the `authenticate(request, true)` method.
```java
return manyAuths.authenticate(request, true).toString();
```

## Maven Dependency
```
<dependency>
    <groupId>com.douglasrlee</groupId>
    <artifactId>many-auths</artifactId>
    <version>LATEST</version>
</dependency>
```