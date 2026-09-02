// package com.datn.project.service;

// import okhttp3.*;
// import org.json.JSONArray;
// import org.json.JSONObject;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;

// import java.io.IOException;

// @Service
// public class BrevoMailService {

//     private final OkHttpClient client = new OkHttpClient();
//     private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

//     @Value("${brevo-api-key}")
//     private String apiKey;

//     @Value("${brevo-sender-email}")
//     private String senderEmail;

//     @Value("${brevo-sender-name}")
//     private String senderName;

//     public void send(String to, String subject, String htmlContent) {
//         JSONObject body = new JSONObject();

//         JSONObject sender = new JSONObject();
//         sender.put("name", senderName);
//         sender.put("email", senderEmail);
//         body.put("sender", sender);

//         JSONArray toArray = new JSONArray();
//         JSONObject recipient = new JSONObject();
//         recipient.put("email", to);
//         toArray.put(recipient);
//         body.put("to", toArray);

//         body.put("subject", subject);
//         body.put("htmlContent", htmlContent);

//         RequestBody requestBody = RequestBody.create(
//                 body.toString(), MediaType.parse("application/json"));

//         Request request = new Request.Builder()
//                 .url(BREVO_API_URL)
//                 .post(requestBody)
//                 .addHeader("api-key", apiKey)
//                 .addHeader("Content-Type", "application/json")
//                 .addHeader("Accept", "application/json")
//                 .build();

//         try (Response response = client.newCall(request).execute()) {
//             if (!response.isSuccessful()) {
//                 String errorBody = response.body() != null ? response.body().string() : "no body";
//                 throw new RuntimeException("Gửi email thất bại: " + response.code() + " - " + errorBody);
//             }
//         } catch (IOException e) {
//             throw new RuntimeException("Gửi email thất bại: " + e.getMessage(), e);
//         }
//     }
// }