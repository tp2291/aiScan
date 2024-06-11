package com.cisco.wxcc.saa.abo.utils;

import com.cisco.wxcc.saa.abo.exceptions.ConfigurationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasongoodwin.monads.Try;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CatalogUtils {

  private CatalogUtils() {}

  public static Try<String> getCatalogConfiguration(String catalogUrlStr) {

    return Try.ofFailable(
            () -> {

                try {
                  URL catalogUrl =
                          new URL(catalogUrlStr);
                  return makeHttpCall(catalogUrl).get();
                } catch (Exception e) {
                  throw new ConfigurationException(
                          "Exception while getting the catalog configuration : " + e);
                }
            }
    ).recoverWith(Try::failure);
  }

  public static Try<String> makeHttpCall(URL catalogUrl)  {
    return Try.ofFailable(
            () -> {
              HttpURLConnection connection = (HttpURLConnection) catalogUrl.openConnection();
              connection.setRequestMethod("GET");
              connection.setRequestProperty("Accept", "application/json");

              if (connection.getResponseCode() != 200) {
                throw new ConfigurationException(
                        "Failed catalog service make api call : HTTP error code : "
                                + connection.getResponseCode());
              }
              BufferedReader bufferedReader =
                      new BufferedReader(new InputStreamReader((connection.getInputStream())));
              String output;
              StringBuilder schemaStringBuilder = new StringBuilder();
              while ((output = bufferedReader.readLine()) != null) {
                schemaStringBuilder.append(output);
              }
              connection.disconnect();
              return schemaStringBuilder.toString();
            }
    ).recoverWith(throwable -> Try.failure(new ConfigurationException("Failed catalog service make api call : " ,throwable)));
  }

    public static String getValFromJson(String jsonString, String keyPath)
            throws ConfigurationException {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonTreeObj = mapper.readTree(jsonString);
                String keyVal = jsonTreeObj.at(keyPath).asText().trim();
                if (keyVal.isBlank()) {
                    throw new ConfigurationException(
                            "Exception in getValFromJson. Empty or null value for the key ");
                }

                return keyVal;

            } catch (Exception e) {
                throw new ConfigurationException(
                        "Exception while getting the key from json. key-path : "
                                + keyPath
                                + jsonString);
            }
    }
}
