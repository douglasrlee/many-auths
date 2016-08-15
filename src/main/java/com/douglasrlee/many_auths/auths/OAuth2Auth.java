package com.douglasrlee.many_auths.auths;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.javalite.http.Get;
import org.javalite.http.Http;
import org.javalite.http.Post;
import org.json.JSONObject;

import com.douglasrlee.many_auths.ManyAuths;

public class OAuth2Auth extends ManyAuths {
  public OAuth2Auth(String authorizationURL,
                    String tokenURL,
                    String clientID,
                    String clientSecret,
                    String callbackURL) {
    this.authorizationURL = authorizationURL;
    this.tokenURL = tokenURL;
    this.clientID = clientID;
    this.clientSecret = clientSecret;
    this.callbackURL = callbackURL;
  }

  public OAuth2Auth(String authorizationURL,
                    String tokenURL,
                    String clientID,
                    String clientSecret,
                    String callbackURL,
                    String profileURL) {
    this.authorizationURL = authorizationURL;
    this.tokenURL = tokenURL;
    this.clientID = clientID;
    this.clientSecret = clientSecret;
    this.callbackURL = callbackURL;
    this.profileURL = profileURL;
  }

  public String authenticate() {
    String authorizationURL = this.authorizationURL;
    String parameters;
    try {
      parameters = "?client_id=" + URLEncoder.encode(clientID, "UTF-8")
                 + "&redirect_uri=" + URLEncoder.encode(callbackURL, "UTF-8")
                 + "&response_type=code";
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new RuntimeException(unsupportedEncodingException);
    }

    return authorizationURL + parameters;
  }

  public JSONObject authenticate(HttpServletRequest request, boolean returnProfile) {
    String code = request.getParameter("code");
    String requestBody;
    try {
      requestBody = "client_id=" + URLEncoder.encode(this.clientID, "UTF-8")
                  + "&client_secret=" + URLEncoder.encode(clientSecret, "UTF-8")
                  + "&redirect_uri=" + URLEncoder.encode(this.callbackURL, "UTF-8")
                  + "&code=" + URLEncoder.encode(code, "UTF-8")
                  + "&grant_type=authorization_code";
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new RuntimeException(unsupportedEncodingException);
    }

    Post post = Http.post(this.tokenURL, requestBody).header("Content-Type", "application/x-www-form-urlencoded");
    JSONObject response = new JSONObject(post.text());

    if(returnProfile) {
      Get get = Http.get(this.profileURL).header("Authorization", "Bearer " + response.getString("access_token"));

      response = new JSONObject(get.text());
    }

    return response;
  }
}