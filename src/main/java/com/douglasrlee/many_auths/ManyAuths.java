package com.douglasrlee.many_auths;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

public abstract class ManyAuths {
  public String authorizationURL = null;
  public String tokenURL         = null;
  public String clientID         = null;
  public String clientSecret     = null;
  public String callbackURL      = null;
  public String profileURL       = null;

  /**
   * This will run through the authentication process for whatever auth method you are using
   * and will return a redirect string.
   *
   * @return redirect string (http://mydomain/authorize?thing1=value1&thing2=value2)
   */
  public abstract String authenticate();

  /**
   * This will run through the process of getting a token for whatever auth method you are using
   * and will return a JSON object.
   *
   * @return JSON object with token response
   */
  public JSONObject authenticate(HttpServletRequest request) {
    return authenticate(request, false);
  }

  /**
   * This will run through the process of getting a token for whatever auth method you are using
   * and will return a JSON object for the token response or return a JSON object for the profile
   * response if chosen.
   *
   * @return JSON object with token response or profile response
   */
  public abstract JSONObject authenticate(HttpServletRequest request, boolean returnProfile);
}