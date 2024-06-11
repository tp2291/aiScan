package com.cisco.wxcc.saa.integration.auth;

import com.cisco.wxcc.saa.exceptions.AuthClientException;
import com.cisco.wxcc.saa.pojo.OAuthTokenInfo;
import com.cisco.wxcc.saa.utils.ConfigHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import static com.cisco.wxcc.saa.App.agentId;
import static com.cisco.wxcc.saa.App.interactionId;
import static com.cisco.wxcc.saa.constants.AppConstants.*;

@Slf4j
public class OAuthTokenHandler {

  private static final HttpClient client = HttpClient.newHttpClient();

  private static Configuration appProps;

  static {
    try {
      appProps = ConfigHelper.loadAppConfig();
    } catch (org.apache.commons.configuration2.ex.ConfigurationException e) {
      log.info("Unable to load configs : "+ e, Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId), Markers.append(KIBANA_AGENT_ID_FIELD_NAME, agentId));
    }
  }

  private final ConcurrentHashMap<String, Optional<OAuthTokenInfo>> tokenInfoMap =
          new ConcurrentHashMap<>();
  private final Predicate<String> hasTokenExpired =
          key -> {
            if (tokenInfoMap.containsKey(key) && tokenInfoMap.get(key).isPresent()) {
              Optional<OAuthTokenInfo> tokenInfo = tokenInfoMap.get(key);
              long minutesToExpire =
                      ChronoUnit.MINUTES.between(
                              LocalDateTime.now(Clock.systemUTC()),
                              LocalDateTime.ofEpochSecond(
                                      tokenInfo.get().getAccessTokenValidUntil() / 1000, 0, ZoneOffset.UTC));
              return minutesToExpire <= TOKEN_CACHE_EXPIRY_IN_MIN;
            }
            return true;
          };

  private final Predicate<String> hasRefreshTokenExpired =
          key -> {
            if (tokenInfoMap.containsKey(key) && tokenInfoMap.get(key).isPresent()) {
              Optional<OAuthTokenInfo> tokenInfo = tokenInfoMap.get(key);
              long minutesToExpire =
                      ChronoUnit.MINUTES.between(
                              LocalDateTime.now(Clock.systemUTC()),
                              LocalDateTime.ofEpochSecond(
                                      tokenInfo.get().getRefreshTokenValidUntil() / 1000, 0, ZoneOffset.UTC));
              return minutesToExpire <= TOKEN_CACHE_EXPIRY_IN_MIN;
            }
            return true;
          };


  public Optional<OAuthTokenInfo> getAccessToken(
          String name, String password, String orgId, String scope)
          throws AuthClientException, IOException, InterruptedException {
    final String key = name + "-" + scope;
    if (hasTokenExpired.test(key)) {
      log.warn("Access Token Null or Expired", Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId), Markers.append(KIBANA_AGENT_ID_FIELD_NAME, agentId));
      if(hasRefreshTokenExpired.test(key)){
        log.warn("Refresh Token Null or expired. Creating new Access Token!", Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId), Markers.append(KIBANA_AGENT_ID_FIELD_NAME, agentId));
        String assertion = getBearerToken(name, password, orgId);
        Optional<OAuthTokenInfo> tokenInfo =
                getAccessTokenBySAMLAssertion(assertion, scope);
        tokenInfoMap.put(key, tokenInfo);
      }
      else{
        log.warn("Refresh Token not expired. Creating new access token using refresh token.", Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId), Markers.append(KIBANA_AGENT_ID_FIELD_NAME, agentId));
        Optional<OAuthTokenInfo> tokenInfo = getAccessTokenByRefreshToken(tokenInfoMap.get(key).get().getRefreshToken() , scope);
        tokenInfoMap.put(key, tokenInfo);
      }
    }
    return tokenInfoMap.get(key);
  }

  private String getBearerToken(String name, String password, String orgId)
          throws AuthClientException, IOException, InterruptedException {
    final String body = createBody(name, password);
    String bearerToken;

    String url = String.format("%s/token/%s/v2/actions/GetBearerToken/invoke",
            appProps.getString(BROKER_URL),
            orgId);

    HttpRequest codeRequest = HttpRequest.newBuilder()
            .header("content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .uri(URI.create(url))
            .build();
    HttpResponse<String> response = client.send(codeRequest, HttpResponse.BodyHandlers.ofString());
    final Map<String, Object> responseMap = parseResponse(response.body());
    bearerToken = findBearerTokenInfo(responseMap);
    return bearerToken;
  }

  private Optional<OAuthTokenInfo> getAccessTokenBySAMLAssertion(
          final String assertion, final String scope)
          throws AuthClientException, IOException, InterruptedException {
    final String params =
            createSAMLAssertionParams(assertion, scope);
    String clientToken = Base64.encodeBase64String(
            (appProps.getString(CLIENT_ID) + ":" + appProps.getString(CLIENT_SECRET)).getBytes()
    );

    String url = String.format("%s/oauth2/v1/access_token",
            appProps.getString(BROKER_URL));

    HttpRequest codeRequest = HttpRequest.newBuilder()
            .header("content-type", "application/x-www-form-urlencoded")
            .header("Authorization", "Basic " + clientToken)
            .POST(HttpRequest.BodyPublishers.ofString(params))
            .uri(URI.create(url))
            .build();

    HttpResponse<String> response = client.send(codeRequest, HttpResponse.BodyHandlers.ofString());
    final Map<String, Object> responseMap = parseResponse(response.body());
    return Optional.of(createTokenInfo(responseMap));
  }

  private Optional<OAuthTokenInfo> getAccessTokenByRefreshToken(
          final String refreshToken, final String scope)
          throws AuthClientException, IOException, InterruptedException {
    final String params =
            createRefreshTokenParams(refreshToken, scope);
    String clientToken = Base64.encodeBase64String(
            (appProps.getString(CLIENT_ID) + ":" + appProps.getString(CLIENT_SECRET)).getBytes()
    );

    String url = String.format("%s/oauth2/v1/access_token",
            appProps.getString(BROKER_URL));

    HttpRequest codeRequest = HttpRequest.newBuilder()
            .header("content-type", "application/x-www-form-urlencoded")
            .header("Authorization", "Basic " + clientToken)
            .POST(HttpRequest.BodyPublishers.ofString(params))
            .uri(URI.create(url))
            .build();

    HttpResponse<String> response = client.send(codeRequest, HttpResponse.BodyHandlers.ofString());
    final Map<String, Object> responseMap = parseResponse(response.body());
    return Optional.of(createTokenInfo(responseMap));
  }

  private String createBody(final String name, final String password) {
    final Map<String, Object> dataMap = new HashMap<>();
    dataMap.put(NAME, name);
    dataMap.put(SERVICE_SECRET, password);
    return new Gson().toJson(dataMap);
  }

  private Map<String, Object> parseResponse(final String tokenResponse) {
    Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
    return new Gson().fromJson(tokenResponse, mapType);
  }

  private String findBearerTokenInfo(final Map<String, Object> dataMap) throws AuthClientException {
    if (MapUtils.isEmpty(dataMap) || !dataMap.containsKey(BEARER_TOKEN)) {
      throw new AuthClientException("BearerToken not obtained!");
    }
    return String.valueOf(dataMap.get(BEARER_TOKEN));
  }

  private String createSAMLAssertionParams(
          final String assertion, final String scope) {

    Map<String, String> formData = new HashMap<>();
    formData.put(REQ_GRANT_TYPE, GRANT_TYPE_SAML2_BEARER);
    formData.put(REQ_SCOPE, scope);
    formData.put(ASSERTION, assertion);
    return getFormDataAsString(formData);
  }

  private String createRefreshTokenParams(
          final String refreshToken, final String scope) {

    Map<String, String> formData = new HashMap<>();
    formData.put(REQ_GRANT_TYPE, GRANT_TYPE_REFRESH_TOKEN);
    formData.put(REQ_SCOPE, scope);
    formData.put(REFRESH_TOKEN, refreshToken);
    return getFormDataAsString(formData);
  }

  private static @NotNull String getFormDataAsString(Map<String, String> formData) {
    StringBuilder formBodyBuilder = new StringBuilder();
    for (Map.Entry<String, String> singleEntry : formData.entrySet()) {
      if (formBodyBuilder.length() > 0) {
        formBodyBuilder.append("&");
      }
      formBodyBuilder.append(URLEncoder.encode(singleEntry.getKey(), StandardCharsets.UTF_8));
      formBodyBuilder.append("=");
      formBodyBuilder.append(URLEncoder.encode(singleEntry.getValue(), StandardCharsets.UTF_8));
    }
    return formBodyBuilder.toString();
  }

  private OAuthTokenInfo createTokenInfo(final Map<String, Object> dataMap)
          throws AuthClientException {
    if (MapUtils.isEmpty(dataMap)) {
      throw new AuthClientException("Access token not obtained!");
    }
    final long expiresIn =
            NumberUtils.toLong(
                    StringUtils.substringBefore(String.valueOf(dataMap.get(EXPIRES_IN)), "."));
    final String tokenType = String.valueOf(dataMap.get(TOKEN_TYPE));
    final String accessToken = String.valueOf(dataMap.get(ACCESS_TOKEN));
    final String latestRefreshToken = String.valueOf(dataMap.get(REFRESH_TOKEN));
    final long refreshTokenExpiresIn =
            NumberUtils.toLong(
                    StringUtils.substringBefore(
                            String.valueOf(dataMap.get(REFRESH_TOKEN_EXPIRES_IN)), "."));
    final String scope = String.valueOf(dataMap.get(REQ_SCOPE));
    final int accountExpiration =
            NumberUtils.toInt(
                    StringUtils.substringBefore(String.valueOf(dataMap.get(ACCOUNT_EXPIRATION)), "."));
    final long accessTokenValidUntil = System.currentTimeMillis() + (expiresIn * 1000L);
    final long refreshTokenValidUntil = System.currentTimeMillis() + (refreshTokenExpiresIn * 1000L);
    final String trackingId = String.valueOf(dataMap.get(TRACKING_ID));

    // create token
    final OAuthTokenInfo tokenInfo = new OAuthTokenInfo();
    tokenInfo.setAccessToken(accessToken);
    tokenInfo.setExpiresIn(expiresIn);
    tokenInfo.setRefreshToken(latestRefreshToken);
    tokenInfo.setRefreshTokenExpiresIn(refreshTokenExpiresIn);
    tokenInfo.setTokenType(tokenType);
    tokenInfo.setScope(scope);
    tokenInfo.setAccountExpiration(accountExpiration);
    tokenInfo.setAccessTokenValidUntil(accessTokenValidUntil);
    tokenInfo.setRefreshTokenValidUntil(refreshTokenValidUntil);
    tokenInfo.setTrackingId(trackingId);
    return tokenInfo;
  }

  public boolean hasTokenExpired(String key) {
    return hasTokenExpired.test(key);
  }
}

