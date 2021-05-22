// package edu.wpi.teamA.modules;
//
// import com.sondehealth.authentication.AccessToken;
// import com.sondehealth.authentication.SondeCredentialsService;
// import com.sondehealth.exceptions.SDKClientException;
// import com.sondehealth.exceptions.SDKUnauthorizedException;
// import com.sondehealth.exceptions.SondeServiceException;
// import com.sondehealth.factory.SondeHealthClientProvider;
// import com.sondehealth.model.*;
// import com.sondehealth.service.StorageClient;
// import com.sondehealth.service.UserClient;
// import java.io.*;
// import java.net.URL;
// import java.net.URLConnection;
// import java.nio.charset.StandardCharsets;
// import java.util.Arrays;
// import java.util.List;
// import javax.net.ssl.HttpsURLConnection;
// import org.json.JSONException;
// import org.json.JSONObject;
//
// public class SondeAPI {
//
//  private final String CLIENT_ID = "1cm641m9v8urioqac9qffora98";
//  private final String CLIENT_SECRET = "fqu5kv34eogr4974f450s0c99dum0ccdp6b5jget53bu980lus8";
//  private final String USER_ID = "73494e571";
//
//  public SondeAPI() {
//    uploadFile();
//  }
//
//  List<Scopes> scopeList =
//      Arrays.asList(
//          Scopes.STORAGE_WRITE, Scopes.SCORES_WRITE, Scopes.MEASURES_READ, Scopes.MEASURES_LIST);
//
//  public void uploadFile() {
//    SondeCredentialsService credentialsService =
//        SondeHealthClientProvider.getClientCredentialsAuthProvider(CLIENT_ID, CLIENT_SECRET);
//    AccessToken token = null;
//    try {
//      token = credentialsService.generateAccessToken(scopeList);
//      String accessToken =
//          token.getAccessToken(); // This line will get the accessToken with the requested scopes\
//      token.setScopes(
//          "sonde-platform/users.write sonde-platform/scores.write
// sonde-platform/questionnaires.read sonde-platform/questionnaire-responses.write
// sonde-platform/storage.write");
//      accessToken = token.getAccessToken();
//
//      UserClient userClient = SondeHealthClientProvider.getUserClient(credentialsService);
//      UserCreationRequest request =
//          new UserCreationRequest.UserBuilder(Gender.MALE, "1991")
//              .withLanguage("ENGLISH")
//              .build(); // language is optional
//
//      UserCreationResponse response;
//      //      userClient = SondeHealthClientProvider.getUserClient(credentialsService);
//      //      response = userClient.createUser(request);
//      //      System.out.println(response.getUserIdentifier());
//
//      StorageClient client = SondeHealthClientProvider.getStorageClient(credentialsService);
//      FileUploadRequest fileUploadRequest =
//          new FileUploadRequest(
//              Country.UNITED_STATES,
//              USER_ID,
//              this.getClass().getResource("/edu/wpi/teamA/assets/sounds/exAhh.wav").getPath(),
//              FileType.WAV);
//      FileUploadResponse fileResponse = client.uploadFile(fileUploadRequest);
//
//      System.out.println("UPLOADED FILE?");
//      System.out.println(fileResponse.getFilePath());
//
//      //      String data =
//      //          "{\n"
//      //              + "  \"questionnaire\": {\n"
//      //              + "    \"id\": \"qnr_e23er432w\",\n"
//      //              + "    \"language\": \"en\",\n"
//      //              + "    \"userIdentifier\": \""
//      //              + USER_ID
//      //              + "\",\n"
//      //              + "    \"respondedAt\": \"2020-08-17T07:11:02Z\",\n"
//      //              + "    \"questionResponses\": [\n"
//      //              + "      {\n"
//      //              + "        \"optionIndex\": 1\n"
//      //              + "      },\n"
//      //              + "      {\n"
//      //              + "        \"optionIndexes\": [\n"
//      //              + "          0,\n"
//      //              + "          2\n"
//      //              + "        ]\n"
//      //              + "      },\n"
//      //              + "      {\n"
//      //              + "        \"isSkipped\": true\n"
//      //              + "      },\n"
//      //              + "      {\n"
//      //              + "        \"response\": \"104F\"\n"
//      //              + "      }\n"
//      //              + "    ]\n"
//      //              + "  }\n"
//      //              + "}";
//      //
//      //      JSONObject json =
//      //          readJsonFromUrl(
//      //              "https://api.stage.sondeservices.com/platform/v1/questionnaire-responses",
//      //              accessToken,
//      //              data);
//
//      String data2 =
//          "{\n"
//              + "  \"userIdentifier\": \""
//              + USER_ID
//              + "\",\n"
//              + "  \"filePath\": \""
//              + fileResponse.getFilePath()
//              + "\",\n"
//              + "  \"questionnaireResponseId\": \"qrs_67ytu89iu\",\n"
//              + "  \"measureName\": \"respiratory-symptoms-risk\",\n"
//              + "}";
//
//      System.out.println(token.getAccessToken());
//
//      JSONObject json2 =
//          readJsonFromUrl(
//              "https://api.stage.sondeservices.com/platform/v1/inference/scores",
//              accessToken,
//              data2);
//      System.out.println("SCORE: " + json2.getInt("score"));
//
//    } catch (SondeServiceException
//        | SDKClientException
//        | SDKUnauthorizedException
//        | IOException ex) {
//      // Exception handling code
//    }
//  }
//
//  private static String readAll(Reader rd) throws IOException {
//    StringBuilder sb = new StringBuilder();
//    int cp;
//    while ((cp = rd.read()) != -1) {
//      sb.append((char) cp);
//    }
//    return sb.toString();
//  }
//
//  public static JSONObject readJsonFromUrl(String spec, String token, String data)
//      throws IOException, JSONException {
//    System.out.println(1);
//    URL url = new URL(spec);
//    URLConnection con = url.openConnection();
//    System.out.println(2);
//    HttpsURLConnection http = (HttpsURLConnection) con;
//    http.setRequestMethod("POST"); // PUT is another valid option
//    http.setDoOutput(true);
//    http.setConnectTimeout(10);
//    http.setReadTimeout(10);
//    System.out.println(3);
//
//    byte[] out = data.getBytes();
//    int length = out.length;
//
//    System.out.println(4);
//    // http.setFixedLengthStreamingMode(length);
//    http.setRequestProperty("Content-Type", "application/json");
//    http.setRequestProperty("Authorization", token);
//    System.out.println(5);
//
//    System.out.println(5.1);
//    http.getOutputStream().write(out);
//    System.out.println(6);
//
//    BufferedReader rd = null;
//
//    if (http.getResponseCode() == 200) {
//      rd = new BufferedReader(new InputStreamReader(http.getInputStream()));
//    } else {
//      rd = new BufferedReader(new InputStreamReader(http.getErrorStream()));
//    }
//
//    StringBuilder result = new StringBuilder();
//
//    String line;
//    while ((line = rd.readLine()) != null) {
//      result.append(line);
//    }
//    rd.close();
//
//    System.out.println(result.toString());
//    // Do something with http.getInputStream()
//    InputStream is = http.getInputStream();
//    System.out.println(7);
//    try {
//      BufferedReader redr = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
//      String jsonText = readAll(redr);
//      JSONObject json = new JSONObject(jsonText);
//      return json;
//    } finally {
//      is.close();
//    }
//  }
// }
