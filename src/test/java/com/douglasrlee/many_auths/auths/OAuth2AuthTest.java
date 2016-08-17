package com.douglasrlee.many_auths.auths;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.javalite.test.jspec.JSpec.the;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.douglasrlee.many_auths.ManyAuths;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OAuth2Auth.class)
@PowerMockIgnore("javax.net.ssl.*")
public class OAuth2AuthTest {
  private ManyAuths manyAuths                   = null;
  private ManyAuths manyAuthsWithProfile        = null;
  private HttpServletRequest httpServletRequest = null;
  private static WireMockServer wireMockServer  = null;

  @BeforeClass
  public static void beforeAll() {
    wireMockServer = new WireMockServer(wireMockConfig().port(3000));
    wireMockServer.start();
  }

  @Before
  public void before() {
    manyAuths = new OAuth2Auth("http://localhost:3000/authorize",
                               "http://localhost:3000/token",
                               "my-client-id",
                               "my-client-secret",
                               "http://example.com/callback");

    manyAuthsWithProfile = new OAuth2Auth("http://localhost:3000/authorize",
                                          "http://localhost:3000/token",
                                          "my-client-id",
                                          "my-client-secret",
                                          "http://example.com/callback",
                                          "http://localhost:3000/me");

    httpServletRequest = mock(HttpServletRequest.class);
  }

  @After
  public void after() {
    wireMockServer.resetRequests();
  }

  @AfterClass
  public static void afterAll() {
    wireMockServer.stop();
  }

  @Test
  public void authenticate() throws UnsupportedEncodingException {
    the(manyAuths.authenticate()).shouldBeEqual(
        manyAuths.authorizationURL
          + "?client_id=" + URLEncoder.encode(manyAuths.clientID, "UTF-8")
          + "&redirect_uri=" + URLEncoder.encode(manyAuths.callbackURL, "UTF-8")
          + "&response_type=code"
    );

    the(manyAuthsWithProfile.authenticate()).shouldBeEqual(
        manyAuthsWithProfile.authorizationURL
          + "?client_id=" + URLEncoder.encode(manyAuthsWithProfile.clientID, "UTF-8")
          + "&redirect_uri=" + URLEncoder.encode(manyAuthsWithProfile.callbackURL, "UTF-8")
          + "&response_type=code"
    );
  }

  @Test(expected = RuntimeException.class)
  public void authenticateException() throws Exception {
    PowerMockito.mockStatic(URLEncoder.class);
    PowerMockito.when(URLEncoder.encode(Mockito.anyString(), Mockito.anyString())).thenThrow(new UnsupportedEncodingException());

    manyAuths.authenticate();
  }

  @Test
  public void authenticateWithRequest() {
    when(httpServletRequest.getParameter("code")).thenReturn("my-code");

    WireMock.configureFor(3000);

    JSONObject wireMockResponse = new JSONObject();
    wireMockResponse.put("access_token", "my-token");
    wireMockResponse.put("expires_in", 3000);
    wireMockResponse.put("refresh_token", "my-refresh_token");
    wireMockResponse.put("token_type", "Bearer");

    stubFor(post(urlEqualTo("/token"))
        .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(wireMockResponse.toString())));

    JSONObject response = manyAuths.authenticate(httpServletRequest);

    the(response).shouldContain("access_token");
    the(response.get("access_token")).shouldBeEqual("my-token");
    the(response).shouldContain("expires_in");
    the(response.get("expires_in")).shouldBeEqual(3000);
    the(response).shouldContain("refresh_token");
    the(response.get("refresh_token")).shouldBeEqual("my-refresh_token");
    the(response).shouldContain("token_type");
    the(response.get("token_type")).shouldBeEqual("Bearer");
  }

  @Test(expected = RuntimeException.class)
  public void authenticateWithRequestException() throws Exception {
    when(httpServletRequest.getParameter("code")).thenReturn("my-code");

    PowerMockito.mockStatic(URLEncoder.class);
    PowerMockito.when(URLEncoder.encode(Mockito.anyString(), Mockito.anyString())).thenThrow(new UnsupportedEncodingException());

    manyAuths.authenticate(httpServletRequest);
  }

  @Test
  public void authenticateWithRequestAndBoolean() {
    when(httpServletRequest.getParameter("code")).thenReturn("my-code");

    WireMock.configureFor(3000);

    JSONObject wireMockResponse = new JSONObject();
    wireMockResponse.put("access_token", "my-token");
    wireMockResponse.put("expires_in", 3000);
    wireMockResponse.put("refresh_token", "my-refresh_token");
    wireMockResponse.put("token_type", "Bearer");

    stubFor(post(urlEqualTo("/token"))
        .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(wireMockResponse.toString())));

    JSONObject response = manyAuthsWithProfile.authenticate(httpServletRequest, false);

    the(response).shouldContain("access_token");
    the(response.get("access_token")).shouldBeEqual("my-token");
    the(response).shouldContain("expires_in");
    the(response.get("expires_in")).shouldBeEqual(3000);
    the(response).shouldContain("refresh_token");
    the(response.get("refresh_token")).shouldBeEqual("my-refresh_token");
    the(response).shouldContain("token_type");
    the(response.get("token_type")).shouldBeEqual("Bearer");

    wireMockResponse = new JSONObject();
    wireMockResponse.put("id", 1);
    wireMockResponse.put("first_name", "John");
    wireMockResponse.put("last_name", "Doe");

    stubFor(get(urlEqualTo("/me"))
        .withHeader("Authorization", equalTo("Bearer my-token"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(wireMockResponse.toString())));

    response = manyAuthsWithProfile.authenticate(httpServletRequest, true);

    the(response).shouldContain("id");
    the(response.get("id")).shouldBeEqual(1);
    the(response).shouldContain("first_name");
    the(response.get("first_name")).shouldBeEqual("John");
    the(response).shouldContain("last_name");
    the(response.get("last_name")).shouldBeEqual("Doe");
  }

  @Test(expected = RuntimeException.class)
  public void authenticateWithRequestAndBooleanException() throws Exception {
    when(httpServletRequest.getParameter("code")).thenReturn("my-code");

    PowerMockito.mockStatic(URLEncoder.class);
    PowerMockito.when(URLEncoder.encode(Mockito.anyString(), Mockito.anyString())).thenThrow(new UnsupportedEncodingException());

    manyAuths.authenticate(httpServletRequest, true);
  }
}