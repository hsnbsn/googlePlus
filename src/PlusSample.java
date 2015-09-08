/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusScopes;
import com.google.api.services.plus.model.Person;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;

/**
 * @author Yaniv Inbar
 */
public class PlusSample {
    
  private static final String APPLICATION_NAME = "LookUpUser";
  private static final java.io.File DATA_STORE_DIR = new java.io.File("V:/sample");
  private static FileDataStoreFactory dataStoreFactory;
  private static HttpTransport httpTransport;
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static Plus plus;
  private static Credential authorize() throws Exception {
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
        new InputStreamReader(PlusSample.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println(
          "Enter Client ID and Secret from https://code.google.com/apis/console/?api=plus "
          + "into plus-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JSON_FACTORY, clientSecrets,
        Collections.singleton(PlusScopes.PLUS_ME)).setDataStoreFactory(
        dataStoreFactory).build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }
  public static void main(String[] args) {
    try {
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
      Credential credential = authorize();
      plus = new Plus.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
      getProfile();
      return;
    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }



  /** Get the profile for the authenticated user. */
  private static void getProfile() throws IOException {
  
    Person profile = plus.people().get("110725278286343566549").execute();
    System.out.println(profile.getGender());
    System.out.println(profile.getBirthday());
    System.out.println(profile.getRelationshipStatus());
    System.out.println(profile.getPlacesLived().get(0).getValue());
    System.out.println(profile.getAboutMe());
    System.out.println(profile.getAgeRange());
    System.out.println(profile.getKind());
    System.out.println(profile.getDomain());
    System.out.println(profile.getOccupation());
  }
}